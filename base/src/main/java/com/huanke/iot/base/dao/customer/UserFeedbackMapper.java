package com.huanke.iot.base.dao.customer;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.customer.UserFeedbackPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserFeedbackMapper extends BaseMapper<UserFeedbackPo> {

    List<UserFeedbackPo> selectListByPara(@Param("param") Object queryBean);
}
