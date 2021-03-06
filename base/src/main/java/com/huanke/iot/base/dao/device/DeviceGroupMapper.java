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

    List<DeviceGroupPo> selectAllList(@Param("param") DeviceGroupPo queryBean,@Param("limit") int limit ,@Param("offset") int offset);

    List<DeviceGroupPo> selectAllGroup(@Param("param") DeviceGroupPo queryBean);

    Integer selectAllCount(@Param("param") DeviceGroupPo queryBean);

    Integer insertBatch(List<DeviceGroupPo> deviceGroupPoList);

    DeviceGroupPo selectByDeviceId(Integer deviceId);

    DeviceGroupPo selectById(Integer id);

    List<DeviceGroupPo> selectByCustomerId(Integer customerId);
}
