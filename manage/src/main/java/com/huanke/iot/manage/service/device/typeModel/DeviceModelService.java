package com.huanke.iot.manage.service.device.typeModel;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.device.DeviceIdPoolMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAblityMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAblityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.format.DeviceModelFormatItemMapper;
import com.huanke.iot.base.dao.format.DeviceModelFormatMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.device.DeviceIdPoolPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAblityOptionPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAblityPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.base.po.format.DeviceModelFormatItemPo;
import com.huanke.iot.base.po.format.DeviceModelFormatPo;
import com.huanke.iot.manage.service.device.operate.DeviceOperateService;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelFormatCreateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelQueryRequest;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceModelAblityVo;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceModelVo;
import com.huanke.iot.manage.vo.response.format.ModelFormatVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DeviceModelService {

    @Autowired
    private DeviceOperateService deviceOperateService;

    @Autowired
    private DeviceModelMapper deviceModelMapper;

    @Autowired
    private DeviceModelAblityMapper deviceModelAblityMapper;

    @Autowired
    private DeviceModelAblityOptionMapper deviceModelAblityOptionMapper;

    @Autowired
    private DeviceAblityOptionMapper deviceAblityOptionMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private DeviceModelFormatMapper deviceModelFormatMapper;

    @Autowired
    private DeviceModelFormatItemMapper deviceModelFormatItemMapper;

    @Autowired
    private DeviceIdPoolMapper deviceIdPoolMapper;


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
    public ApiResponse<Integer> createOrUpdate(DeviceModelCreateOrUpdateRequest modelRequest) {

        //先保存 型号
        int effectCount = 0;
        Boolean ret = true;
        DeviceModelPo deviceModelPo = new DeviceModelPo();

        try {
            if (modelRequest != null) {
                BeanUtils.copyProperties(modelRequest, deviceModelPo);
            }
            //校验 productId 不可为空
            if (StringUtils.isNotBlank(modelRequest.getProductId())) {
                DeviceModelPo queryDeviceModelPo = new DeviceModelPo();
                queryDeviceModelPo = deviceModelMapper.selectByProductId(modelRequest.getProductId());

                if (modelRequest.getId() != null && modelRequest.getId() > 0) {
                    //如果 存在此productid 的型号 且 主键 和 保存的型号 不一致时不允许
                    if (null != queryDeviceModelPo && !modelRequest.getId().equals(queryDeviceModelPo.getId())) {
                        return new ApiResponse<>(RetCode.PARAM_ERROR, "已存在此产品id。");
                    }
                    deviceModelPo.setLastUpdateTime(System.currentTimeMillis());
                    //如果不是删除，则设置成 正常状态
                    if (!CommonConstant.STATUS_DEL.equals(modelRequest.getStatus())) {
                        deviceModelPo.setStatus(CommonConstant.STATUS_YES);
                    }
                    deviceModelMapper.updateById(deviceModelPo);
                } else {
                    //新增的时候 如果存在此productid 则不可保存
                    if (null != queryDeviceModelPo) {
                        return new ApiResponse<>(RetCode.PARAM_ERROR, "已存在此产品id。");
                    }
                    deviceModelPo.setCreateTime(System.currentTimeMillis());
                    deviceModelPo.setStatus(CommonConstant.STATUS_YES);
                    deviceModelMapper.insert(deviceModelPo);

                    //增加wxdeviceid配额 //todo 默认增加 200
                    deviceOperateService.createWxDeviceIdPools(deviceModelPo.getCustomerId(), deviceModelPo.getProductId(), DeviceConstant.WXDEVICEID_DEF_COUNT);
                }


                //随后保存型号的自定义功能
                List<DeviceModelCreateOrUpdateRequest.DeviceModelAblityRequest> deviceModelAblityRequests = modelRequest.getDeviceModelAblitys();
                if (deviceModelAblityRequests != null && deviceModelAblityRequests.size() > 0) {

                    this.createOrUpdateModelAblitys(deviceModelAblityRequests, deviceModelPo.getId());
                }

                //保存 型号的自定义版式
                DeviceModelFormatCreateRequest modelFormat = modelRequest.getDeviceModelFormat();

                if (modelFormat != null && modelFormat.getModelFormatPages() != null && modelFormat.getModelFormatPages().size() > 0) {
                    this.createOrUpdateModelFormat(modelFormat, deviceModelPo.getId(), deviceModelPo.getFormatId());
                }

                return new ApiResponse<>(deviceModelPo.getId());
            } else {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "产品id不可为空。");
            }


        } catch (Exception e) {
            log.error("保存型号出错！", e);
            ret = false;
            return new ApiResponse<>(RetCode.PARAM_ERROR, "保存型号出错");
        }
//        return new ApiResponse<>(deviceModelPo.getId());
//        Boolean formatRet = createOrUpdateModelFormat(d);
    }


    /**
     * 增加 型号的 deviceId 配额
     *
     * @param customerId
     * @param productId
     * @param addCount
     * @return
     */
    public ApiResponse<Boolean> createWxDeviceIdPools(Integer customerId, String productId, Integer addCount) {
        Boolean ret = true;
        try {

            if (null == addCount || addCount <= 0) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "配额数量必须大于0");
            }
            if (addCount > DeviceConstant.WXDEVICEID_MAX_COUNT) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "配额数量超出上限");
            }
            //校验客户是否存在
            CustomerPo queryCustomerPo = customerMapper.selectById(customerId);
            if (null == queryCustomerPo) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "客户不存在");
            } else if (null == queryCustomerPo.getAppid() || null == queryCustomerPo.getAppsecret() || null == queryCustomerPo.getPublicId()) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "客户公众号信息不存在");
            }

            ret = deviceOperateService.createWxDeviceIdPools(customerId, productId, addCount);

            return new ApiResponse<>(ret);
        } catch (Exception e) {
            ret = false;
            log.error("createWxDeviceIdPools.error = {}", e);
            throw new RuntimeException("增加 型号的 deviceId 配额");
        }
    }

    /**
     * 保存型号的自定义功能
     *
     * @param deviceModelAblityRequests
     * @param modelId
     * @return
     */
    public Boolean createOrUpdateModelAblitys(List<DeviceModelCreateOrUpdateRequest.DeviceModelAblityRequest> deviceModelAblityRequests, Integer modelId) {
        Boolean ret = true;
        try {
            //遍历当前 型号的能力
            deviceModelAblityRequests.stream().forEach(deviceModelAblityRequest -> {

                DeviceModelAblityPo deviceModelAblityPo = new DeviceModelAblityPo();
                deviceModelAblityPo.setModelId(modelId);
                deviceModelAblityPo.setDefinedName(deviceModelAblityRequest.getDefinedName());
                deviceModelAblityPo.setAblityId(deviceModelAblityRequest.getAblityId());
                deviceModelAblityPo.setMinVal(deviceModelAblityRequest.getMinVal());
                deviceModelAblityPo.setMaxVal(deviceModelAblityRequest.getMaxVal());

                //判断当前型号的功能 主键是否为空，不为空是更新，空是 保存
                //todo 选项未保存
                if (deviceModelAblityRequest.getId() != null && deviceModelAblityRequest.getId() > 0) {
                    deviceModelAblityPo.setId(deviceModelAblityRequest.getId());
                    deviceModelAblityPo.setLastUpdateTime(System.currentTimeMillis());
                    deviceModelAblityPo.setStatus(deviceModelAblityRequest.getStatus());
                    if (!CommonConstant.STATUS_DEL.equals(deviceModelAblityRequest.getStatus())) {
                        deviceModelAblityPo.setStatus(CommonConstant.STATUS_YES);
                    }
                    deviceModelAblityMapper.updateById(deviceModelAblityPo);
                } else {
                    deviceModelAblityPo.setStatus(CommonConstant.STATUS_YES);
                    deviceModelAblityPo.setCreateTime(System.currentTimeMillis());
                    deviceModelAblityMapper.insert(deviceModelAblityPo);
                }
                //遍历 当前 型号的功能项的自定义选项
                List<DeviceModelCreateOrUpdateRequest.DeviceModelAblityOptionRequest> deviceModelAblityOptionRequests = deviceModelAblityRequest.getDeviceModelAblityOptions();
                if (deviceModelAblityOptionRequests != null && deviceModelAblityOptionRequests.size() > 0) {
                    deviceModelAblityOptionRequests.stream().forEach(deviceModelAblityOptionRequest -> {

                        DeviceModelAblityOptionPo deviceModelAblityOptionPo = new DeviceModelAblityOptionPo();
                        deviceModelAblityOptionPo.setModelAblityId(deviceModelAblityPo.getId());
                        deviceModelAblityOptionPo.setAblityOptionId(deviceModelAblityOptionRequest.getAblityOptionId());
                        deviceModelAblityOptionPo.setDefinedName(deviceModelAblityOptionRequest.getDefinedName());
                        deviceModelAblityOptionPo.setMinVal(deviceModelAblityOptionRequest.getMinVal());
                        deviceModelAblityOptionPo.setMaxVal(deviceModelAblityOptionRequest.getMaxVal());

                        // 如果 有id 是 更新，否则是新增
                        if (deviceModelAblityOptionRequest.getId() != null && deviceModelAblityOptionRequest.getId() > 0) {
                            if (!CommonConstant.STATUS_DEL.equals(deviceModelAblityOptionRequest.getStatus())) {
                                deviceModelAblityOptionPo.setStatus(CommonConstant.STATUS_YES);
                            } else {
                                deviceModelAblityOptionPo.setStatus(CommonConstant.STATUS_DEL);
                            }
                            deviceModelAblityOptionPo.setId(deviceModelAblityOptionRequest.getId());
                            deviceModelAblityOptionPo.setLastUpdateTime(System.currentTimeMillis());
                            deviceModelAblityOptionMapper.updateById(deviceModelAblityOptionPo);
                        } else {
                            deviceModelAblityOptionPo.setStatus(CommonConstant.STATUS_YES);
                            deviceModelAblityOptionPo.setCreateTime(System.currentTimeMillis());
                            deviceModelAblityOptionMapper.insert(deviceModelAblityOptionPo);
                        }

                    });
                }
            });

        } catch (Exception e) {
            ret = false;
            log.error("保存型号-型号能力项-错误", e);
            throw new RuntimeException("保存型号-型号能力项-错误");

        }

        return ret;

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

        //查询 型号列表
        List<DeviceModelPo> deviceModelPos = deviceModelMapper.selectList(queryDeviceModelPo, limit, offset);
        return deviceModelPos.stream().map(deviceModelPo -> {
            DeviceModelVo deviceModelVo = new DeviceModelVo();
            deviceModelVo.setName(deviceModelPo.getName());
            deviceModelVo.setCustomerId(deviceModelPo.getCustomerId());
            deviceModelVo.setCustomerName(deviceModelPo.getCustomerName());
            deviceModelVo.setProductId(deviceModelPo.getProductId());
            deviceModelVo.setTypeId(deviceModelPo.getTypeId());
            deviceModelVo.setTypeName(deviceModelPo.getTypeName());
            deviceModelVo.setRemark(deviceModelPo.getRemark());
            deviceModelVo.setStatus(deviceModelPo.getStatus());
            deviceModelVo.setVersion(deviceModelPo.getVersion());
            deviceModelVo.setIcon(deviceModelPo.getIcon());
            deviceModelVo.setModelNo(deviceModelPo.getModelNo());
            deviceModelVo.setId(deviceModelPo.getId());

            List<DeviceModelAblityVo> deviceModelAblityVos = selectModelAblitysByModelId(deviceModelPo.getId());

            deviceModelVo.setDeviceModelAblitys(deviceModelAblityVos);

            //型号的版式
            ModelFormatVo modelFormatVo = selectModelFormatPages(deviceModelPo.getId(), deviceModelPo.getFormatId());
            deviceModelVo.setModelFormatVo(modelFormatVo);

            //首先查询device_pool表中 该型号可用的 wxDeviceId数量
            DeviceIdPoolPo deviceIdPoolPo = new DeviceIdPoolPo();
            deviceIdPoolPo.setCustomerId(deviceModelPo.getCustomerId());
            deviceIdPoolPo.setProductId(deviceModelPo.getProductId());
            deviceIdPoolPo.setStatus(DeviceConstant.WXDEVICEID_STATUS_NO);

            Integer devicePoolCount = deviceIdPoolMapper.selectCount(deviceIdPoolPo);
            deviceModelVo.setDevicePoolCount(devicePoolCount);
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

            List<DeviceModelAblityVo> deviceModelAblityVos = selectModelAblitysByModelId(deviceModelPo.getId());
            deviceModelVo.setDeviceModelAblitys(deviceModelAblityVos);
            return deviceModelVo;
        }).collect(Collectors.toList());

    }

    /**
     * 根据类型ID 查询型号列表
     *
     * @param typeIds
     * @return
     */
    public List<DeviceModelVo> selectModelsByTypeIds(String typeIds) {

        List<String> typeIdList = Arrays.asList(typeIds.split(","));

        List<DeviceModelPo> deviceModelPos = deviceModelMapper.selectModelsByTypeIds(typeIdList);
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

            List<DeviceModelAblityVo> deviceModelAblityVos = selectModelAblitysByModelId(deviceModelPo.getId());
            deviceModelVo.setDeviceModelAblitys(deviceModelAblityVos);

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
        if (null != deviceModelPo) {
            deviceModelVo.setName(deviceModelPo.getName());
            deviceModelVo.setCustomerId(deviceModelPo.getCustomerId());
            deviceModelVo.setCustomerName(deviceModelPo.getCustomerName());
            deviceModelVo.setProductId(deviceModelPo.getProductId());
            deviceModelVo.setFormatId(deviceModelPo.getFormatId());
            deviceModelVo.setTypeId(deviceModelPo.getTypeId());
            deviceModelVo.setTypeName(deviceModelPo.getTypeName());
            deviceModelVo.setRemark(deviceModelPo.getRemark());
            deviceModelVo.setStatus(deviceModelPo.getStatus());
            deviceModelVo.setVersion(deviceModelPo.getVersion());
            deviceModelVo.setIcon(deviceModelVo.getIcon());
            deviceModelVo.setId(deviceModelPo.getId());

            //型号的功能集
            List<DeviceModelAblityVo> deviceModelAblityVos = selectModelAblitysByModelId(deviceModelPo.getId());

            deviceModelVo.setDeviceModelAblitys(deviceModelAblityVos);

            //型号的版式
            ModelFormatVo modelFormatVo = selectModelFormatPages(deviceModelPo.getId(), deviceModelPo.getFormatId());
            deviceModelVo.setModelFormatVo(modelFormatVo);

            //首先查询device_pool表中 该型号可用的 wxDeviceId数量
            DeviceIdPoolPo deviceIdPoolPo = new DeviceIdPoolPo();
            deviceIdPoolPo.setCustomerId(deviceModelPo.getCustomerId());
            deviceIdPoolPo.setProductId(deviceModelPo.getProductId());
            deviceIdPoolPo.setStatus(DeviceConstant.WXDEVICEID_STATUS_NO);

            Integer devicePoolCount = deviceIdPoolMapper.selectCount(deviceIdPoolPo);
            deviceModelVo.setDevicePoolCount(devicePoolCount);

        }
        return deviceModelVo;
    }

    /**
     * 根据型号主键 查询 该型号的功能
     *
     * @param modelId
     * @return
     */
    public List<DeviceModelAblityVo> selectModelAblitysByModelId(Integer modelId) {

        //查询型号的自定义能力项
        List<DeviceModelAblityPo> deviceModelAblityPos = deviceModelAblityMapper.selectByModelId(modelId);

        List<DeviceModelAblityVo> deviceModelAblityVos = new ArrayList<DeviceModelAblityVo>();
        if (deviceModelAblityPos != null && deviceModelAblityPos.size() > 0) {
            deviceModelAblityVos = deviceModelAblityPos.stream().map(deviceModelAblityPo -> {
                DeviceModelAblityVo deviceModelAblityVo = new DeviceModelAblityVo();
                deviceModelAblityVo.setId(deviceModelAblityPo.getId());
                deviceModelAblityVo.setModelId(deviceModelAblityPo.getModelId());
                deviceModelAblityVo.setAblityId(deviceModelAblityPo.getAblityId());
                deviceModelAblityVo.setDefinedName(deviceModelAblityPo.getDefinedName());
                deviceModelAblityVo.setMinVal(deviceModelAblityPo.getMinVal());
                deviceModelAblityVo.setMaxVal(deviceModelAblityPo.getMaxVal());
                deviceModelAblityVo.setStatus(deviceModelAblityPo.getStatus());

                //查询 型号的能力项的 选项
                List<DeviceModelAblityVo.DeviceModelAblityOptionVo> deviceModelAblityOptionVos = selectModelAblityOptionsByModelAblityId(deviceModelAblityPo.getId());

                deviceModelAblityVo.setDeviceModelAblityOptions(deviceModelAblityOptionVos);


                return deviceModelAblityVo;
            }).collect(Collectors.toList());
        }
        return deviceModelAblityVos;
    }


    /**
     * 根据 型号的能力主键 获取 自定义的选项
     *
     * @param modelAblityId
     * @return
     */
    public List<DeviceModelAblityVo.DeviceModelAblityOptionVo> selectModelAblityOptionsByModelAblityId(Integer modelAblityId) {

        List<DeviceModelAblityOptionPo> deviceModelAblityOptionPos = deviceModelAblityOptionMapper.getOptionsByJoinId(modelAblityId);
        List<DeviceModelAblityVo.DeviceModelAblityOptionVo> deviceModelAblityOptionVos = new ArrayList<>();
        if (deviceModelAblityOptionPos != null && deviceModelAblityOptionPos.size() > 0) {
            deviceModelAblityOptionVos = deviceModelAblityOptionPos.stream().map(deviceModelAblityOptionPo -> {
                DeviceModelAblityVo.DeviceModelAblityOptionVo deviceModelAblityOptionVo = new DeviceModelAblityVo.DeviceModelAblityOptionVo();

                deviceModelAblityOptionVo.setAblityOptionId(deviceModelAblityOptionPo.getAblityOptionId());
                deviceModelAblityOptionVo.setDefinedName(deviceModelAblityOptionPo.getDefinedName());
                deviceModelAblityOptionVo.setMinVal(deviceModelAblityOptionPo.getMinVal());
                deviceModelAblityOptionVo.setMaxVal(deviceModelAblityOptionPo.getMaxVal());
                deviceModelAblityOptionVo.setStatus(deviceModelAblityOptionPo.getStatus());
                deviceModelAblityOptionVo.setId(deviceModelAblityOptionPo.getId());
                return deviceModelAblityOptionVo;
            }).collect(Collectors.toList());
        }
        return deviceModelAblityOptionVos;
    }


    /**
     * 根据主键 删除 型号
     *
     * @param modelId
     * @return
     */
    public Boolean deleteModelById(Integer modelId) {

        Boolean ret = true;
        ret = deviceModelMapper.deleteById(modelId) > 0;
        return ret;
    }


    /**
     * 创建 或者更新 型号的版式
     *
     * @param modelFormatRequest
     * @return
     */
    public Boolean createOrUpdateModelFormat(DeviceModelFormatCreateRequest modelFormatRequest, Integer modelId, Integer formatId) {

        Boolean ret = true;
        try {
            //遍历保存 型号的版式页面信息
            if (modelFormatRequest.getModelFormatPages() != null && modelFormatRequest.getModelFormatPages().size() > 0) {
                modelFormatRequest.getModelFormatPages().stream().forEach(modelFormatPageCreateRequest -> {

                    DeviceModelFormatPo deviceModelFormatPo = new DeviceModelFormatPo();

                    if (modelFormatPageCreateRequest != null) {
                        BeanUtils.copyProperties(modelFormatPageCreateRequest, deviceModelFormatPo);
                    }
                    deviceModelFormatPo.setFormatId(formatId);
                    deviceModelFormatPo.setModelId(modelId);

                    if (deviceModelFormatPo.getId() != null && deviceModelFormatPo.getId() > 0) {
                        //如果 状态不是删除，则默认是新增
                        if (!CommonConstant.STATUS_DEL.equals(modelFormatPageCreateRequest.getStatus())) {
                            deviceModelFormatPo.setStatus(CommonConstant.STATUS_YES);
                        }
                        deviceModelFormatPo.setLastUpdateTime(System.currentTimeMillis());
                        deviceModelFormatMapper.updateById(deviceModelFormatPo);
                    } else {
                        deviceModelFormatPo.setStatus(CommonConstant.STATUS_YES);
                        deviceModelFormatPo.setCreateTime(System.currentTimeMillis());
                        deviceModelFormatMapper.insert(deviceModelFormatPo);
                    }

                    //遍历保存 型号的 具体配置项
                    if (modelFormatPageCreateRequest.getModelFormatItems() != null && modelFormatPageCreateRequest.getModelFormatItems().size() > 0) {
                        modelFormatPageCreateRequest.getModelFormatItems().stream().forEach(modelFormatItemCreateRequest -> {

                            DeviceModelFormatItemPo deviceModelFormatItemPo = new DeviceModelFormatItemPo();

                            deviceModelFormatItemPo.setAblityId(modelFormatItemCreateRequest.getAblityId());
                            deviceModelFormatItemPo.setItemId(modelFormatItemCreateRequest.getItemId());
                            deviceModelFormatItemPo.setModelFormatId(deviceModelFormatPo.getId());
                            deviceModelFormatItemPo.setShowName(modelFormatItemCreateRequest.getShowName());
                            deviceModelFormatItemPo.setShowStatus(modelFormatItemCreateRequest.getShowStatus());

                            //更新操作
                            if (modelFormatItemCreateRequest.getId() != null && modelFormatItemCreateRequest.getId() > 0) {

                                if (!CommonConstant.STATUS_DEL.equals(modelFormatItemCreateRequest.getStatus())) {
                                    deviceModelFormatItemPo.setStatus(CommonConstant.STATUS_YES);
                                } else {
                                    deviceModelFormatItemPo.setStatus(CommonConstant.STATUS_DEL);
                                }
                                deviceModelFormatItemPo.setId(modelFormatItemCreateRequest.getId());
                                deviceModelFormatItemPo.setLastUpdateTime(System.currentTimeMillis());
                                deviceModelFormatItemMapper.updateById(deviceModelFormatItemPo);
                            } else {
                                //新增操作

                                deviceModelFormatItemPo.setStatus(CommonConstant.STATUS_YES);
                                deviceModelFormatItemPo.setCreateTime(System.currentTimeMillis());
                                deviceModelFormatItemMapper.insert(deviceModelFormatItemPo);
                            }

                        });
                    }

                });
            }
        } catch (Exception e) {
            log.error("保存型号版式报错", e);
            throw new RuntimeException("保存型号版式报错");
        }
        return ret;

    }

    /**
     * 根据型号主键 查询 该型号的功能
     *
     * @param modelId
     * @return //
     */
    public ModelFormatVo selectModelFormatPages(Integer modelId, Integer formatId) {

        ModelFormatVo modelFormatVo = new ModelFormatVo();
        //查询型号的自定义版式
        List<DeviceModelFormatPo> deviceModelFormatPages = deviceModelFormatMapper.obtainModelFormatPages(modelId, formatId);
        List<ModelFormatVo.DeviceModelFormatPageVo> deviceModelFormatPageVos = new ArrayList<ModelFormatVo.DeviceModelFormatPageVo>();
        if (deviceModelFormatPages != null && deviceModelFormatPages.size() > 0) {
            deviceModelFormatPageVos = deviceModelFormatPages.stream().map(deviceModelFormatPagePo -> {
                ModelFormatVo.DeviceModelFormatPageVo deviceModelFormatPageVo = new ModelFormatVo.DeviceModelFormatPageVo();
                deviceModelFormatPageVo.setId(deviceModelFormatPagePo.getId());
                deviceModelFormatPageVo.setPageId(deviceModelFormatPagePo.getPageId());
                deviceModelFormatPageVo.setShowName(deviceModelFormatPagePo.getShowName());
                deviceModelFormatPageVo.setShowStatus(deviceModelFormatPagePo.getShowStatus());
                deviceModelFormatPageVo.setStatus(deviceModelFormatPagePo.getStatus());
                deviceModelFormatPageVo.setFormatId(formatId);
                deviceModelFormatPageVo.setModelId(modelId);

                //查询 型号的自定义版式的配置项
                List<DeviceModelFormatItemPo> deviceModelFormatItempos = deviceModelFormatItemMapper.obtainModelFormatItems(deviceModelFormatPagePo.getId());
                List<ModelFormatVo.DeviceModelFormatItemVo> deviceModelFormatItemVos = new ArrayList<ModelFormatVo.DeviceModelFormatItemVo>();
                if (deviceModelFormatItempos != null && deviceModelFormatItempos.size() > 0) {
                    deviceModelFormatItemVos = deviceModelFormatItempos.stream().map(deviceModelFormatItemPo -> {
                        ModelFormatVo.DeviceModelFormatItemVo deviceModelFormatItemVo = new ModelFormatVo.DeviceModelFormatItemVo();
                        deviceModelFormatItemVo.setId(deviceModelFormatItemPo.getId());
                        deviceModelFormatItemVo.setModelFormatId(deviceModelFormatPagePo.getId());
                        deviceModelFormatItemVo.setAblityId(deviceModelFormatItemPo.getAblityId());
                        deviceModelFormatItemVo.setItemId(deviceModelFormatItemPo.getItemId());
                        deviceModelFormatItemVo.setShowName(deviceModelFormatItemPo.getShowName());
                        deviceModelFormatItemVo.setShowStatus(deviceModelFormatItemPo.getShowStatus());
                        deviceModelFormatItemVo.setStatus(deviceModelFormatItemPo.getStatus());
                        return deviceModelFormatItemVo;

                    }).collect(Collectors.toList());
                    deviceModelFormatPageVo.setModelFormatItems(deviceModelFormatItemVos);
                }

                return deviceModelFormatPageVo;

            }).collect(Collectors.toList());

            modelFormatVo.setModelFormatPages(deviceModelFormatPageVos);
        }
        return modelFormatVo;
    }
}
