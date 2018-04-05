package com.huanke.iot.base.dao.impl;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.security.user.UserPo;

public interface UserMapper extends BaseMapper<UserPo> {

    UserPo selectByUserName(String username);
}
