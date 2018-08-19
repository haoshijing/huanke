package com.huanke.iot.base.dao.device.ablity;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.alibity.DeviceAblityOptionPo;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:21
 */
public interface DeviceAblityOptionMapper extends BaseMapper<DeviceAblityOptionPo> {

    List<DeviceAblityOptionPo> selectOptionsByAblityId(Integer ablityId);
}
