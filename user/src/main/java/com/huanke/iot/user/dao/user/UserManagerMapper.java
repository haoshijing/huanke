package com.huanke.iot.user.dao.user;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.user.model.user.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserManagerMapper extends BaseMapper<User> {

    User selectByUserName(@Param("userName") String userName);

    List<User> selectAll();
}
