package com.huanke.iot.user.service.role;

import com.huanke.iot.base.dao.role.RoleManagerMapper;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.role.Permission;
import com.huanke.iot.base.po.role.Role;
import com.huanke.iot.base.po.role.Role2PermissionReq;
import com.huanke.iot.base.po.role.Role2PermissionRsp;
import com.huanke.iot.base.po.user.User;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Log4j
@Service
public class RoleService {

    @Resource
    private RoleManagerMapper roleManagerMapper;

    @Resource
    private HttpServletRequest request;

    @Value("${skipRemoteHost}")
    private String skipRemoteHost;

    @Value("${serverConfigHost}")
    private String serverConfigHost;



    public List<Role> getRoleList() {

        /*获取当前域名*/
        String userHost = obtainSecondHost();
        /*过滤特殊域名 pro*/
        if(!StringUtils.contains(skipRemoteHost,userHost)){
            System.out.println("查询全部");
            log.error("查询全部:{}");
            return roleManagerMapper.selectAll();

        }else{
            System.out.println("查询域名="+userHost);
            return roleManagerMapper.selectAllBySLD(userHost);
        }

    }

    @Transactional
    public Integer createRole(Role2PermissionReq req) {

        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        Role role = req.getRole();
        role.setCreater(user.getId());
        role.setSecondDomain(user.getSecondDomain());

//        String requestHost =  request.getHeader("origin");
//        String userHost = "";
//        if(!StringUtils.isEmpty(requestHost)){
//            int userHostIdx =   requestHost.indexOf("."+serverConfigHost);
//            if(userHostIdx > -1){
//                userHost = requestHost.substring(7,userHostIdx);
//            }
//        }

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

    public String obtainSecondHost() {
        String requestHost = request.getHeader("origin");
        String userHost = "";
        if (!StringUtils.isEmpty(requestHost)) {
            int userHostIdx = requestHost.indexOf("." + serverConfigHost);
            if (userHostIdx > -1) {
                userHost = requestHost.substring(7, userHostIdx);
            }
        }

        return userHost;
    }
}
