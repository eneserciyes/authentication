package tr.com.ogedik.authentication.util;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import tr.com.ogedik.authentication.constants.Permission;
import tr.com.ogedik.authentication.model.AuthenticationGroup;
import tr.com.ogedik.authentication.model.UserGrantedAuthority;
import tr.com.ogedik.commons.util.ListUtils;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

/** @author orkun.gedik */
@UtilityClass
public class AuthenticationUtil {
  private static final Logger logger = LogManager.getLogger(AuthenticationUtil.class);

  /**
   * Returns an list of {@link GrantedAuthority} by given groups
   *
   * @param groups {@link List}<{@link tr.com.ogedik.authentication.model.AuthenticationGroup}>
   * @return {@link List}<{@link UserGrantedAuthority}>
   */
  public static List<UserGrantedAuthority> getAuthorities(List<AuthenticationGroup> groups) {
    try {
      List<Permission> permissions =
          (List<Permission>) ListUtils.mergeNested(groups, "permissions");
      logger.info("Retrieved permission list: {}", permissions);
      return permissions.stream()
          .map(permission -> new UserGrantedAuthority(permission))
          .collect(Collectors.toList());
    } catch (IllegalAccessException e) {
      logger.warn(
          "Cannot parse user permissions. Authentication will be provided without authorities");
      return null;
    }
  }

  public static String generateRandomPassword(){
    final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    final int len = 10;

    SecureRandom random = new SecureRandom();
    StringBuilder sb = new StringBuilder();

    // each iteration of loop choose a character randomly from the given ASCII range
    // and append it to StringBuilder instance

    for (int i = 0; i < len; i++) {
      int randomIndex = random.nextInt(chars.length());
      sb.append(chars.charAt(randomIndex));
    }

    return sb.toString();

  }
}
