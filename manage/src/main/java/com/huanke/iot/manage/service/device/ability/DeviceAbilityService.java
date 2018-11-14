package com.huanke.iot.manage.service.device.ability;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityOptionMapper;
import com.huanke.iot.base.dao.device.ability.DeviceTypeAbilitysMapper;
import com.huanke.iot.base.po.device.ability.DeviceAbilityOptionPo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.device.ability.DeviceTypeAbilitysPo;
import com.huanke.iot.manage.vo.request.device.ability.DeviceAbilityCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ability.DeviceAbilityOptionCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ability.DeviceAbilityQueryRequest;
import com.huanke.iot.manage.vo.response.device.ability.DeviceAbilityOptionVo;
import com.huanke.iot.manage.vo.response.device.ability.DeviceAbilityVo;
import com.huanke.iot.manage.vo.response.device.ability.DeviceTypeAbilitysVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DeviceAbilityService {

    @Autowired
    private DeviceAbilityMapper deviceAbilityMapper;

    @Autowired
    private DeviceAbilityOptionMapper deviceAbilityOptionMapper;

    @Autowired
    private DeviceTypeAbilitysMapper deviceAbilitysMapper;

    @Value("${env}")
    private String env;

    /**
     * 新增 功能
     *
     * @param abilityRequest
     * @return
     */
//    @Transactional
    public ApiResponse<Integer> createOrUpdate(DeviceAbilityCreateOrUpdateRequest abilityRequest) throws Exception {

        int effectCount = 0;
        Boolean ret = false;

        if(StringUtils.isNotBlank(abilityRequest.getAbilityCode())){
            DeviceAbilityPo queryAbilityPo = deviceAbilityMapper.selectByAbilityCode(abilityRequest.getAbilityCode());
            if(queryAbilityPo!=null&&queryAbilityPo.getId().equals(abilityRequest.getId())){
                return new ApiResponse<>(RetCode.PARAM_ERROR,"已存在此功能项代码");
            }
        }

        DeviceAbilityPo deviceAbilityPo = new DeviceAbilityPo();
        try {
            BeanUtils.copyProperties(abilityRequest, deviceAbilityPo);
            //如果有id则为更新 否则为新增
            if (abilityRequest.getId() != null && abilityRequest.getId() > 0) {
                //如果 状态不是删除，则全部默认为正常
                if (CommonConstant.STATUS_DEL.equals(deviceAbilityPo.getStatus())) {
//                deviceAbilityPo.setStatus(CommonConstant.STATUS_YES);
                } else {
                    deviceAbilityPo.setStatus(CommonConstant.STATUS_YES);
                }
                deviceAbilityPo.setLastUpdateTime(System.currentTimeMillis());
                ret = deviceAbilityMapper.updateById(deviceAbilityPo) > 0;
            } else {
                deviceAbilityPo.setStatus(CommonConstant.STATUS_YES);
                deviceAbilityPo.setCreateTime(System.currentTimeMillis());
                ret = deviceAbilityMapper.insert(deviceAbilityPo) > 0;
            }

            //判断 该功能里的选项是否为空，若不为空则进行保存
            if (abilityRequest.getDeviceAbilityOptions() != null && abilityRequest.getDeviceAbilityOptions().size() > 0) {

                for (DeviceAbilityOptionCreateOrUpdateRequest deviceAbilityOptionRequest : abilityRequest.getDeviceAbilityOptions()) {
                    DeviceAbilityOptionPo deviceAbilityOptionPo = new DeviceAbilityOptionPo();
                    deviceAbilityOptionPo.setAbilityId(deviceAbilityPo.getId());
                    deviceAbilityOptionPo.setOptionName(deviceAbilityOptionRequest.getOptionName());
                    deviceAbilityOptionPo.setOptionValue(deviceAbilityOptionRequest.getOptionValue());
                    deviceAbilityOptionPo.setDefaultValue(deviceAbilityOptionRequest.getDefaultVal());
                    deviceAbilityOptionPo.setMinVal(deviceAbilityOptionRequest.getMinVal());
                    deviceAbilityOptionPo.setMaxVal(deviceAbilityOptionRequest.getMaxVal());
                    deviceAbilityOptionPo.setStatus(CommonConstant.STATUS_YES);
                    //如果 该选项有id 则为更新 ，否则为新增
                    if (deviceAbilityOptionRequest.getId() != null && deviceAbilityOptionRequest.getId() > 0) {
                        deviceAbilityOptionPo.setId(deviceAbilityOptionRequest.getId());
                        deviceAbilityOptionPo.setLastUpdateTime(System.currentTimeMillis());

                        //只有当前台传的状态完全等于 删除的时候，才会更新状态为删除。否则仍然为正常状态
                        if (CommonConstant.STATUS_DEL.equals(deviceAbilityOptionRequest.getStatus())) {
                            deviceAbilityOptionPo.setStatus(CommonConstant.STATUS_DEL);
                        } else {
                            deviceAbilityOptionPo.setStatus(CommonConstant.STATUS_YES);
                        }
                        deviceAbilityOptionMapper.updateById(deviceAbilityOptionPo);
                    } else {

                        deviceAbilityOptionPo.setCreateTime(System.currentTimeMillis());
                        deviceAbilityOptionMapper.insert(deviceAbilityOptionPo);

                    }

                }
            }
            return new ApiResponse<>(deviceAbilityPo.getId());

        } catch (RuntimeException e) {
            log.error("保存功能项失败 = {}", e);
            throw new RuntimeException(e);
//            return new ApiResponse<>(RetCode.ERROR,"保存功能项失败");
        }

    }

    /**
     * 查询功能列表
     *
     * @param request
     * @return
     */
    public List<DeviceAbilityVo> selectList(DeviceAbilityQueryRequest request) {

        DeviceAbilityPo queryDeviceAbilityPo = new DeviceAbilityPo();
        queryDeviceAbilityPo.setAbilityName(request.getAbilityName());
        queryDeviceAbilityPo.setAbilityCode(request.getAbilityCode());
        queryDeviceAbilityPo.setDirValue(request.getDirValue());
        queryDeviceAbilityPo.setWriteStatus(request.getWriteStatus());
        queryDeviceAbilityPo.setReadStatus(request.getReadStatus());
        queryDeviceAbilityPo.setRunStatus(request.getRunStatus());
        queryDeviceAbilityPo.setConfigType(request.getConfigType());
        queryDeviceAbilityPo.setAbilityType(request.getAbilityType());
        queryDeviceAbilityPo.setStatus(request.getStatus());
        Integer offset = (request.getPage() - 1) * request.getLimit();
        Integer limit = request.getLimit();

        //查询 功能列表
        List<DeviceAbilityPo> deviceAbilityPos = deviceAbilityMapper.selectList(queryDeviceAbilityPo, limit, offset);
        List<DeviceAbilityVo> deviceAbilityVos = deviceAbilityPos.stream().map(deviceAbilityPo -> {
            DeviceAbilityVo deviceAbilityVo = new DeviceAbilityVo();
            deviceAbilityVo.setAbilityName(deviceAbilityPo.getAbilityName());
            deviceAbilityVo.setAbilityCode(deviceAbilityPo.getAbilityCode());
            deviceAbilityVo.setDirValue(deviceAbilityPo.getDirValue());
            deviceAbilityVo.setWriteStatus(deviceAbilityPo.getWriteStatus());
            deviceAbilityVo.setReadStatus(deviceAbilityPo.getReadStatus());
            deviceAbilityVo.setRunStatus(deviceAbilityPo.getRunStatus());
            deviceAbilityVo.setConfigType(deviceAbilityPo.getConfigType());
            deviceAbilityVo.setAbilityType(deviceAbilityPo.getAbilityType());
            deviceAbilityVo.setMinVal(deviceAbilityPo.getMinVal());
            deviceAbilityVo.setMaxVal(deviceAbilityPo.getMaxVal());
            deviceAbilityVo.setRemark(deviceAbilityPo.getRemark());
            deviceAbilityVo.setId(deviceAbilityPo.getId());

            //根据功能主键 查询该功能下的 选项列表
            List<DeviceAbilityOptionPo> deviceAbilityOptionpos = deviceAbilityOptionMapper.selectOptionsByAbilityId(deviceAbilityPo.getId());
            List<DeviceAbilityOptionVo> deviceAbilityOptionVos = deviceAbilityOptionpos.stream().map(deviceAbilityOptionPo -> {
                DeviceAbilityOptionVo deviceAbilityOptionVo = new DeviceAbilityOptionVo();
                deviceAbilityOptionVo.setId(deviceAbilityOptionPo.getId());
                deviceAbilityOptionVo.setStatus(deviceAbilityOptionPo.getStatus());
                deviceAbilityOptionVo.setOptionValue(deviceAbilityOptionPo.getOptionValue());
                deviceAbilityOptionVo.setOptionName(deviceAbilityOptionPo.getOptionName());
                deviceAbilityOptionVo.setDefaultVal(deviceAbilityOptionPo.getDefaultValue());
                deviceAbilityOptionVo.setMinVal(deviceAbilityOptionPo.getMinVal());
                deviceAbilityOptionVo.setMaxVal(deviceAbilityOptionPo.getMaxVal());
                return deviceAbilityOptionVo;
            }).collect(Collectors.toList());

            deviceAbilityVo.setDeviceAbilityOptions(deviceAbilityOptionVos);
            return deviceAbilityVo;
        }).collect(Collectors.toList());

        return deviceAbilityVos;
    }

    public ApiResponse<Integer> selectCount(Integer status) {
        DeviceAbilityPo deviceAbilityPo = new DeviceAbilityPo();
        deviceAbilityPo.setStatus(status);
        return new ApiResponse<>(RetCode.OK, "设备功能总数查询成功", this.deviceAbilityMapper.selectCount(deviceAbilityPo));
    }


    /**
     * 查询设备类型指定的 功能类型的 功能列表
     *
     * @param typeId
     * @param abilityType
     * @return
     */
    public List<DeviceTypeAbilitysVo> selectListByType(Integer typeId, Integer abilityType) {

        //查询 功能列表
        List<DeviceTypeAbilitysPo> deviceTypeAbilitysPos = deviceAbilityMapper.selectAbilitysByType(typeId, abilityType);

        List<DeviceTypeAbilitysVo> deviceTypeAbilitysVos = deviceTypeAbilitysPos.stream().map(deviceTypeAbilitysPo -> {
            DeviceTypeAbilitysVo deviceTypeAbilitysVo = new DeviceTypeAbilitysVo();
            deviceTypeAbilitysVo.setId(deviceTypeAbilitysPo.getId());
            deviceTypeAbilitysVo.setTypeId(deviceTypeAbilitysPo.getTypeId());
            deviceTypeAbilitysVo.setAbilityId(deviceTypeAbilitysPo.getAbilityId());
            deviceTypeAbilitysVo.setAbilityName(deviceTypeAbilitysPo.getAbilityName());
            deviceTypeAbilitysVo.setAbilityType(deviceTypeAbilitysPo.getAbilityType());
            deviceTypeAbilitysVo.setMinVal(deviceTypeAbilitysPo.getMinVal());
            deviceTypeAbilitysVo.setMaxVal(deviceTypeAbilitysPo.getMaxVal());

            //根据功能主键 查询该功能下的 选项列表
            List<DeviceAbilityOptionPo> deviceAbilityOptionpos = deviceAbilityOptionMapper.selectOptionsByAbilityId(deviceTypeAbilitysPo.getAbilityId());
            List<DeviceAbilityOptionVo> deviceAbilityOptionVos = deviceAbilityOptionpos.stream().map(deviceAbilityOptionPo -> {
                DeviceAbilityOptionVo deviceAbilityOptionVo = new DeviceAbilityOptionVo();
                deviceAbilityOptionVo.setId(deviceAbilityOptionPo.getId());
                deviceAbilityOptionVo.setOptionValue(deviceAbilityOptionPo.getOptionValue());
                deviceAbilityOptionVo.setOptionName(deviceAbilityOptionPo.getOptionName());
                deviceAbilityOptionVo.setDefaultVal(deviceAbilityOptionPo.getDefaultValue());
                deviceAbilityOptionVo.setMinVal(deviceAbilityOptionPo.getMinVal());
                deviceAbilityOptionVo.setMaxVal(deviceAbilityOptionPo.getMaxVal());
                deviceAbilityOptionVo.setStatus(deviceAbilityOptionPo.getStatus());
                return deviceAbilityOptionVo;
            }).collect(Collectors.toList());

            deviceTypeAbilitysVo.setDeviceAbilityOptions(deviceAbilityOptionVos);
            return deviceTypeAbilitysVo;
        }).collect(Collectors.toList());

        return deviceTypeAbilitysVos;
    }

    /**
     * 根据主键查询 功能
     *
     * @param typeId
     * @return
     */
    public DeviceAbilityVo selectById(Integer typeId) {

        DeviceAbilityPo deviceAbilityPo = deviceAbilityMapper.selectById(typeId);

        DeviceAbilityVo deviceAbilityVo = new DeviceAbilityVo();
        if (deviceAbilityPo != null) {
            deviceAbilityVo.setAbilityName(deviceAbilityPo.getAbilityName());
            deviceAbilityVo.setDirValue(deviceAbilityPo.getDirValue());
            deviceAbilityVo.setWriteStatus(deviceAbilityPo.getWriteStatus());
            deviceAbilityVo.setReadStatus(deviceAbilityPo.getReadStatus());
            deviceAbilityVo.setRunStatus(deviceAbilityPo.getRunStatus());
            deviceAbilityVo.setConfigType(deviceAbilityPo.getConfigType());
            deviceAbilityVo.setAbilityType(deviceAbilityPo.getAbilityType());
            deviceAbilityVo.setMinVal(deviceAbilityPo.getMinVal());
            deviceAbilityVo.setMaxVal(deviceAbilityPo.getMaxVal());
            deviceAbilityVo.setRemark(deviceAbilityPo.getRemark());
            deviceAbilityVo.setId(deviceAbilityPo.getId());

            //根据功能主键 查询该功能下的 选项列表
            List<DeviceAbilityOptionPo> deviceAbilityOptionpos = deviceAbilityOptionMapper.selectOptionsByAbilityId(deviceAbilityPo.getId());
            List<DeviceAbilityOptionVo> deviceAbilityOptionVos = deviceAbilityOptionpos.stream().map(deviceAbilityOptionPo -> {
                DeviceAbilityOptionVo deviceAbilityOptionVo = new DeviceAbilityOptionVo();
                deviceAbilityOptionVo.setId(deviceAbilityOptionPo.getId());
                deviceAbilityOptionVo.setOptionValue(deviceAbilityOptionPo.getOptionValue());
                deviceAbilityOptionVo.setOptionName(deviceAbilityOptionPo.getOptionName());
                deviceAbilityOptionVo.setDefaultVal(deviceAbilityOptionPo.getDefaultValue());
                deviceAbilityOptionVo.setMinVal(deviceAbilityOptionPo.getMinVal());
                deviceAbilityOptionVo.setMaxVal(deviceAbilityOptionPo.getMaxVal());
                deviceAbilityOptionVo.setStatus(deviceAbilityOptionPo.getStatus());
                return deviceAbilityOptionVo;
            }).collect(Collectors.toList());

            deviceAbilityVo.setDeviceAbilityOptions(deviceAbilityOptionVos);
        }
        return deviceAbilityVo;
    }

    /**
     * 删除 该功能 需判断 是否有类型 进行了引用，如果有，则不允许删除
     * 并同时删除该功能下 所有的选项
     *
     * @param abilityId
     * @return
     */
    public ApiResponse<Boolean> deleteAbility(Integer abilityId) throws Exception {

        try {
            //首先进行判断该 功能是否存在。
            DeviceAbilityPo deviceAbilityPo = deviceAbilityMapper.selectById(abilityId);
            if (deviceAbilityPo != null) {

                //根据功能项主键 查询是否有类型 进行了配置使用，如果有，则不允许删除。
                List<DeviceTypeAbilitysPo> deviceTypeAbilitysPos = deviceAbilitysMapper.selectByAbilityId(abilityId);
                if (deviceTypeAbilitysPos != null && deviceTypeAbilitysPos.size() > 0) {
                    return new ApiResponse<>(RetCode.PARAM_ERROR, "该功能项已被设备类型所引用，不可删除");
                }

                Boolean ret = false;
                //先删除 该 功能
                ret = deviceAbilityMapper.deleteById(abilityId) > 0;
                //再删除 选项表中 的选项
                ret = ret && deviceAbilityMapper.deleteOptionByAbilityId(abilityId) > 0;

                return new ApiResponse<>(RetCode.OK, "删除成功");

            } else {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "该功能项不存在");
            }
        } catch (Exception e) {
            log.error("删除能力项失败= {}", e);
            throw new RuntimeException(e);
        }


    }


//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
