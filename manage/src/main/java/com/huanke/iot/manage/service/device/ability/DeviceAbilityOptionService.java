package com.huanke.iot.manage.service.device.ability;

import com.huanke.iot.base.dao.device.ability.DeviceAbilityOptionMapper;
import com.huanke.iot.base.po.device.ability.DeviceAbilityOptionPo;
import com.huanke.iot.manage.vo.request.device.ability.DeviceAbilityOptionCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ability.DeviceAbilityOptionQueryRequest;
import com.huanke.iot.manage.vo.response.device.ability.DeviceAbilityOptionVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DeviceAbilityOptionService {

    @Autowired
    private DeviceAbilityOptionMapper deviceAbilityOptionMapper;


    /**
     * 添加 功能的选项
     * @param optionRequest
     * @return
     */
    public Boolean createOrUpdate(DeviceAbilityOptionCreateOrUpdateRequest optionRequest) {

        int effectCount = 0;
        DeviceAbilityOptionPo deviceAbilityOptionPo = new DeviceAbilityOptionPo();
        if(null!=optionRequest){
            BeanUtils.copyProperties(optionRequest,deviceAbilityOptionPo);
        }
        if(optionRequest.getId() != null && optionRequest.getId() > 0){
            deviceAbilityOptionPo.setLastUpdateTime(System.currentTimeMillis());
            effectCount = deviceAbilityOptionMapper.updateById(deviceAbilityOptionPo);
        }else{
            deviceAbilityOptionPo.setCreateTime(System.currentTimeMillis());
            effectCount =  deviceAbilityOptionMapper.insert(deviceAbilityOptionPo);
        }
        return effectCount > 0;
    }

    /**
     * 查询 选项列表 可根据 功能主键 或者选项名称
     * @param request
     * @return
     */
    public List<DeviceAbilityOptionVo> selectList(DeviceAbilityOptionQueryRequest request) {

        DeviceAbilityOptionPo queryDeviceAbilityOptionPo = new DeviceAbilityOptionPo();
        queryDeviceAbilityOptionPo.setOptionName(request.getOptionName());
        queryDeviceAbilityOptionPo.setAbilityId(request.getAbilityId());

        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();

        List<DeviceAbilityOptionPo> deviceAbilityOptionPos = deviceAbilityOptionMapper.selectList(queryDeviceAbilityOptionPo,limit,offset);
        return deviceAbilityOptionPos.stream().map(deviceAbilityOptinPo -> {
            DeviceAbilityOptionVo DeviceAbilityOptionVo = new DeviceAbilityOptionVo();
            DeviceAbilityOptionVo.setOptionName(deviceAbilityOptinPo.getOptionName());
            DeviceAbilityOptionVo.setOptionValue(deviceAbilityOptinPo.getOptionValue());
            DeviceAbilityOptionVo.setMinVal(deviceAbilityOptinPo.getMinVal());
            DeviceAbilityOptionVo.setMaxVal(deviceAbilityOptinPo.getMaxVal());
            DeviceAbilityOptionVo.setStatus(deviceAbilityOptinPo.getStatus());
//            DeviceAbilityOptionVo.setAbilityId(deviceAbilityOptinPo.getAbilityId());
            DeviceAbilityOptionVo.setId(deviceAbilityOptinPo.getId());
            return DeviceAbilityOptionVo;
        }).collect(Collectors.toList());
    }

    /**
     * 删除 选项
     * @param optionRequest
     * @return Boolean
     */
    public Boolean deleteOptionById(DeviceAbilityOptionQueryRequest optionRequest) {

        Boolean ret  =false;

        //判断当 功能id不为空时
        if( optionRequest.getId()!=null){
            //先删除 该 功能
            ret = deviceAbilityOptionMapper.deleteById(optionRequest.getId()) > 0;
        }else{
            log.error("选项主键不可为空");
            return false;
        }
        return ret;
    }
}
