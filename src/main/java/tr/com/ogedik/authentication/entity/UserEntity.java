package tr.com.ogedik.authentication.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.ObjectUtils;
import tr.com.ogedik.authentication.constants.AuthenticationConstants;
import tr.com.ogedik.commons.entity.ResourceEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/** @author orkun.gedik */
@Entity
@Table(name = AuthenticationConstants.Entity.APPLICATION_USER)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEntity extends ResourceEntity {

  @Column(name = AuthenticationConstants.COLS.USERNAME)
  private String username;

  @Column(name = AuthenticationConstants.COLS.DISPLAY_NAME)
  private String displayName;

  @Column(name = AuthenticationConstants.COLS.EMAIL)
  private String email;

  @Column(name = AuthenticationConstants.COLS.PASSWORD)
  private String password;

  @Column(name = AuthenticationConstants.COLS.TEAM)
  private String team;

  @Column(name = AuthenticationConstants.COLS.ENROLMENT_DATE)
  private LocalDateTime enrolmentDate;

  @Column(name = AuthenticationConstants.COLS.AVATAR_URL)
  private String avatarUrl;

  @Column(name = AuthenticationConstants.COLS.ADMIN)
  private String isAdmin;

  @Column(name = AuthenticationConstants.COLS.LAST_LOGIN_DATE)
  private LocalDateTime localDateTime;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserEntity that = (UserEntity) o;

    return ObjectUtils.equals(that.getResourceId(), this.getResourceId())
        && ObjectUtils.equals(that.username, this.username)
        && ObjectUtils.equals(that.password, this.password)
        && ObjectUtils.equals(that.team, this.team)
        && ObjectUtils.equals(that.enrolmentDate, this.enrolmentDate);
  }
}
