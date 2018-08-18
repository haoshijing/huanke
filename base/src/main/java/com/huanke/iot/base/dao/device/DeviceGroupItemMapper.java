package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.DevicePo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sixiaojun
 * @version 2018-08-15
 **/
public interface DeviceGroupItemMapper extends BaseMapper<DeviceGroupItemPo> {

    /**
     * 根据设备ID查询设备群关系
     *sixiaojun
     * @param deviceId
     * @returnDeviceGroupItemPo
     */
    DeviceGroupItemPo selectByDeviceId(Integer deviceId);
}
