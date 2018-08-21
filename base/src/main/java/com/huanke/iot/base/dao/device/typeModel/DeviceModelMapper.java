package com.huanke.iot.base.dao.device.typeModel;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/16 15:21
 */
public interface DeviceModelMapper extends BaseMapper<DeviceModelPo> {

    DeviceModelPo selectById(DeviceModelPo queryDeviceModelPo);

    List<DeviceModelPo> selectByTypeId(DeviceModelPo queryDeviceModelPo);

}
