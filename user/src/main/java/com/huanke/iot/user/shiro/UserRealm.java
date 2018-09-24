package com.huanke.iot.user.shiro;

import com.huanke.iot.user.dao.role.RoleManagerMapper;
import com.huanke.iot.user.dao.user.UserManagerMapper;
import com.huanke.iot.user.model.role.Permission;
import com.huanke.iot.user.model.user.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.util.StringUtils;

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
