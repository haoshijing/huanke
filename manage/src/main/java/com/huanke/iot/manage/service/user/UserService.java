package com.huanke.iot.manage.service.user;

import com.huanke.iot.base.dao.user.UserManagerMapper;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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


    public User getCurrentUser() {

        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        user.setPassword(null);
        return user;
    }

    public Integer createUser(User user) {

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
