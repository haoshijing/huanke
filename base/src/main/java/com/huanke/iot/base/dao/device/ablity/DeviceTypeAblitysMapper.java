package com.huanke.iot.base.dao.device.ablity;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.alibity.DeviceTypeAblitysPo;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/16 15:21
 */
public interface DeviceTypeAblitysMapper extends BaseMapper<DeviceTypeAblitysPo> {

    List<DeviceTypeAblitysPo> selectByTypeId(Integer typeId);
    Integer deleteByTypeId (Integer typeId);

}