package com.huanke.iot.user.service.user;

import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.user.User;
import org.springframework.util.StringUtils;

public class UserValidator {

    private UserValidator() {
    }


    private static class Holder {
        private static UserValidator instance = new UserValidator();
    }

    public static UserValidator getInstance() {
        return Holder.instance;
    }

    public static void validateLogin(String userName, String pwd) {

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(pwd)) {
            throw new BusinessException("登录参数错误");
        }
    }

    public void validateDelUser(Integer userId) {

        if (StringUtils.isEmpty(userId)) {
            throw new BusinessException("参数不能为空");
        }
    }

    public void validateCreateUser(User user) {

        if (StringUtils.isEmpty(user.getUserName())) {
            throw new BusinessException("用户名不能为空");
        }
    }

    public void validateUpdateUser(User user) {
    }
}
