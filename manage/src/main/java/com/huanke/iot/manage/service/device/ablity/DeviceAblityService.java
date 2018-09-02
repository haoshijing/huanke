package com.huanke.iot.manage.service.device.ablity;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityOptionMapper;
import com.huanke.iot.base.po.device.alibity.DeviceAblityOptionPo;
import com.huanke.iot.base.po.device.alibity.DeviceAblityPo;
import com.huanke.iot.base.po.device.alibity.DeviceTypeAblitysPo;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityOptionCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityQueryRequest;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityOptionVo;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityVo;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceTypeAblitysVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
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
     *
     * @param ablityRequest
     * @return
     */
    public ApiResponse<Integer> createOrUpdate(DeviceAblityCreateOrUpdateRequest ablityRequest) {

        int effectCount = 0;
        Boolean ret = false;
        DeviceAblityPo deviceAblityPo = new DeviceAblityPo();
        BeanUtils.copyProperties(ablityRequest, deviceAblityPo);
        //如果有id则为更新 否则为新增
        if (ablityRequest.getId() != null && ablityRequest.getId() > 0) {
            //如果 状态不是删除，则全部默认为正常
            if(deviceAblityPo.getStatus()!=null&&deviceAblityPo.getStatus().equals(CommonConstant.STATUS_DEL)){
//                deviceAblityPo.setStatus(CommonConstant.STATUS_YES);
            }else{
                deviceAblityPo.setStatus(CommonConstant.STATUS_YES);
            }
            deviceAblityPo.setLastUpdateTime(System.currentTimeMillis());
            ret = deviceAblityMapper.updateById(deviceAblityPo) > 0;
        } else {
            deviceAblityPo.setStatus(CommonConstant.STATUS_YES);
            deviceAblityPo.setCreateTime(System.currentTimeMillis());
            ret = deviceAblityMapper.insert(deviceAblityPo) > 0;
        }
        //判断 该功能里的选项是否为空，若不为空则进行保存
        if (ablityRequest.getDeviceAblityOptions() != null && ablityRequest.getDeviceAblityOptions().size() > 0) {

            for (DeviceAblityOptionCreateOrUpdateRequest deviceAblityOptionRequest : ablityRequest.getDeviceAblityOptions()) {
                DeviceAblityOptionPo deviceAblityOptionPo = new DeviceAblityOptionPo();
                deviceAblityOptionPo.setAblityId(deviceAblityPo.getId());
                deviceAblityOptionPo.setOptionName(deviceAblityOptionRequest.getOptionName());
                deviceAblityOptionPo.setOptionValue(deviceAblityOptionRequest.getOptionValue());
                deviceAblityOptionPo.setMinVal(deviceAblityOptionRequest.getMinVal());
                deviceAblityOptionPo.setMaxVal(deviceAblityOptionRequest.getMinVal());
                deviceAblityOptionPo.setStatus(CommonConstant.STATUS_YES);
                //如果 该选项有id 则为更新 ，否则为新增
                if(deviceAblityOptionRequest.getId()!=null&&deviceAblityOptionRequest.getId()>0){
                    deviceAblityOptionPo.setId(deviceAblityOptionRequest.getId());
                    deviceAblityOptionPo.setLastUpdateTime(System.currentTimeMillis());

                    //只有当前台传的状态完全等于 删除的时候，才会更新状态为删除。否则仍然为正常状态
                    if(CommonConstant.STATUS_DEL.equals(deviceAblityOptionRequest.getStatus())){
                        deviceAblityOptionPo.setStatus(CommonConstant.STATUS_DEL);
                    }else{
                        deviceAblityOptionPo.setStatus(CommonConstant.STATUS_YES);
                    }
                    deviceAblityOptionMapper.updateById(deviceAblityOptionPo);
                }else{

                    deviceAblityOptionPo.setCreateTime(System.currentTimeMillis());
                    deviceAblityOptionMapper.insert(deviceAblityOptionPo);
                }


            }
        }

        return new ApiResponse<>(deviceAblityPo.getId());
    }

    /**
     * 查询功能列表
     *
     * @param request
     * @return
     */
    public List<DeviceAblityVo> selectList(DeviceAblityQueryRequest request) {

        DeviceAblityPo queryDeviceAblityPo = new DeviceAblityPo();
        queryDeviceAblityPo.setAblityName(request.getAblityName());
        queryDeviceAblityPo.setDirValue(request.getDirValue());
        queryDeviceAblityPo.setWriteStatus(request.getWriteStatus());
        queryDeviceAblityPo.setReadStatus(request.getReadStatus());
        queryDeviceAblityPo.setRunStatus(request.getRunStatus());
        queryDeviceAblityPo.setConfigType(request.getConfigType());
        queryDeviceAblityPo.setAblityType(request.getAblityType());

        Integer offset = (request.getPage() - 1) * request.getLimit();
        Integer limit = request.getLimit();

        //查询 功能列表
        List<DeviceAblityPo> deviceAblityPos = deviceAblityMapper.selectList(queryDeviceAblityPo, limit, offset);
        List<DeviceAblityVo> deviceAblityVos = deviceAblityPos.stream().map(deviceAblityPo -> {
            DeviceAblityVo deviceAblityVo = new DeviceAblityVo();
            deviceAblityVo.setAblityName(deviceAblityPo.getAblityName());
            deviceAblityVo.setDirValue(deviceAblityPo.getDirValue());
            deviceAblityVo.setWriteStatus(deviceAblityPo.getWriteStatus());
            deviceAblityVo.setReadStatus(deviceAblityPo.getReadStatus());
            deviceAblityVo.setRunStatus(deviceAblityPo.getRunStatus());
            deviceAblityVo.setConfigType(deviceAblityPo.getConfigType());
            deviceAblityVo.setAblityType(deviceAblityPo.getAblityType());
            deviceAblityVo.setMinVal(deviceAblityPo.getMinVal());
            deviceAblityVo.setMaxVal(deviceAblityPo.getMaxVal());
            deviceAblityVo.setRemark(deviceAblityPo.getRemark());
            deviceAblityVo.setId(deviceAblityPo.getId());

            //根据功能主键 查询该功能下的 选项列表
            List<DeviceAblityOptionPo> deviceAblityOptionpos = deviceAblityOptionMapper.selectOptionsByAblityId(deviceAblityPo.getId());
            List<DeviceAblityOptionVo> deviceAblityOptionVos = deviceAblityOptionpos.stream().map(deviceAblityOptionPo -> {
                DeviceAblityOptionVo deviceAblityOptionVo = new DeviceAblityOptionVo();
                deviceAblityOptionVo.setId(deviceAblityOptionPo.getId());
                deviceAblityOptionVo.setStatus(deviceAblityOptionPo.getStatus());
                deviceAblityOptionVo.setOptionValue(deviceAblityOptionPo.getOptionValue());
                deviceAblityOptionVo.setOptionName(deviceAblityOptionPo.getOptionName());
                deviceAblityOptionVo.setMinVal(deviceAblityOptionPo.getMinVal());
                deviceAblityOptionVo.setMaxVal(deviceAblityOptionPo.getMaxVal());
                return deviceAblityOptionVo;
            }).collect(Collectors.toList());

            deviceAblityVo.setDeviceAblityOptions(deviceAblityOptionVos);
            return deviceAblityVo;
        }).collect(Collectors.toList());

        return deviceAblityVos;
    }


    /**
     * 查询设备类型指定的 功能类型的 功能列表
     *
     * @param typeId
     * @param ablityType
     * @return
     */
    public List<DeviceTypeAblitysVo> selectListByType(Integer typeId,Integer ablityType) {

        //查询 功能列表
        List<DeviceTypeAblitysPo> deviceTypeAblitysPos = deviceAblityMapper.selectAblitysByType(typeId,ablityType);

        List<DeviceTypeAblitysVo> deviceTypeAblitysVos = deviceTypeAblitysPos.stream().map(deviceTypeAblitysPo -> {
            DeviceTypeAblitysVo deviceTypeAblitysVo = new DeviceTypeAblitysVo();
            deviceTypeAblitysVo.setId(deviceTypeAblitysPo.getId());
            deviceTypeAblitysVo.setTypeId(deviceTypeAblitysPo.getTypeId());
            deviceTypeAblitysVo.setAblityId(deviceTypeAblitysPo.getAblityId());
            deviceTypeAblitysVo.setAblityName(deviceTypeAblitysPo.getAblityName());
            deviceTypeAblitysVo.setAblityType(deviceTypeAblitysPo.getAblityType());
            deviceTypeAblitysVo.setMinVal(deviceTypeAblitysPo.getMinVal());
            deviceTypeAblitysVo.setMaxVal(deviceTypeAblitysPo.getMaxVal());

            //根据功能主键 查询该功能下的 选项列表
            List<DeviceAblityOptionPo> deviceAblityOptionpos = deviceAblityOptionMapper.selectOptionsByAblityId(deviceTypeAblitysPo.getAblityId());
            List<DeviceAblityOptionVo> deviceAblityOptionVos = deviceAblityOptionpos.stream().map(deviceAblityOptionPo -> {
                DeviceAblityOptionVo deviceAblityOptionVo = new DeviceAblityOptionVo();
                deviceAblityOptionVo.setId(deviceAblityOptionPo.getId());
                deviceAblityOptionVo.setOptionValue(deviceAblityOptionPo.getOptionValue());
                deviceAblityOptionVo.setOptionName(deviceAblityOptionPo.getOptionName());
                deviceAblityOptionVo.setMinVal(deviceAblityOptionPo.getMinVal());
                deviceAblityOptionVo.setMaxVal(deviceAblityOptionPo.getMaxVal());
                deviceAblityOptionVo.setStatus(deviceAblityOptionPo.getStatus());
                return deviceAblityOptionVo;
            }).collect(Collectors.toList());

            deviceTypeAblitysVo.setDeviceAblityOptions(deviceAblityOptionVos);
            return deviceTypeAblitysVo;
        }).collect(Collectors.toList());

        return deviceTypeAblitysVos;
    }
    /**
     * 根据主键查询 功能
     *
     * @param typeId
     * @return
     */
    public DeviceAblityVo selectById(Integer typeId) {

        DeviceAblityPo deviceAblityPo = deviceAblityMapper.selectById(typeId);

        DeviceAblityVo deviceAblityVo = new DeviceAblityVo();
        if(deviceAblityPo!=null){
            deviceAblityVo.setAblityName(deviceAblityPo.getAblityName());
            deviceAblityVo.setDirValue(deviceAblityPo.getDirValue());
            deviceAblityVo.setWriteStatus(deviceAblityPo.getWriteStatus());
            deviceAblityVo.setReadStatus(deviceAblityPo.getReadStatus());
            deviceAblityVo.setRunStatus(deviceAblityPo.getRunStatus());
            deviceAblityVo.setConfigType(deviceAblityPo.getConfigType());
            deviceAblityVo.setAblityType(deviceAblityPo.getAblityType());
            deviceAblityVo.setMinVal(deviceAblityPo.getMinVal());
            deviceAblityVo.setMaxVal(deviceAblityPo.getMaxVal());
            deviceAblityVo.setRemark(deviceAblityPo.getRemark());
            deviceAblityVo.setId(deviceAblityPo.getId());

            //根据功能主键 查询该功能下的 选项列表
            List<DeviceAblityOptionPo> deviceAblityOptionpos = deviceAblityOptionMapper.selectOptionsByAblityId(deviceAblityPo.getId());
            List<DeviceAblityOptionVo> deviceAblityOptionVos = deviceAblityOptionpos.stream().map(deviceAblityOptionPo -> {
                DeviceAblityOptionVo deviceAblityOptionVo = new DeviceAblityOptionVo();
                deviceAblityOptionVo.setId(deviceAblityOptionPo.getId());
                deviceAblityOptionVo.setOptionValue(deviceAblityOptionPo.getOptionValue());
                deviceAblityOptionVo.setOptionName(deviceAblityOptionPo.getOptionName());
                deviceAblityOptionVo.setMinVal(deviceAblityOptionPo.getMinVal());
                deviceAblityOptionVo.setMaxVal(deviceAblityOptionPo.getMaxVal());
                deviceAblityOptionVo.setStatus(deviceAblityOptionPo.getStatus());
                return deviceAblityOptionVo;
            }).collect(Collectors.toList());

            deviceAblityVo.setDeviceAblityOptions(deviceAblityOptionVos);
        }
        return deviceAblityVo;
    }
    /**
     * 删除 该功能
     * 并同时删除该功能下 所有的选项
     *
     * @param ablityId
     * @return
     */
    public Boolean deleteAblity(Integer ablityId) {

        Boolean ret = false;
        //先删除 该 功能
        ret = deviceAblityMapper.deleteById(ablityId) > 0;
        //再删除 选项表中 的选项
        ret = ret && deviceAblityMapper.deleteOptionByAblityId(ablityId) > 0;
        return ret;
    }


//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
