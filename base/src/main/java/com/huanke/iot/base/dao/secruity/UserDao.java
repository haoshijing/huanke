package com.huanke.iot.base.dao.secruity;

import com.huanke.iot.base.po.security.user.UserPo;

public interface UserDao {
    UserPo selectByUserName(String userName);
}
