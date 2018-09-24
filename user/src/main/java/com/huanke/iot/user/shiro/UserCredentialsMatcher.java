package com.huanke.iot.user.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

public class UserCredentialsMatcher extends SimpleCredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(final AuthenticationToken token, final AuthenticationInfo info) {

        String pwdFromPage = new String(((UsernamePasswordToken) token).getPassword());
        return pwdFromPage.equals(info.getCredentials().toString());
    }
}
