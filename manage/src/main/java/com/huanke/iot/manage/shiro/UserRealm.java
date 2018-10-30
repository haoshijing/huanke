package com.huanke.iot.manage.shiro;

import com.huanke.iot.base.dao.role.RoleManagerMapper;
import com.huanke.iot.base.dao.user.UserManagerMapper;
import com.huanke.iot.base.po.role.Permission;
import com.huanke.iot.base.po.user.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UserRealm extends AuthorizingRealm {

    @Resource
    private UserManagerMapper userManagerMapper;

    @Resource
    private RoleManagerMapper roleManagerMapper;

    @SuppressWarnings({"boxing", "unchecked"})
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {

        if (principals == null) {
            throw new AuthorizationException();
        }

        final SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        final List<String> permissions = (List<String>) SecurityUtils.getSubject().getSession().getAttribute("permission");
        info.addStringPermissions(permissions);
        return info;
    }

    @SuppressWarnings("boxing")
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken token) throws AuthenticationException {

        final UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;

        final String username = usernamePasswordToken.getUsername();
        if (username == null) {
            throw new AccountException("用户名不能为空");
        }
        User user = userManagerMapper.selectByUserName(username);
        if (user == null) {
            throw new UnknownAccountException("用户不存在");
        }

        //session存入user相关信息
        SecurityUtils.getSubject().getSession().setAttribute("user", user);
        //session存入permission相关信息
        final List<Permission> permissionList = roleManagerMapper.getPermissionsByRoleId(user.getRoleId());
        final List<String> permissions = new ArrayList<>();
        permissionList.forEach(item -> {
            String authKey = null == item.getParent() ? item.getAuthKey() : item.getAuthKey() + ":" + item.getAction();
            permissions.add(authKey);
        });

        SecurityUtils.getSubject().getSession().setAttribute("permission", permissions);
        return new SimpleAuthenticationInfo(username, user.getPassword(), getName());
    }

    @PostConstruct
    protected void initCredentialsMatcher() {

        setCredentialsMatcher(new UserCredentialsMatcher());
    }
}
