package com.huanke.iot.manage.security;

import com.huanke.iot.base.dao.impl.UserMapper;
import com.huanke.iot.base.po.security.user.UserPo;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;


public class MyAuthorizingRealm extends AuthorizingRealm {

    @Autowired
    private UserMapper userMapper;
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if(token instanceof UsernamePasswordToken){
            UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken)token;
            String username = usernamePasswordToken.getUsername();
            UserPo userPo = userMapper.selectByUserName(username);
            if(userPo == null){
                throw  new AuthenticationException("账号不存在");
            }
            return new SimpleAuthenticationInfo(username,userPo.getPassword(),"");
        }
        return null;
    }
}
