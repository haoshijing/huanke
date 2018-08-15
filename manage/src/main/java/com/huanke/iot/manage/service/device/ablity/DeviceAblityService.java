package com.huanke.iot.manage.service.device.ablity;

import com.huanke.iot.base.dao.device.DeviceGroupItemMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityMapper;
import com.huanke.iot.base.po.device.DeviceTypePo;
import com.huanke.iot.base.po.device.alibity.DeviceAblityPo;
import com.huanke.iot.manage.vo.request.type.DeviceTypeCreateUpdateVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class DeviceAblityService {

    @Autowired
    private DeviceAblityMapper deviceAblityMapper;


    public Boolean createOrUpdate(DeviceAblityPo deviceAblityPo) {

        int effectCount = 0;
        if(deviceAblityPo.getId() != null && deviceAblityPo.getId() > 0){
            deviceAblityPo.setCreateTime(System.currentTimeMillis());
            effectCount =  deviceAblityMapper.updateById(deviceAblityPo);
        }else{
            deviceAblityPo.setLastUpdateTime(System.currentTimeMillis());
            effectCount = deviceAblityMapper.insert(deviceAblityPo);
        }
        return effectCount > 0;
    }
}
