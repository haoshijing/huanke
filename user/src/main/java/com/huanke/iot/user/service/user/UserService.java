package com.huanke.iot.user.service.user;

import com.huanke.iot.base.dao.user.UserManagerMapper;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.user.LoginRsp;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserManagerMapper userManagerMapper;

    @Resource
    private HttpServletRequest httpServletRequest;

    @Value("${saltEncrypt}")
    private String saltEncrypt;

    @Value("${skipRemoteHost}")
    private String skipRemoteHost;

    @Value("${serverConfigHost}")
    private String serverConfigHost;

    @SuppressWarnings("unchecked")
    public LoginRsp login(String userHost, String userName, String pwd) {

        UserValidator.validateLogin(userName, pwd);

        String password = MD5Util.md5(MD5Util.md5(pwd) + saltEncrypt);
        UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            if (!subject.isAuthenticated()) {
                throw new AccountException();
            }
        } catch (IncorrectCredentialsException | AccountException e) {
            throw new AccountException("用户名或密码错误");
        }
        //返回用户信息（置空密码）和权限
        LoginRsp rsp = new LoginRsp();
        User user = (User) subject.getSession().getAttribute("user");

        /*过滤 特殊域名*/
        if(!StringUtils.contains(userHost,skipRemoteHost)){
            if(!StringUtils.equals(userHost,user.getSecondDomain())){
                throw new AccountException("用户名与当前域名不匹配");
            }
        }

        user.setPassword(null);
        rsp.setUser(user);
        rsp.setPermissions((List<String>) subject.getSession().getAttribute("permission"));
        return rsp;
    }

    public Boolean logout() {

        final Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout();
        }
        return true;
    }

    public User getCurrentUser() {

        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        user.setPassword(null);
        return user;
    }

    public Integer createUser(User user) {

        UserValidator.getInstance().validateCreateUser(user);
        if (null != userManagerMapper.selectByUserName(user.getUserName())) {
            throw new BusinessException("用户名已存在");
        }
        /*获取当前域名*/
        String userHost = obtainSecondHost();
        user.setSecondDomain(userHost);
        user.setPassword(MD5Util.md5(MD5Util.md5(user.getPassword()) + saltEncrypt));
        userManagerMapper.insert(user);
        return user.getId();
    }

    public Boolean updateUser(User user) {

        UserValidator.getInstance().validateUpdateUser(user);
        user.setPassword(MD5Util.md5(MD5Util.md5(user.getPassword()) + saltEncrypt));
        return 1 == userManagerMapper.updateById(user);
    }

    public Boolean delUser(Integer userId) {

        UserValidator.getInstance().validateDelUser(userId);
        return 1 == userManagerMapper.deleteById(userId);
    }

    public List<User> getUserList() {

        /*获取当前域名*/
        String userHost = obtainSecondHost();
        List<User> users = new ArrayList<>();
        if(!StringUtils.contains(userHost,skipRemoteHost)){
            users = userManagerMapper.selectAll();
        }else{
            users = userManagerMapper.selectAllBySLD(userHost);
        }
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    public String obtainSecondHost() {
        String requestHost = httpServletRequest.getHeader("origin");
        String userHost = "";
        if (!StringUtils.isEmpty(requestHost)) {
            int userHostIdx = requestHost.indexOf("." + serverConfigHost);
            if (userHostIdx > -1) {
                userHost = requestHost.substring(7, userHostIdx);
            }
        }

        return userHost;
    }

    public boolean hasSameUser(String userName){
        boolean hasUser = false;
        if (null != userManagerMapper.selectByUserName(userName)) {
            hasUser = true;
        }
        return  hasUser;
    }
}
