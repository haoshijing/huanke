package com.huanke.iot.manage.service.device.typeModel;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceAblitySetMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceTypeAblitysMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeAblitySetMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.po.device.alibity.DeviceAblityPo;
import com.huanke.iot.base.po.device.alibity.DeviceAblitySetPo;
import com.huanke.iot.base.po.device.alibity.DeviceTypeAblitysPo;
import com.huanke.iot.base.po.device.typeModel.DeviceTypeAblitySetPo;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceTypeAblitysCreateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeAblitySetCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeQueryRequest;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityVo;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceTypeAblitysVo;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceTypeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

//import com.huanke.iot.base.dao.device.ablity.DeviceAblitySetMapper;

/**
 * 设备类型 service
 */
@Repository
@Slf4j
public class DeviceTypeService {

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Autowired
    private DeviceTypeAblitySetMapper deviceTypeAblitySetMapper;

    @Autowired
    private DeviceTypeAblitysMapper deviceTypeAblitysMapper;

    @Autowired
    private DeviceAblityMapper deviceAblityMapper;

    @Autowired
    private DeviceAblitySetMapper deviceAblitySetMapper;

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
        BeanUtils.copyProperties(typeRequest, deviceTypePo);
        deviceTypePo.setStatus(CommonConstant.STATUS_YES);
        if (typeRequest.getId() != null && typeRequest.getId() > 0) {
            deviceTypePo.setLastUpdateTime(System.currentTimeMillis());
            deviceTypeMapper.updateById(deviceTypePo);
        } else {
            deviceTypePo.setCreateTime(System.currentTimeMillis());
            deviceTypeMapper.insert(deviceTypePo);
        }

        //再保存 类型的功能集
        List<DeviceTypeAblitysCreateRequest> deviceTypeAblitysCreateRequests = typeRequest.getDeviceTypeAblitys();
        if (deviceTypeAblitysCreateRequests != null && deviceTypeAblitysCreateRequests.size() > 0) {
            //遍历功能集
            deviceTypeAblitysCreateRequests.stream().forEach(deviceTypeAblitysCreateRequest -> {
                DeviceTypeAblitysPo deviceTypeAblitysPo = new DeviceTypeAblitysPo();
                deviceTypeAblitysPo.setAblityId(deviceTypeAblitysCreateRequest.getAblityId());
                deviceTypeAblitysPo.setTypeId(deviceTypePo.getId());

                //如果该功能id不为空 则是更新 否则为新增
                if (deviceTypeAblitysCreateRequest.getId() != null && deviceTypeAblitysCreateRequest.getId() > 0) {
                    deviceTypeAblitysPo.setId(deviceTypeAblitysCreateRequest.getId());
                    deviceTypeAblitysPo.setLastUpdateTime(System.currentTimeMillis());
                    deviceTypeAblitysMapper.updateById(deviceTypeAblitysPo);
                } else {
                    deviceTypeAblitysPo.setCreateTime(System.currentTimeMillis());
                    deviceTypeAblitysMapper.insert(deviceTypeAblitysPo);
                }

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
    public Boolean deleteDeviceType(Integer typeId) {

        Boolean ret = false;
        ret = deviceTypeMapper.updateStatusById(typeId,CommonConstant.STATUS_DEL) > 0;
        return ret;
    }

    /**
     * 创建 或修改 类型的功能集
     *
     * @param request
     * @return
     */
    public ApiResponse<Boolean> createOrUpdateDeviceTypeAblitySet(DeviceTypeAblitySetCreateOrUpdateRequest request) {

        int effectCount = 0;
        Boolean ret = false;
        // 主键 不能为空
        if (request.getAblitySetId() == null || request.getAblitySetId() <= 0) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该功能集不存在");
        }
        if (request.getTypeId() == null || request.getTypeId() <= 0) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该类型不存在");
        }

        //保存前 先进行 验证 该类型 和该 功能集是否存在
        DeviceTypePo queryDeviceTypePo = deviceTypeMapper.selectById(request.getTypeId());
        DeviceAblitySetPo queryDeviceAblitySetPo = deviceAblitySetMapper.selectById(request.getAblitySetId());

        if (null == queryDeviceTypePo) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该类型不存在");
        }

        if (null == queryDeviceAblitySetPo) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该功能集不存在");
        }


        //只有 类型与功能集都存在才会进行保存
        DeviceTypeAblitySetPo deviceTypeAblitySetPo = new DeviceTypeAblitySetPo();
        deviceTypeAblitySetPo.setAblitySetId(request.getAblitySetId());
        deviceTypeAblitySetPo.setTypeId(request.getTypeId());

        //当主键存在的时候 说明是更新  否则是新增
        if (request.getId() != null && request.getId() > 0) {
            deviceTypeAblitySetPo.setId(request.getId());
            deviceTypeAblitySetPo.setLastUpdateTime(System.currentTimeMillis());
        } else {
            //当新增的时候 判断 该类型是否已经添加有功能集，如果有不允许添加
            DeviceTypeAblitySetPo queryTypeAblitySetPo = deviceTypeAblitySetMapper.selectByTypeId(request.getTypeId());
            if (queryTypeAblitySetPo != null) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "该类型已添加功能集，不可重复添加");
            }

            deviceTypeAblitySetPo.setCreateTime(System.currentTimeMillis());
        }

        ret = deviceTypeAblitySetMapper.insert(deviceTypeAblitySetPo) > 0;

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

        Integer offset = (request.getPage() - 1) * request.getLimit();
        Integer limit = request.getLimit();


        //查询 类型列表
        List<DeviceTypePo> deviceTypePos = deviceTypeMapper.selectList(queryDeviceTypePo, limit, offset);
        return deviceTypePos.stream().map(deviceTypePo -> {
            DeviceTypeVo deviceTypeVo = new DeviceTypeVo();
            if(deviceTypePo!=null){
                deviceTypeVo.setName(deviceTypePo.getName());
                deviceTypeVo.setTypeNo(deviceTypePo.getTypeNo());
                deviceTypeVo.setIcon(deviceTypePo.getIcon());
                deviceTypeVo.setStopWatch(deviceTypePo.getStopWatch());
                deviceTypeVo.setSource(deviceTypePo.getSource());
                deviceTypeVo.setRemark(deviceTypePo.getRemark());
                deviceTypeVo.setId(deviceTypePo.getId());
            }

            //查询该 类型的 功能集合
            List<DeviceTypeAblitysVo> deviceTypeAblitysVos = selectAblitysByTypeId(deviceTypePo.getId());

            deviceTypeVo.setDeviceTypeAblitys(deviceTypeAblitysVos);
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
        if(deviceTypePo!=null){
            deviceTypeVo.setName(deviceTypePo.getName());
            deviceTypeVo.setTypeNo(deviceTypePo.getTypeNo());
            deviceTypeVo.setIcon(deviceTypePo.getIcon());
            deviceTypeVo.setStopWatch(deviceTypePo.getStopWatch());
            deviceTypeVo.setSource(deviceTypePo.getSource());
            deviceTypeVo.setRemark(deviceTypePo.getRemark());
            deviceTypeVo.setId(deviceTypePo.getId());
            List<DeviceTypeAblitysVo> deviceTypeAblitysVos = selectAblitysByTypeId(deviceTypePo.getId());
            deviceTypeVo.setDeviceTypeAblitys(deviceTypeAblitysVos);
        }
        return deviceTypeVo;
    }


    /**
     * 根据类型主键 查询 该类型的功能集 下的功能
     *
     * @param typeId
     * @return
     */

    public List<DeviceTypeAblitysVo> selectAblitysByTypeId(Integer typeId) {


        List<DeviceTypeAblitysPo> deviceTypeAblitysPos = deviceAblityMapper.selectAblityListByTypeId(typeId);
        List<DeviceTypeAblitysVo> deviceTypeAblitysVos = deviceTypeAblitysPos.stream().map(deviceTypeAblitysPo -> {

            DeviceTypeAblitysVo deviceTypeAblitysVo = new DeviceTypeAblitysVo();
            if(deviceTypeAblitysPo!=null){
                deviceTypeAblitysVo.setAblityId(deviceTypeAblitysPo.getAblityId());
                deviceTypeAblitysVo.setAblityName(deviceTypeAblitysPo.getAblityName());
                deviceTypeAblitysVo.setId(deviceTypeAblitysPo.getId());
                deviceTypeAblitysVo.setTypeId(deviceTypeAblitysPo.getTypeId());
            }

            return deviceTypeAblitysVo;
        }).collect(Collectors.toList());


        return deviceTypeAblitysVos;

    }
//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
