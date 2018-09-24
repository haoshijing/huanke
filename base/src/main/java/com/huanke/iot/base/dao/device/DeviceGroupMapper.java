package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.group.DeviceGroupPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年04月08日 10:20
 **/
public interface DeviceGroupMapper extends BaseMapper<DeviceGroupPo> {


    Integer queryGroupCount(@Param("userId") Integer userId, @Param("groupName") String groupName);

    Integer updateGroupStatus(@Param("userId") Integer userId, @Param("groupId") Integer groupId, @Param("status") Integer status);

    int updateDeviceGroupId(@Param("userId") Integer userId,
                            @Param("newGroupId") int newGroupId,
                            @Param("deviceId") Integer deviceId);

    DeviceGroupPo queryByName(DeviceGroupPo deviceGroupPo);

    DeviceGroupPo selectByDeviceId(Integer deviceId);

    DeviceGroupPo selectById(Integer id);

    List<DeviceGroupPo> selectByCustomerId(Integer customerId);
}
