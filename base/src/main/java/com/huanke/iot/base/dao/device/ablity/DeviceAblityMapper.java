package com.huanke.iot.base.dao.device.ablity;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.alibity.DeviceAblityPo;
import com.huanke.iot.base.po.device.alibity.DeviceTypeAblitysPo;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:21
 */
public interface DeviceAblityMapper extends BaseMapper<DeviceAblityPo> {

    Integer deleteOptionByAblityId(Integer AblityId);

    Integer deleteOptionByOptionId(Integer OptionId);

    List<DeviceTypeAblitysPo> selectAblityListByTypeId(Integer typeId);


}
