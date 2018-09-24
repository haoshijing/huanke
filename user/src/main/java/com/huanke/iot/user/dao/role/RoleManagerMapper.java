package com.huanke.iot.user.dao.role;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.user.model.role.Permission;
import com.huanke.iot.user.model.role.Role;
import com.huanke.iot.user.model.user.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RoleManagerMapper extends BaseMapper<Role> {

    List<Permission> getPermissionsByRoleId(@Param("roleId") Integer roleId);

    List<Role> selectAll();

    int insertRole2Permission(@Param("roleId") Integer roleId, @Param("permissions") List<Integer> ids);

    int delRole2PermissionByRoleId(@Param("roleId") Integer roleId);

    List<User> getUsersByRoleId(@Param("roleId") Integer roleId);
}
