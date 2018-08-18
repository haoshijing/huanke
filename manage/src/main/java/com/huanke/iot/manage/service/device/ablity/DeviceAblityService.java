package com.huanke.iot.manage.service.device.ablity;

import com.huanke.iot.base.dao.device.ablity.DeviceAblityMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityOptionMapper;
import com.huanke.iot.base.po.device.alibity.DeviceAblityPo;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityQueryRequest;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DeviceAblityService {

    @Autowired
    private DeviceAblityMapper deviceAblityMapper;

    @Autowired
    private DeviceAblityOptionMapper deviceAblityOptionMapper;


    /**
     * 新增 功能
     * @param ablityRequest
     * @return
     */
    public Boolean createOrUpdate(DeviceAblityCreateOrUpdateRequest ablityRequest) {

        int effectCount = 0;
        DeviceAblityPo deviceAblityPo = new DeviceAblityPo();
        BeanUtils.copyProperties(ablityRequest,deviceAblityPo);
        //如果有id则为更新 否则为新增
        if(ablityRequest.getId() != null && ablityRequest.getId() > 0){
            deviceAblityPo.setLastUpdateTime(System.currentTimeMillis());
            effectCount = deviceAblityMapper.updateById(deviceAblityPo);
        }else{
            deviceAblityPo.setCreateTime(System.currentTimeMillis());
            effectCount =  deviceAblityMapper.insert(deviceAblityPo);
        }
        return effectCount > 0;
    }

    /**
     * 查询功能列表
     * @param request
     * @return
     */
    public List<DeviceAblityVo> selectList(DeviceAblityQueryRequest request) {

        DeviceAblityPo queryDeviceAblityPo = new DeviceAblityPo();
        queryDeviceAblityPo.setAblityName(request.getAblityName());
        queryDeviceAblityPo.setDirValue(request.getDirValue());
        queryDeviceAblityPo.setWriteStatus(request.getWriteStatus());

        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();

        List<DeviceAblityPo> deviceAblityPos = deviceAblityMapper.selectList(queryDeviceAblityPo,limit,offset);
        return deviceAblityPos.stream().map(deviceAblityPo -> {
            DeviceAblityVo deviceAblityVo = new DeviceAblityVo();
            deviceAblityVo.setAblityName(deviceAblityPo.getAblityName());
            deviceAblityVo.setDirValue(deviceAblityPo.getDirValue());
            deviceAblityVo.setWriteStatus(deviceAblityPo.getWriteStatus());
            deviceAblityVo.setId(deviceAblityPo.getId());
            return deviceAblityVo;
        }).collect(Collectors.toList());
    }


    /**
     * 删除 该功能
     * 并同时删除该功能下 所有的选项
     * @param ablityRequest
     * @return
     */
    public Boolean deleteAblity(DeviceAblityCreateOrUpdateRequest ablityRequest) {

        Boolean ret  =false;

        //判断当 功能id不为空时
        if( ablityRequest.getId()!=null){
            //先删除 该 功能
            ret = deviceAblityMapper.deleteById(ablityRequest.getId()) > 0;
            //再删除 选项表中 的选项
            ret = ret && deviceAblityMapper.deleteOptionByAblityId(ablityRequest.getId()) > 0;
        }else{
            log.error("功能主键不可为空");
            return false;
        }
        return ret;
    }


//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
