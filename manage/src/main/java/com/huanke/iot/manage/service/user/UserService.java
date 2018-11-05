package com.huanke.iot.manage.service.user;

import com.huanke.iot.base.dao.user.UserManagerMapper;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.util.CommonUtil;
import com.huanke.iot.base.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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

    @Resource
    private CommonUtil commonUtil;

    public User getCurrentUser() {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) SecurityUtils.getSubject().getSession().getAttribute("user");
        if(user == null || user.getUserName()==null){
            throw new BusinessException("登录超时失效，请重新登录！");
        }
        user.setPassword(null);
        return user;
    }

    public Integer createUser(User user) {

        if (null != userManagerMapper.selectByUserName(user.getUserName())) {
            throw new BusinessException("用户名已存在");
        }

        user.setPassword(MD5Util.md5(MD5Util.md5(user.getPassword()) + saltEncrypt));
        userManagerMapper.insert(user);
        return user.getId();
    }

    public boolean hasSameUser(String userName){
        boolean hasUser = false;
        if (null != userManagerMapper.selectByUserName(userName)) {
            hasUser = true;
        }
        return  hasUser;
    }

    public String getUserName(Integer userId){
        if(userId!=null){
            User user = userManagerMapper.selectById(userId);
            if(user!=null){
                return user.getUserName();

            }else{
                return null;
            }

        }else{
            return null;
        }

    }
}
