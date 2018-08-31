package com.huanke.iot.base.dao.device.typeModel;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/16 15:21
 */
public interface DeviceTypeMapper extends BaseMapper<DeviceTypePo> {

    Integer getabilitySetId(Integer typeId);

    Integer updateStatusById(@Param("id")Integer id, @Param("status")Integer status);

    List<DeviceTypePo> selectListByTypeIds(List list);
}
