package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceCustomerUserRelationPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * onlymark
 * 2018/8/21
 */
public interface DeviceCustomerUserRelationMapper extends BaseMapper<DeviceCustomerUserRelationPo>{

    List<DeviceCustomerUserRelationPo> findAllByDeviceCustomerUserRelationPo(DeviceCustomerUserRelationPo deviceCustomerUserRelationPo);

    int deleteRelationByJoinId(@Param("openId") String openId, @Param("deviceId") Integer deviceId);

    int insertBatch(List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPoList);

    Integer updateBatch(List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPoList);

    DeviceCustomerUserRelationPo selectByDeviceId(Integer deviceId);

    List<DeviceCustomerUserRelationPo> selectByOpenId(String openId);

    List<DeviceCustomerUserRelationPo> selectByUserId(Integer userId);

    Integer deleteBatch(List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPoList);

    void updateStatus(DeviceCustomerUserRelationPo updatePo);

    Integer queryRelationCount(DeviceCustomerUserRelationPo deviceCustomerUserRelationPo);

    int deleteRealationByDeviceId(Integer deviceId);

}
