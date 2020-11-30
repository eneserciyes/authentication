package tr.com.ogedik.authentication.util;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tr.com.ogedik.authentication.constants.Permission;
import tr.com.ogedik.authentication.model.UserGrantedAuthority;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

/** @author orkun.gedik */
@UtilityClass
public class AuthenticationUtil {
  private static final Logger logger = LogManager.getLogger(AuthenticationUtil.class);

  public static List<UserGrantedAuthority> getAuthorities(List<Permission> permissions) {
    try {
      logger.info("Retrieved permission list: {}", permissions);
      return permissions.stream()
          .map(UserGrantedAuthority::new)
          .collect(Collectors.toList());
    } catch (Exception e) {
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
