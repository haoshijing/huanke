package com.huanke.iot.user.service.role;

import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.user.dao.role.RoleManagerMapper;
import com.huanke.iot.user.model.role.Permission;
import com.huanke.iot.user.model.role.Role;
import com.huanke.iot.user.model.role.Role2PermissionReq;
import com.huanke.iot.user.model.role.Role2PermissionRsp;
import com.huanke.iot.user.model.user.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleService {

    @Resource
    private RoleManagerMapper roleManagerMapper;

    public List<Role> getRoleList() {

        return roleManagerMapper.selectAll();
    }

    @Transactional
    public Integer createRole(Role2PermissionReq req) {

        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        Role role = req.getRole();
        role.setCreater(user.getId());
        roleManagerMapper.insert(role);
        if (!CollectionUtils.isEmpty(req.getPermissions())) {
            roleManagerMapper.insertRole2Permission(role.getId(), req.getPermissions());
        }
        return role.getId();
    }

    @Transactional
    public Boolean updateRole(Role2PermissionReq req) {

        roleManagerMapper.delRole2PermissionByRoleId(req.getRole().getId());
        if (!CollectionUtils.isEmpty(req.getPermissions())) {
            roleManagerMapper.insertRole2Permission(req.getRole().getId(), req.getPermissions());
        }
        return 1 == roleManagerMapper.updateById(req.getRole());
    }

    @Transactional
    public Boolean delRole(Integer roleId) {

        if (!CollectionUtils.isEmpty(roleManagerMapper.getUsersByRoleId(roleId))) {
            throw new BusinessException("仍有用户使用该角色");
        }

        roleManagerMapper.delRole2PermissionByRoleId(roleId);
        return 1 == roleManagerMapper.deleteById(roleId);

    }

    public List<Permission> getPermissions() {

        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        List<Permission> permissions = roleManagerMapper.getPermissionsByRoleId(user.getRoleId());
        permissions.forEach(i -> {
            String authKey = null == i.getParent() ? i.getAuthKey() : i.getAuthKey() + ":" + i.getAction();
            i.setAuthKey(authKey);
        });
        return permissions;
    }

    public Role2PermissionRsp getRoleDetail(Integer roleId) {

        Role2PermissionRsp rsp = new Role2PermissionRsp();
        rsp.setRole(roleManagerMapper.selectById(roleId));
        List<Permission> permissions = roleManagerMapper.getPermissionsByRoleId(roleId);
        permissions.forEach(i -> {
            String authKey = null == i.getParent() ? i.getAuthKey() : i.getAuthKey() + ":" + i.getAction();
            i.setAuthKey(authKey);
        });
        rsp.setPermissions(permissions);
        return rsp;
    }
}
