package com.huanke.iot.manage.service.device.ablity;

import com.huanke.iot.base.dao.device.ablity.DeviceAblityOptionMapper;
import com.huanke.iot.base.po.device.alibity.DeviceAblityOptionPo;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityOptionCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityOptionQueryRequest;
import com.huanke.iot.manage.vo.response.ablity.DeviceAblityOptionVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DeviceAblityOptionService {

    @Autowired
    private DeviceAblityOptionMapper deviceAblityOptionMapper;


    public Boolean createOrUpdate(DeviceAblityOptionCreateOrUpdateRequest optionRequest) {

        int effectCount = 0;
        DeviceAblityOptionPo deviceAblityOptionPo = new DeviceAblityOptionPo();
        BeanUtils.copyProperties(optionRequest,deviceAblityOptionPo);
        if(optionRequest.getId() != null && optionRequest.getId() > 0){
            deviceAblityOptionPo.setLastUpdateTime(System.currentTimeMillis());
            effectCount = deviceAblityOptionMapper.updateById(deviceAblityOptionPo);
        }else{
            deviceAblityOptionPo.setCreateTime(System.currentTimeMillis());
            effectCount =  deviceAblityOptionMapper.insert(deviceAblityOptionPo);
        }
        return effectCount > 0;
    }

    public List<DeviceAblityOptionVo> selectList(DeviceAblityOptionQueryRequest request) {

        DeviceAblityOptionPo queryDeviceAblityOptionPo = new DeviceAblityOptionPo();
        queryDeviceAblityOptionPo.setOptionName(request.getOptionName());
        queryDeviceAblityOptionPo.setAblityId(request.getAblityId());

        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();

        List<DeviceAblityOptionPo> deviceAblityOptionPos = deviceAblityOptionMapper.selectList(queryDeviceAblityOptionPo,limit,offset);
        return deviceAblityOptionPos.stream().map(deviceAblityOptinPo -> {
            DeviceAblityOptionVo DeviceAblityOptionVo = new DeviceAblityOptionVo();
            DeviceAblityOptionVo.setOptionName(deviceAblityOptinPo.getOptionName());
            DeviceAblityOptionVo.setAblityId(deviceAblityOptinPo.getAblityId());
            DeviceAblityOptionVo.setId(deviceAblityOptinPo.getId());
            return DeviceAblityOptionVo;
        }).collect(Collectors.toList());
    }

}
