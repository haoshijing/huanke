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

    Integer deleteDeviceById(Integer deviceId);

    DevicePo selectByWxDeviceId(String wxDeviceId);

    Integer updateByDeviceId(DevicePo devicePo);

    Integer insertBatch(List<DevicePo> devicePoList);

    Integer updateBatch(List<DevicePo> devicePoList);

    Integer selectDataByTime(@Param("startTime")Long startTime,@Param("endTime") Long endTime);
    Integer selectCustomerDataByTime(@Param("startTime")Long startTime,@Param("endTime") Long endTime,@Param("customerId") Integer customerId);

    Integer deleteDeviceBatch(List<DevicePo> devicePoList);

    Integer getCustomerId(DevicePo devicePo);

    List<DevicePo> selectChildDeviceListByHostDeviceId(Integer hostDeviceId);

    DevicePo getByHostDeviceIdAndTypeId(@Param("hostDeviceId") Integer hostDeviceId, @Param("childId") String childId);

    Integer queryChildDeviceCount(Integer hostDeviceId);

    List<DevicePo> queryChildDevice(Integer hostDeviceId);

    List<Integer> queryChildDeviceIds(Integer hostDeviceId);

    DevicePo getChildDevice(@Param("hostDeviceId")Integer hostDeviceId, @Param("address")String address);

    List selectDeviceCount(@Param("nowYear")int nowYear,@Param("status")int status);
    List selectDeviceCountByCustomer(@Param("nowYear")int nowYear,@Param("status")int status , @Param("customerId")Integer customerId);

    List<DevicePo> selectByModelId(Integer modelId);
    List<DevicePo> selectByTypeId(Integer typeId);

}