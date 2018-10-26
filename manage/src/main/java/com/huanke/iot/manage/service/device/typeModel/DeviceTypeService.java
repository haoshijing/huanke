package com.huanke.iot.manage.service.device.typeModel;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityOptionMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilitySetMapper;
import com.huanke.iot.base.dao.device.ability.DeviceTypeAbilitysMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeAbilitySetMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityOptionPo;
import com.huanke.iot.base.po.device.ability.DeviceAbilitySetPo;
import com.huanke.iot.base.po.device.ability.DeviceTypeAbilitysPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.base.po.device.typeModel.DeviceTypeAbilitySetPo;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import com.huanke.iot.base.util.UniNoCreateUtils;
import com.huanke.iot.manage.vo.request.device.ability.DeviceTypeAbilitysCreateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeAbilitySetCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeQueryRequest;
import com.huanke.iot.manage.vo.response.device.ability.DeviceAbilityOptionVo;
import com.huanke.iot.manage.vo.response.device.ability.DeviceTypeAbilitysVo;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceTypeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//import com.huanke.iot.base.dao.device.ability.DeviceAbilitySetMapper;

/**
 * 设备类型 service
 */
@Repository
@Slf4j
public class DeviceTypeService {

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Autowired
    private DeviceTypeAbilitySetMapper deviceTypeAbilitySetMapper;

    @Autowired
    private DeviceTypeAbilitysMapper deviceTypeAbilitysMapper;

    @Autowired
    private DeviceAbilityMapper deviceAbilityMapper;

    @Autowired
    private DeviceAbilityOptionMapper deviceAbilityOptionMapper;

    @Autowired
    private DeviceAbilitySetMapper deviceAbilitySetMapper;

    @Autowired
    private DeviceModelMapper deviceModelMapper;

    @Autowired
    private DeviceMapper deviceMapper;


    @Value("${accessKeyId}")
    private String accessKeyId;

    @Value("${accessKeySecret}")
    private String accessKeySecret;

    @Value("${bucketUrl}")
    private String bucketUrl;

    @Value("${bucketName}")
    private String bucketName;


    /**
     * 创建或修改类型
     *
     * @param typeRequest
     * @return
     */
    public ApiResponse<Integer> createOrUpdate(DeviceTypeCreateOrUpdateRequest typeRequest) {

        //先保存 类型基本信息
        DeviceTypePo deviceTypePo = new DeviceTypePo();
        if (typeRequest != null) {
            BeanUtils.copyProperties(typeRequest, deviceTypePo);
        }
        if (typeRequest.getId() != null && typeRequest.getId() > 0) {
            if (CommonConstant.STATUS_DEL.equals(deviceTypePo.getStatus())) {
                deviceTypePo.setStatus(CommonConstant.STATUS_DEL);
            } else {
                deviceTypePo.setStatus(CommonConstant.STATUS_YES);
            }
            deviceTypePo.setLastUpdateTime(System.currentTimeMillis());
            deviceTypeMapper.updateById(deviceTypePo);
        } else {
            deviceTypePo.setStatus(CommonConstant.STATUS_YES);
            deviceTypePo.setTypeNo(UniNoCreateUtils.createNo(DeviceConstant.DEVICE_UNI_NO_TYPE));
            deviceTypePo.setCreateTime(System.currentTimeMillis());
            deviceTypeMapper.insert(deviceTypePo);
        }

        //先删除 已保存的功能集 ，再保存 类型的功能集
        deviceTypeAbilitysMapper.deleteByTypeId(deviceTypePo.getId());

        List<DeviceTypeAbilitysCreateRequest> deviceTypeAbilitysCreateRequests = typeRequest.getDeviceTypeAbilitys();
        if (deviceTypeAbilitysCreateRequests != null && deviceTypeAbilitysCreateRequests.size() > 0) {
            //遍历功能集
            deviceTypeAbilitysCreateRequests.stream().forEach(deviceTypeAbilitysCreateRequest -> {
                DeviceTypeAbilitysPo deviceTypeAbilitysPo = new DeviceTypeAbilitysPo();
                deviceTypeAbilitysPo.setAbilityId(deviceTypeAbilitysCreateRequest.getAbilityId());
                deviceTypeAbilitysPo.setTypeId(deviceTypePo.getId());

                deviceTypeAbilitysMapper.insert(deviceTypeAbilitysPo);

            });

        }
        return new ApiResponse<>(deviceTypePo.getId());

    }


    /**
     * 删除 类型（物理删除）  该类型下的型号如何操作？？？？？？？？？？？？？？
     *
     * @param typeId
     * @return
     */
    public Boolean destoryDeviceType(Integer typeId) {

        DeviceTypePo deviceTypePo = new DeviceTypePo();
        deviceTypePo.setId(typeId);
        Boolean ret = false;
        //判断当 类型id不为空时
        //先删除 该 功能
        ret = deviceTypeMapper.deleteById(typeId) > 0;
        return ret;
    }

    /**
     * 删除 类型（逻辑删除）  该类型下的型号如何操作？？？？？？？？？？？？？？
     *
     * @param typeId
     * @return
     */
    public ApiResponse<Boolean> deleteDeviceType(Integer typeId) {

        Boolean ret = false;

        List<DevicePo> devicePos = deviceMapper.selectByTypeId(typeId);
        if(devicePos!=null&&devicePos.size()>0){
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该类型已有设备，无法删除！");
        }

        List<DeviceModelPo> deviceModelPos = deviceModelMapper.selectByTypeId(typeId);
        if(deviceModelPos!=null&&deviceModelPos.size()>0){
            return new ApiResponse<>(RetCode.PARAM_ERROR, "已有型号使用该类型，无法删除！");
        }

        ret = deviceTypeMapper.updateStatusById(typeId, CommonConstant.STATUS_DEL) > 0;
        if(ret){
            return new ApiResponse<>(RetCode.OK, "删除成功",ret);
        }else {
            return new ApiResponse<>(RetCode.ERROR, "删除失败",ret);
        }
    }

    /**
     * 创建 或修改 类型的功能集
     *
     * @param request
     * @return
     */
    public ApiResponse<Boolean> createOrUpdateDeviceTypeAbilitySet(DeviceTypeAbilitySetCreateOrUpdateRequest request) {

        int effectCount = 0;
        Boolean ret = false;
        // 主键 不能为空
        if (request.getAbilitySetId() == null || request.getAbilitySetId() <= 0) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该功能集不存在");
        }
        if (request.getTypeId() == null || request.getTypeId() <= 0) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该类型不存在");
        }

        //保存前 先进行 验证 该类型 和该 功能集是否存在
        DeviceTypePo queryDeviceTypePo = deviceTypeMapper.selectById(request.getTypeId());
        DeviceAbilitySetPo queryDeviceAbilitySetPo = deviceAbilitySetMapper.selectById(request.getAbilitySetId());

        if (null == queryDeviceTypePo) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该类型不存在");
        }

        if (null == queryDeviceAbilitySetPo) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该功能集不存在");
        }


        //只有 类型与功能集都存在才会进行保存
        DeviceTypeAbilitySetPo deviceTypeAbilitySetPo = new DeviceTypeAbilitySetPo();
        deviceTypeAbilitySetPo.setAbilitySetId(request.getAbilitySetId());
        deviceTypeAbilitySetPo.setTypeId(request.getTypeId());

        //当主键存在的时候 说明是更新  否则是新增
        if (request.getId() != null && request.getId() > 0) {
            deviceTypeAbilitySetPo.setId(request.getId());
            deviceTypeAbilitySetPo.setLastUpdateTime(System.currentTimeMillis());
        } else {
            //当新增的时候 判断 该类型是否已经添加有功能集，如果有不允许添加
            DeviceTypeAbilitySetPo queryTypeAbilitySetPo = deviceTypeAbilitySetMapper.selectByTypeId(request.getTypeId());
            if (queryTypeAbilitySetPo != null) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "该类型已添加功能集，不可重复添加");
            }

            deviceTypeAbilitySetPo.setCreateTime(System.currentTimeMillis());
        }

        ret = deviceTypeAbilitySetMapper.insert(deviceTypeAbilitySetPo) > 0;

        return new ApiResponse<>(ret);
    }


    /**
     * 查询类型列表
     *
     * @param request
     * @return
     */
    public List<DeviceTypeVo> selectList(DeviceTypeQueryRequest request) {

        DeviceTypePo queryDeviceTypePo = new DeviceTypePo();
        queryDeviceTypePo.setName(request.getName());
        queryDeviceTypePo.setTypeNo(request.getTypeNo());

        queryDeviceTypePo.setStatus(request.getStatus());

        Integer offset = (request.getPage() - 1) * request.getLimit();
        Integer limit = request.getLimit();

        //查询 类型列表
        List<DeviceTypePo> deviceTypePos = deviceTypeMapper.selectList(queryDeviceTypePo, limit, offset);
        return deviceTypePos.stream().map(deviceTypePo -> {
            DeviceTypeVo deviceTypeVo = new DeviceTypeVo();
            if (deviceTypePo != null) {
                deviceTypeVo.setName(deviceTypePo.getName());
                deviceTypeVo.setTypeNo(deviceTypePo.getTypeNo());
                deviceTypeVo.setIcon(deviceTypePo.getIcon());
                deviceTypeVo.setStopWatch(deviceTypePo.getStopWatch());
                deviceTypeVo.setSource(deviceTypePo.getSource());
                deviceTypeVo.setRemark(deviceTypePo.getRemark());
                deviceTypeVo.setId(deviceTypePo.getId());
            }

            //查询该 类型的 功能集合
            List<DeviceTypeAbilitysVo> deviceTypeAbilitysVos = selectAbilitysByTypeId(deviceTypePo.getId());

            deviceTypeVo.setDeviceTypeAbilitys(deviceTypeAbilitysVos);
            return deviceTypeVo;
        }).collect(Collectors.toList());
    }

    public ApiResponse<Integer> selectCount(Integer status)throws Exception{
        DeviceTypePo deviceTypePo =new DeviceTypePo();
        deviceTypePo.setStatus(status);
        return new ApiResponse<>(RetCode.OK,"查询类型总数成功",deviceTypeMapper.selectCount(deviceTypePo));
    }

    /**
     * 根据类型集合查询该客户可用的设备类型信息
     *
     * @param typeIds
     * @return
     */
    public List<DeviceTypeVo> selectListByTypeIds(String typeIds) {

        List<String> typeIdList = Arrays.asList(typeIds.split(","));
        //查询 类型列表
        List<DeviceTypePo> deviceTypePos = deviceTypeMapper.selectListByTypeIds(typeIdList);
        return deviceTypePos.stream().map(deviceTypePo -> {
            DeviceTypeVo deviceTypeVo = new DeviceTypeVo();
            if (deviceTypePo != null) {
                deviceTypeVo.setName(deviceTypePo.getName());
                deviceTypeVo.setTypeNo(deviceTypePo.getTypeNo());
                deviceTypeVo.setIcon(deviceTypePo.getIcon());
                deviceTypeVo.setStopWatch(deviceTypePo.getStopWatch());
                deviceTypeVo.setSource(deviceTypePo.getSource());
                deviceTypeVo.setRemark(deviceTypePo.getRemark());
                deviceTypeVo.setId(deviceTypePo.getId());
            }

            //查询该 类型的 功能集合
            List<DeviceTypeAbilitysVo> deviceTypeAbilitysVos = selectAbilitysByTypeId(deviceTypePo.getId());

            deviceTypeVo.setDeviceTypeAbilitys(deviceTypeAbilitysVos);
            return deviceTypeVo;
        }).collect(Collectors.toList());
    }


    /**
     * 查询所有的设备类型
     *
     * @return
     */
    public List<DeviceTypeVo> selectAllTypes() {

        //查询 类型列表
        List<DeviceTypePo> deviceTypePos = deviceTypeMapper.selectAllTypes();
        return deviceTypePos.stream().map(deviceTypePo -> {
            DeviceTypeVo deviceTypeVo = new DeviceTypeVo();
            if (deviceTypePo != null) {
                deviceTypeVo.setName(deviceTypePo.getName());
                deviceTypeVo.setTypeNo(deviceTypePo.getTypeNo());
                deviceTypeVo.setIcon(deviceTypePo.getIcon());
                deviceTypeVo.setStopWatch(deviceTypePo.getStopWatch());
                deviceTypeVo.setSource(deviceTypePo.getSource());
                deviceTypeVo.setRemark(deviceTypePo.getRemark());
                deviceTypeVo.setId(deviceTypePo.getId());
            }

            //查询该 类型的 功能集合
            List<DeviceTypeAbilitysVo> deviceTypeAbilitysVos = selectAbilitysByTypeId(deviceTypePo.getId());

            deviceTypeVo.setDeviceTypeAbilitys(deviceTypeAbilitysVos);
            return deviceTypeVo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据主键查询类型
     *
     * @param typeId
     * @return
     */
    public DeviceTypeVo selectById(Integer typeId) {

        DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(typeId);

        DeviceTypeVo deviceTypeVo = new DeviceTypeVo();
        if (deviceTypePo != null) {
            deviceTypeVo.setName(deviceTypePo.getName());
            deviceTypeVo.setTypeNo(deviceTypePo.getTypeNo());
            deviceTypeVo.setIcon(deviceTypePo.getIcon());
            deviceTypeVo.setStopWatch(deviceTypePo.getStopWatch());
            deviceTypeVo.setSource(deviceTypePo.getSource());
            deviceTypeVo.setRemark(deviceTypePo.getRemark());
            deviceTypeVo.setId(deviceTypePo.getId());
            List<DeviceTypeAbilitysVo> deviceTypeAbilitysVos = selectAbilitysByTypeId(deviceTypePo.getId());
            deviceTypeVo.setDeviceTypeAbilitys(deviceTypeAbilitysVos);
        }
        return deviceTypeVo;
    }


    /**
     * 根据类型主键 查询 该类型的功能集 下的功能
     *
     * @param typeId
     * @return
     */

    public List<DeviceTypeAbilitysVo> selectAbilitysByTypeId(Integer typeId) {


        /*查询类型的功能项 过滤已删除的，（正常、禁用）*/
        List<DeviceTypeAbilitysPo> deviceTypeAbilitysPos = deviceAbilityMapper.selectAbilityListByTypeId(typeId);
        List<DeviceTypeAbilitysVo> deviceTypeAbilitysVos = deviceTypeAbilitysPos.stream().map(deviceTypeAbilitysPo -> {

            DeviceTypeAbilitysVo deviceTypeAbilitysVo = new DeviceTypeAbilitysVo();
            if (deviceTypeAbilitysPo != null) {
                deviceTypeAbilitysVo.setAbilityId(deviceTypeAbilitysPo.getAbilityId());
                deviceTypeAbilitysVo.setAbilityName(deviceTypeAbilitysPo.getAbilityName());
                deviceTypeAbilitysVo.setId(deviceTypeAbilitysPo.getId());
                deviceTypeAbilitysVo.setTypeId(deviceTypeAbilitysPo.getTypeId());
                deviceTypeAbilitysVo.setAbilityType(deviceTypeAbilitysPo.getAbilityType());

                List<DeviceAbilityOptionPo> deviceAbilityOptionPos = deviceAbilityOptionMapper.selectOptionsByAbilityId(deviceTypeAbilitysPo.getAbilityId());
                if (deviceAbilityOptionPos != null && deviceAbilityOptionPos.size() > 0) {
                    List<DeviceAbilityOptionVo> deviceAbilityOptionVos = deviceAbilityOptionPos.stream().map(deviceAbilityOptionPo -> {
                        DeviceAbilityOptionVo deviceAbilityOptionVo = new DeviceAbilityOptionVo();

                        deviceAbilityOptionVo.setId(deviceAbilityOptionPo.getId());
                        deviceAbilityOptionVo.setOptionName(deviceAbilityOptionPo.getOptionName());
                        deviceAbilityOptionVo.setOptionValue(deviceAbilityOptionPo.getOptionValue());
                        deviceAbilityOptionVo.setStatus(deviceAbilityOptionPo.getStatus());
                        return deviceAbilityOptionVo;
                    }).collect(Collectors.toList());

                    deviceTypeAbilitysVo.setDeviceAbilityOptions(deviceAbilityOptionVos);
                }

            }

            return deviceTypeAbilitysVo;
        }).collect(Collectors.toList());


        return deviceTypeAbilitysVos;

    }


//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
