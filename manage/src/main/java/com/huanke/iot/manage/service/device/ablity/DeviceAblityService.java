package com.huanke.iot.manage.service.device.ablity;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityOptionMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceTypeAblitysMapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private DeviceTypeAblitysMapper deviceAblitysMapper;

    @Value("${env}")
    private String env;
    /**
     * 新增 功能
     *
     * @param ablityRequest
     * @return
     */
    @Transactional
    public ApiResponse<Integer> createOrUpdate(DeviceAblityCreateOrUpdateRequest ablityRequest) {

        int effectCount = 0;
        Boolean ret = false;
        DeviceAblityPo deviceAblityPo = new DeviceAblityPo();
        try{
            BeanUtils.copyProperties(ablityRequest, deviceAblityPo);
            //如果有id则为更新 否则为新增
            if (ablityRequest.getId() != null && ablityRequest.getId() > 0) {
                //如果 状态不是删除，则全部默认为正常
                if(CommonConstant.STATUS_DEL.equals(deviceAblityPo.getStatus())){
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

                        List a = null;
                        Map b = (Map)a.get(0);
                        deviceAblityOptionPo.setCreateTime(System.currentTimeMillis());
                        deviceAblityOptionMapper.insert(deviceAblityOptionPo);

                    }

                }
            }
            return new ApiResponse<>(deviceAblityPo.getId());

        }catch (Exception e){
            log.error("保存功能项失败 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"保存功能项失败");
        }

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
     * 删除 该功能 需判断 是否有类型 进行了引用，如果有，则不允许删除
     * 并同时删除该功能下 所有的选项
     *
     * @param ablityId
     * @return
     */
    public ApiResponse<Boolean> deleteAblity(Integer ablityId) {

        //如果是开发环境
        if("dev".equals(env)){
            System.out.println("123");
        }else{
            System.out.println("345");
        }

        //首先进行判断该 功能是否存在。
        DeviceAblityPo deviceAblityPo = deviceAblityMapper.selectById(ablityId);
        if(deviceAblityPo!=null){

            //根据功能项主键 查询是否有类型 进行了配置使用，如果有，则不允许删除。
            if(!"dev".equals(env)){
                List<DeviceTypeAblitysPo> deviceTypeAblitysPos = deviceAblitysMapper.selectByAblityId(ablityId);
                if(deviceTypeAblitysPos!=null&&deviceTypeAblitysPos.size()>0){
                    return new ApiResponse<>(RetCode.PARAM_ERROR,"该功能项已被设备类型所引用，不可删除");
                }
            }

            Boolean ret = false;
            //先删除 该 功能
            ret = deviceAblityMapper.deleteById(ablityId) > 0;
            //再删除 选项表中 的选项
            ret = ret && deviceAblityMapper.deleteOptionByAblityId(ablityId) > 0;
            if(!"dev".equals(env)){
                return new ApiResponse<>(RetCode.OK,"删除成功");
            }else{
                return new ApiResponse<>(RetCode.OK,"删除成功-存在类型引用。");
            }
        }else{
            return new ApiResponse<>(RetCode.PARAM_ERROR,"该功能项不存在");
        }

    }


//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
