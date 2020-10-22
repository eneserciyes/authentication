package tr.com.ogedik.authentication.mapper;

import org.mapstruct.Mapper;
import tr.com.ogedik.authentication.entity.GroupEntity;
import tr.com.ogedik.authentication.model.AuthenticationGroup;
import tr.com.ogedik.commons.mapper.AbstractBoMapper;

/** @author orkun.gedik */
@Mapper(componentModel = "spring")
public abstract class GroupMapper extends AbstractBoMapper<AuthenticationGroup, GroupEntity> {

  /**
   * Maps from {@link GroupEntity} to {@link AuthenticationGroup}
   *
   * @param entity {@link GroupEntity}
   * @return {@link AuthenticationGroup}
   */
  @Override
  public abstract AuthenticationGroup convert(GroupEntity entity);

  /**
   * Maps from {@link AuthenticationGroup} to {@link GroupEntity}
   *
   * @param group {@link AuthenticationGroup}
   * @return {@link GroupEntity}
   */
  @Override
  public abstract GroupEntity convert(AuthenticationGroup group);
}
