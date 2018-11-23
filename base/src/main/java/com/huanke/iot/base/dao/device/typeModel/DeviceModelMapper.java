package com.huanke.iot.base.dao.device.typeModel;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.base.resp.device.ModelProjectRsp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/16 15:21
 */
public interface DeviceModelMapper extends BaseMapper<DeviceModelPo> {

    DeviceModelPo selectById(DeviceModelPo queryDeviceModelPo);

    DeviceModelPo selectByProductId(String productId);

    List<DeviceModelPo> selectByTypeId(Integer typeId);

    List<DeviceModelPo> selectModelsByTypeIds(List typeIds);

    Integer getFormatIdById(Integer modelId);

    List<DeviceModelPo> selectModelsByCustomerId(Integer customerId);

    List selectModelPercent(@Param("customerId")Integer customerId);

    void flushCache();

    List<ModelProjectRsp> selectProjectRspByCustomerId(@Param("customerId") Integer customerId);
}
