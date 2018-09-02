package com.huanke.iot.manage.service.device.ablity;

import com.huanke.iot.base.dao.device.ablity.DeviceAblityOptionMapper;
import com.huanke.iot.base.po.device.alibity.DeviceAblityOptionPo;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityOptionCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityOptionQueryRequest;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityOptionVo;
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


    /**
     * 添加 功能的选项
     * @param optionRequest
     * @return
     */
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

    /**
     * 查询 选项列表 可根据 功能主键 或者选项名称
     * @param request
     * @return
     */
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
            DeviceAblityOptionVo.setOptionValue(deviceAblityOptinPo.getOptionValue());
            DeviceAblityOptionVo.setMinVal(deviceAblityOptinPo.getMinVal());
            DeviceAblityOptionVo.setMaxVal(deviceAblityOptinPo.getMaxVal());
            DeviceAblityOptionVo.setStatus(deviceAblityOptinPo.getStatus());
//            DeviceAblityOptionVo.setAblityId(deviceAblityOptinPo.getAblityId());
            DeviceAblityOptionVo.setId(deviceAblityOptinPo.getId());
            return DeviceAblityOptionVo;
        }).collect(Collectors.toList());
    }

    /**
     * 删除 选项
     * @param optionRequest
     * @return Boolean
     */
    public Boolean deleteOptionById(DeviceAblityOptionQueryRequest optionRequest) {

        Boolean ret  =false;

        //判断当 功能id不为空时
        if( optionRequest.getId()!=null){
            //先删除 该 功能
            ret = deviceAblityOptionMapper.deleteById(optionRequest.getId()) > 0;
        }else{
            log.error("选项主键不可为空");
            return false;
        }
        return ret;
    }
}
