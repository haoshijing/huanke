package com.huanke.iot.base.dao.device.ablity;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.alibity.DeviceAblityPo;
import com.huanke.iot.base.po.device.alibity.DeviceTypeAblitysPo;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:21
 */
public interface DeviceAblityMapper extends BaseMapper<DeviceAblityPo> {

    Integer deleteOptionByAblityId(Integer AblityId);

    Integer deleteOptionByOptionId(Integer OptionId);

    List<DeviceTypeAblitysPo> selectAblityListByTypeId(Integer typeId);

    List<DeviceTypeAblitysPo> selectAblitysByType( @Param("typeId")Integer typeId,@Param("ablityType")Integer ablityType);
//    List<DeviceTypeAblitysPo> selectListByType(DeviceAblityPo deviceAblityPo);


    List<DeviceAblityPo> selectDeviceAblitysByTypeId(Integer typeId);


    List<String> getDirValuesByDeviceTypeId(Integer deviceTypeId);
}
