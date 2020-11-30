package tr.com.ogedik.authentication.service.impl;

import org.apache.commons.lang.BooleanUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.ogedik.authentication.entity.UserEntity;
import tr.com.ogedik.authentication.exception.AuthenticationErrorType;
import tr.com.ogedik.authentication.mapper.UserMapper;
import tr.com.ogedik.authentication.model.AuthenticationUser;
import tr.com.ogedik.authentication.persistance.manager.UserPersistenceManager;
import tr.com.ogedik.authentication.service.UserService;
import tr.com.ogedik.authentication.util.AuthenticationUtil;
import tr.com.ogedik.authentication.validation.user.UserValidationFacade;
import tr.com.ogedik.commons.expection.ErrorException;
import tr.com.ogedik.commons.model.JiraSearchUser;
import tr.com.ogedik.commons.model.JiraUser;
import tr.com.ogedik.commons.rest.request.model.EmailRequest;
import tr.com.ogedik.commons.util.MetaUtils;
import tr.com.ogedik.scrumier.proxy.clients.IntegrationProxy;
import tr.com.ogedik.scrumier.proxy.clients.NotificationProxy;

import java.time.LocalDateTime;
import java.util.List;

/** @author orkun.gedik */
@Service
@Transactional
public class UserServiceImpl implements UserService {

  private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

  @Autowired private UserPersistenceManager persistenceManager;
  @Autowired private UserValidationFacade validationFacade;
  @Autowired private UserMapper userMapper;
  @Autowired private IntegrationProxy integrationProxy;
  @Autowired private NotificationProxy notificationProxy;

  @Override
  public List<AuthenticationUser> getAllUsers() {
    return userMapper.convert(persistenceManager.findAll());
  }

  @Override
  public boolean isExist(String username) {
    return persistenceManager.existsByUsername(username);
  }

  @Override
  public AuthenticationUser getUserByUsername(String username) {
    try {
      UserEntity entity = persistenceManager.findByUsername(username);
      logger.info("User entity found from database for username {}.", username);

      return userMapper.convert(entity);
    } catch (IncorrectResultSizeDataAccessException e) {
      logger.error("User entity cannot be retrieved for username {}, error -> {}", username, e);
      throw new ErrorException(
          AuthenticationErrorType.MULTIPLE_USER_FOUND,
          String.format("\"%s\" username is not valid. Please contact with your admin", username));
    }
  }

  @Override
  public AuthenticationUser create(AuthenticationUser user) {
    // validationFacade.validateCreate(user);
    //TODO: validate the creation of user

    JiraUser jiraUser = integrationProxy.getJiraUser(user.getUsername());

    user.setEnrolmentDate(LocalDateTime.now());
    user.setDisplayName(jiraUser.getDisplayName());
    user.setAvatarUrl(jiraUser.getAvatarUrls().get("48x48"));

    UserEntity toBeCreatedEntity = userMapper.convertCreate(user);
    UserEntity createdEntity = persistenceManager.save(toBeCreatedEntity);

    return userMapper.convert(createdEntity);
  }

  @Override
  public AuthenticationUser update(AuthenticationUser user) {
    validationFacade.validateUpdate(user);

    UserEntity foundUser = persistenceManager.findByUsername(user.getUsername());
    user.setResourceId(foundUser.getResourceId());

    UserEntity userToBeUpdated = userMapper.convert(user);
    UserEntity updatedEntity = persistenceManager.update(userToBeUpdated);

    return userMapper.convert(updatedEntity);
  }

  @Override
  public void delete(String username) {
    if (BooleanUtils.isFalse(persistenceManager.existsByUsername(username))) {
      throw new ErrorException(
          AuthenticationErrorType.USER_NOT_FOUND, username + " username isn't exist.");
    }

    persistenceManager.deleteByUsername(username);
  }

  @Override
  public AuthenticationUser createFromJiraUser(JiraSearchUser jiraSearchUser, String authenticatedUsername) {
    AuthenticationUser user = new AuthenticationUser();
    MetaUtils.fillMeta(user, authenticatedUsername);

    user.setUsername(jiraSearchUser.getName());
    String password = AuthenticationUtil.generateRandomPassword();

    logger.log(Level.INFO, "Password: " + password);
    user.setPassword(password);
    user.setEmail(jiraSearchUser.getEmailAddress());

    EmailRequest request = new EmailRequest();
    request.setTo(user.getEmail());
    request.setSubject("Scrumier Registration");
    request.setBody("Your username: " + user.getUsername() + "\nYour password: " + user.getPassword());

    //boolean status = notificationProxy.sendMail(request);
    return create(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AuthenticationUser user = getUserByUsername(username);

    if (user == null) {
      logger.warn("ApplicationUser cannot be found in database. Username is {}", username);
      throw new ErrorException(
          AuthenticationErrorType.USER_NOT_FOUND, "User is not registered yet.");
    }

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .authorities(AuthenticationUtil.getAuthorities(user.getGroups()))
        .build();
  }
}
