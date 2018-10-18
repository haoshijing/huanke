package com.huanke.iot.base.dao.role;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.role.Permission;
import com.huanke.iot.base.po.role.Role;
import com.huanke.iot.base.po.user.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleManagerMapper extends BaseMapper<Role> {

    List<Permission> getPermissionsByRoleId(@Param("roleId") Integer roleId);

    List<Role> selectAll();

    List<Role> selectAllBySLD(@Param("secondDomain")String secondDomain);


    int insertRole2Permission(@Param("roleId") Integer roleId, @Param("permissions") List<Integer> ids);

    int delRole2PermissionByRoleId(@Param("roleId") Integer roleId);

    List<User> getUsersByRoleId(@Param("roleId") Integer roleId);
}
