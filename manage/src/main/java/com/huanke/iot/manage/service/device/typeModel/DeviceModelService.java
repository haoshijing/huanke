package com.huanke.iot.manage.service.device.typeModel;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAblityMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAblityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAblityPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelQueryRequest;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceModelAblityVo;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceModelVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DeviceModelService {

    @Autowired
    private DeviceTypeService deviceTypeService;

    @Autowired
    private DeviceModelMapper deviceModelMapper;

    @Autowired
    private DeviceModelAblityMapper deviceModelAblityMapper;

    @Autowired
    private DeviceModelAblityOptionMapper deviceModelAblityOptionMapper;

    @Autowired
    private DeviceAblityOptionMapper deviceAblityOptionMapper;


    @Value("${accessKeyId}")
    private String accessKeyId;

    @Value("${accessKeySecret}")
    private String accessKeySecret;

    @Value("${bucketUrl}")
    private String bucketUrl;

    @Value("${bucketName}")
    private String bucketName;

    /**
     * 创建 或者更新 型号
     *
     * @param modelRequest
     * @return
     */
    public Boolean createOrUpdate(DeviceModelCreateOrUpdateRequest modelRequest) {

        //先保存 型号
        int effectCount = 0;
        DeviceModelPo deviceModelPo = new DeviceModelPo();
        BeanUtils.copyProperties(modelRequest, deviceModelPo);
        if (modelRequest.getId() != null && modelRequest.getId() > 0) {
            deviceModelPo.setLastUpdateTime(System.currentTimeMillis());
            effectCount = deviceModelMapper.updateById(deviceModelPo);
        } else {
            deviceModelPo.setCreateTime(System.currentTimeMillis());
            effectCount = deviceModelMapper.insert(deviceModelPo);
        }

        //随后取出 型号的 功能
        List<DeviceModelCreateOrUpdateRequest.DeviceModelAblityRequest> deviceModelAblityRequests = modelRequest.getDeviceModelAblitys();
        if (deviceModelAblityRequests != null && deviceModelAblityRequests.size() > 0) {
            //遍历当前 型号的能力 b
            for (int i = 0; i < deviceModelAblityRequests.size(); i++) {
                DeviceModelCreateOrUpdateRequest.DeviceModelAblityRequest deviceModelAblityRequest = deviceModelAblityRequests.get(i);
                DeviceModelAblityPo deviceModelAblityPo = new DeviceModelAblityPo();

                deviceModelAblityPo.setModelId(deviceModelPo.getId());
                deviceModelAblityPo.setDefinedName(deviceModelAblityRequest.getDefinedName());
                deviceModelAblityPo.setAblityId(deviceModelAblityRequest.getAblityId());
                deviceModelAblityPo.setStatus(deviceModelAblityRequest.getStatus());

                //判断当前型号的功能 主键是否为空，不为空是更新，空是 保存
                //todo 选项未保存
                if (deviceModelAblityRequest.getId() != null) {
                    deviceModelAblityPo.setId(deviceModelAblityRequest.getId());
                    deviceModelAblityPo.setLastUpdateTime(System.currentTimeMillis());
                    deviceModelAblityMapper.updateById(deviceModelAblityPo);
                } else {
                    deviceModelAblityPo.setCreateTime(System.currentTimeMillis());
                    deviceModelAblityMapper.insert(deviceModelAblityPo);
                }
            }
        }
        return effectCount > 0;
    }

    /**
     * 查询 型号的列表
     *
     * @param request
     * @return
     */
    public List<DeviceModelVo> selectList(DeviceModelQueryRequest request) {

        DeviceModelPo queryDeviceModelPo = new DeviceModelPo();
        queryDeviceModelPo.setName(request.getName());
        queryDeviceModelPo.setCustomerId(request.getCustomerId());
        queryDeviceModelPo.setProductId(request.getProductId());
        queryDeviceModelPo.setTypeId(request.getTypeId());
        queryDeviceModelPo.setStatus(request.getStatus());

        Integer offset = (request.getPage() - 1) * request.getLimit();
        Integer limit = request.getLimit();

        List<DeviceModelPo> deviceModelPos = deviceModelMapper.selectList(queryDeviceModelPo, limit, offset);
        return deviceModelPos.stream().map(deviceModelPo -> {
            DeviceModelVo deviceModelVo = new DeviceModelVo();
            deviceModelVo.setName(deviceModelPo.getName());
            deviceModelVo.setCustomerId(deviceModelPo.getCustomerId());
            deviceModelVo.setProductId(deviceModelPo.getProductId());
            deviceModelVo.setTypeId(deviceModelPo.getTypeId());
            deviceModelVo.setRemark(deviceModelPo.getRemark());
            deviceModelVo.setStatus(deviceModelPo.getStatus());
            deviceModelVo.setVersion(deviceModelPo.getVersion());
            deviceModelVo.setIcon(deviceModelVo.getIcon());
            deviceModelVo.setId(deviceModelPo.getId());
            return deviceModelVo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据类型ID 查询型号列表
     *
     * @param typeId
     * @return
     */
    public List<DeviceModelVo> selectByTypeId(Integer typeId) {

        DeviceModelPo queryDeviceModelPo = new DeviceModelPo();

        List<DeviceModelPo> deviceModelPos = deviceModelMapper.selectByTypeId(typeId);
        return deviceModelPos.stream().map(deviceModelPo -> {
            DeviceModelVo deviceModelVo = new DeviceModelVo();
            deviceModelVo.setName(deviceModelPo.getName());
            deviceModelVo.setCustomerId(deviceModelPo.getCustomerId());
            deviceModelVo.setProductId(deviceModelPo.getProductId());
            deviceModelVo.setTypeId(deviceModelPo.getTypeId());
            deviceModelVo.setRemark(deviceModelPo.getRemark());
            deviceModelVo.setStatus(deviceModelPo.getStatus());
            deviceModelVo.setVersion(deviceModelPo.getVersion());
            deviceModelVo.setIcon(deviceModelVo.getIcon());
            deviceModelVo.setId(deviceModelPo.getId());
            return deviceModelVo;
        }).collect(Collectors.toList());

    }


    /**
     * 根据型号主键查询 型号
     *
     * @param id
     * @return
     */
    public DeviceModelVo selectById(Integer id) {


        DeviceModelPo deviceModelPo = deviceModelMapper.selectById(id);

        DeviceModelVo deviceModelVo = new DeviceModelVo();
        deviceModelVo.setName(deviceModelPo.getName());
        deviceModelVo.setCustomerId(deviceModelPo.getCustomerId());
        deviceModelVo.setProductId(deviceModelPo.getProductId());
        deviceModelVo.setTypeId(deviceModelPo.getTypeId());
        deviceModelVo.setRemark(deviceModelPo.getRemark());
        deviceModelVo.setStatus(deviceModelPo.getStatus());
        deviceModelVo.setVersion(deviceModelPo.getVersion());
        deviceModelVo.setIcon(deviceModelVo.getIcon());
        deviceModelVo.setId(deviceModelPo.getId());

        return deviceModelVo;
    }
//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }


    /**
     * 添加型号能力 以及能力的选项
     *
     * @param modelAblityRequest
     * @return
     */
    public ApiResponse<Boolean> createDeviceModelAblity(DeviceModelCreateOrUpdateRequest.DeviceModelAblityRequest modelAblityRequest) {

        int effectCount = 0;
        Boolean ret = true;

//        if (null == modelAblityRequest.getAblityId()) {
//            return new ApiResponse<>(RetCode.PARAM_ERROR, "功能主键不能为空");
//        }
//        if (null == modelAblityRequest.getModelId()) {
//            return new ApiResponse<>(RetCode.PARAM_ERROR, "型号主键不能为空");
//        }
//
//        //先进行判断 该型号是否存在
//        DeviceModelPo querydeviceModelPo = new DeviceModelPo();
//        querydeviceModelPo = deviceModelMapper.selectById(modelAblityRequest.getModelId());
//        if (querydeviceModelPo == null) {
//            return new ApiResponse<>(RetCode.PARAM_ERROR, "该型号不存在");
//        }
//        //判断该型号的类型主键是否存在
//        if (querydeviceModelPo.getTypeId() == null) {
//            return new ApiResponse<>(RetCode.PARAM_ERROR, "该型号的类型主键不存在");
//        }

        try {

            // 查询 该型号所在类型的功能集中的功能
//            List<DeviceAblityVo> deviceAblityVos = deviceTypeService.selectAblitysByTypeId(querydeviceModelPo.getTypeId());
//            if (deviceAblityVos != null && deviceAblityVos.size() > 0) {
//                //遍历功能集的功能 插入 型号功能表
//                for (int i = 0; i < deviceAblityVos.size(); i++) {
//                    DeviceAblityVo deviceAblityVo = deviceAblityVos.get(i);
//                    DeviceModelAblityPo deviceModelAblityPo = new DeviceModelAblityPo();
//
//                    //查询 型号的功能。
//                    deviceModelAblityPo.setAblityId(deviceAblityVo.getId());
//                    deviceModelAblityPo.setModelId(modelAblityRequest.getModelId());
//                    deviceModelAblityPo.setDefinedName(deviceAblityVo.getAblityName());//默认名称
//                    deviceModelAblityPo.setStatus(deviceAblityVo.getWriteStatus());//默认 该能力的状态
//                    deviceModelAblityPo.setCreateTime(System.currentTimeMillis());
//                    deviceModelAblityMapper.insert(deviceModelAblityPo);
//
//                    //根据 功能主键 查询该功能主键下的 选项
//                    List<DeviceAblityOptionPo> deviceAblityOptionPos = deviceAblityOptionMapper.selectOptionsByAblityId(deviceAblityVo.getId());
//
//                    if (deviceAblityOptionPos != null && deviceAblityOptionPos.size() > 0) {
//
//                        //遍历选项列表，插入 默认选项名称。
//                        for (int x = 0; x < deviceAblityOptionPos.size(); x++) {
//                            DeviceAblityOptionPo tempDeviceAblityOptionPo = deviceAblityOptionPos.get(x);
//
//                            DeviceModelAblityOptionPo deviceModelAblityOptionPo = new DeviceModelAblityOptionPo();
//                            deviceModelAblityOptionPo.setDefinedName(tempDeviceAblityOptionPo.getOptionName()); //设置选项的默认名称
//                            deviceModelAblityOptionPo.setModelAblityId(deviceModelAblityPo.getId());
//                            deviceModelAblityOptionPo.setCreateTime(System.currentTimeMillis());
//                            deviceModelAblityOptionMapper.insert(deviceModelAblityOptionPo);
//                        }
//
//                    }
//                }
//            } else {
//                return new ApiResponse<>(RetCode.PARAM_ERROR, "该型号所在类型的功能集功能不存在");
//            }
        } catch (Exception e) {
            log.error("添加型号能力 以及能力的选项 错误", e);
        }
        return new ApiResponse<>(ret);
    }


    /**
     * 根据类型主键 查询 该型号的功能
     *
     * @param modelId
     * @return
     */
    public List<DeviceModelAblityVo> selectModelAblitysByModelId(Integer modelId) {

        List<DeviceModelAblityPo> deviceModelAblityPos = deviceModelAblityMapper.selectByModelId(modelId);

        return deviceModelAblityPos.stream().map(deviceModelAblityPo -> {
            DeviceModelAblityVo deviceModelAblityVo = new DeviceModelAblityVo();
            deviceModelAblityVo.setId(deviceModelAblityPo.getId());
            deviceModelAblityVo.setDefinedName(deviceModelAblityPo.getDefinedName());
            deviceModelAblityVo.setAblityId(deviceModelAblityPo.getAblityId());
            deviceModelAblityVo.setModelId(deviceModelAblityPo.getModelId());
            deviceModelAblityVo.setStatus(deviceModelAblityPo.getStatus());
            return deviceModelAblityVo;
        }).collect(Collectors.toList());

    }

    /**
     * 根据主键 删除 型号
     * @param modelId
     * @return
     */
    public Boolean deleteModelById(Integer modelId) {

        Boolean ret = true;
        ret = deviceModelMapper.deleteById(modelId) > 0;
        return ret;
    }

}
