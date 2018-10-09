package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DevicePo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeviceMapper extends BaseMapper<DevicePo>{

    /**
     * 通过mac查找相应的
     * @param mac
     * @return
     */
    DevicePo selectByMac(String mac);

    DevicePo selectByName(String name);

    DevicePo selectDeviceCustomerRelationByMac(String mac);

    /**
     * 查找所有设备
     * @return
     */
    List<DevicePo> selectAll();

    Integer deleteDevice(DevicePo devicePo);

    DevicePo selectByWxDeviceId(String wxDeviceId);

    Integer updateByDeviceId(DevicePo devicePo);

    Integer insertBatch(List<DevicePo> devicePoList);

    Integer updateBatch(List<DevicePo> devicePoList);

    Integer updateBindStatus(DevicePo devicePo);

    Integer deleteDeviceBatch(List<DevicePo> devicePoList);

    Integer getCustomerId(DevicePo devicePo);

    List<DevicePo> selectChildDeviceListByHostDeviceId(Integer hostDeviceId);

    DevicePo getByHostDeviceIdAndTypeId(@Param("hostDeviceId") Integer hostDeviceId, @Param("childId") String childId);

    Integer queryChildDeviceCount(Integer hostDeviceId);

    List<DevicePo> queryChildDevice(Integer hostDeviceId);

    List<Integer> queryChildDeviceIds(Integer hostDeviceId);

    DevicePo getChildDevice(@Param("hostDeviceId")Integer hostDeviceId, @Param("address")String address);
}