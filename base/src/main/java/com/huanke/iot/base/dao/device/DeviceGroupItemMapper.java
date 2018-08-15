package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.DeviceGroupPo;
import com.huanke.iot.base.po.device.DevicePo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sixiaojun
 * @version 2018-08-15
 **/
public interface DeviceGroupItemMapper extends BaseMapper<DeviceGroupPo> {

    /**
     *sixiaojun
     * @param devicePo
     * @returnDeviceGroupItemPo
     */
    DeviceGroupItemPo selectByDeviceId(DevicePo devicePo);
}
