package com.huanke.iot.manage.service.device.typeModel;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.device.DeviceIdPoolMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityOptionMapper;
import com.huanke.iot.base.dao.device.ability.DeviceTypeAbilitysMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.format.DeviceModelFormatItemMapper;
import com.huanke.iot.base.dao.format.DeviceModelFormatMapper;
import com.huanke.iot.base.dao.format.WxFormatItemMapper;
import com.huanke.iot.base.dao.user.UserManagerMapper;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.device.DeviceIdPoolPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityOptionPo;
import com.huanke.iot.base.po.device.ability.DeviceTypeAbilitysPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityOptionPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.base.po.format.DeviceModelFormatItemPo;
import com.huanke.iot.base.po.format.DeviceModelFormatPo;
import com.huanke.iot.base.po.format.WxFormatItemPo;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.util.UniNoCreateUtils;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.device.operate.DeviceOperateService;
import com.huanke.iot.manage.service.user.UserService;
import com.huanke.iot.manage.vo.request.device.operate.DevicePoolRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelFormatCreateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelQueryRequest;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceModelAbilityVo;
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
    private DeviceModelAbilityMapper deviceModelAbilityMapper;

    @Autowired
    private DeviceModelAbilityOptionMapper deviceModelAbilityOptionMapper;

    @Autowired
    private DeviceTypeAbilitysMapper deviceTypeAbilitysMapper;

    @Autowired
    private DeviceAbilityOptionMapper deviceAbilityOptionMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private DeviceModelFormatMapper deviceModelFormatMapper;

    @Autowired
    private DeviceModelFormatItemMapper deviceModelFormatItemMapper;

    @Autowired
    private DeviceIdPoolMapper deviceIdPoolMapper;

    @Autowired
    private WxFormatItemMapper wxFormatItemMapper;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private UserManagerMapper userManagerMapper;

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
        User user = userService.getCurrentUser();
        try {
            if (modelRequest != null) {
                BeanUtils.copyProperties(modelRequest, deviceModelPo);
            }
            //校验 productId 不可为空
            if (StringUtils.isNotBlank(modelRequest.getProductId())) {
//                DeviceModelPo queryDeviceModelPo = new DeviceModelPo();
//                queryDeviceModelPo = deviceModelMapper.selectByProductId(modelRequest.getProductId());

                if (modelRequest.getId() != null && modelRequest.getId() > 0) {
                    //如果 存在此productid 的型号 且 主键 和 保存的型号 不一致时不允许
//                    if (null != queryDeviceModelPo && !modelRequest.getId().equals(queryDeviceModelPo.getId())) {
//                        return new ApiResponse<>(RetCode.PARAM_ERROR, "已存在此产品id。");
//                    }
                    //当 修改 设备型号的类型的时候，同步修改 该型号下的设备的类型
                    if(deviceModelPo.getTypeId()!=null&&deviceModelPo.getTypeId()>0){
                        deviceMapper.updateDeviceTypeByModelId(modelRequest.getId());
                    }else{
                        return new ApiResponse<>(RetCode.PARAM_ERROR, "设备类型不可为空。");
                    }
                    deviceModelPo.setLastUpdateUser(user.getId());
                    deviceModelPo.setLastUpdateTime(System.currentTimeMillis());
                    //如果不是删除，则设置成 正常状态
                    deviceModelPo.setStatus(modelRequest.getStatus() == null ? CommonConstant.STATUS_YES : modelRequest.getStatus());
                    deviceModelMapper.updateById(deviceModelPo);
                } else {
                    //新增的时候 如果存在此productid 则不可保存
//                    if (null != queryDeviceModelPo) {
//                        return new ApiResponse<>(RetCode.PARAM_ERROR, "已存在此产品id。");
//                    }

                    if(deviceModelPo.getTypeId()==null){
                        return new ApiResponse<>(RetCode.PARAM_ERROR, "设备类型不可为空。");
                    }
                    deviceModelPo.setModelNo(UniNoCreateUtils.createNo(DeviceConstant.DEVICE_UNI_NO_MODEl));
                    deviceModelPo.setCreateTime(System.currentTimeMillis());
                    deviceModelPo.setCreateUser(user.getId());
                    deviceModelPo.setStatus(modelRequest.getStatus() == null ? CommonConstant.STATUS_YES : modelRequest.getStatus());
                    deviceModelMapper.insert(deviceModelPo);

                    //增加wxdeviceid配额 //todo 默认增加 100
                    ApiResponse<Integer> addPoolResult = deviceOperateService.createWxDeviceIdPools(deviceModelPo.getCustomerId(), deviceModelPo.getProductId(), DeviceConstant.WXDEVICEID_DEF_COUNT);
                    if (RetCode.ERROR == addPoolResult.getCode()) {
                        log.error("增加配额失败={}", "错误提示：" + addPoolResult.getMsg(), "错误代码：" + addPoolResult.getCode(), " 成功增加：" + addPoolResult.getData());
                    }
                }


                //随后保存型号的自定义功能
                List<DeviceModelCreateOrUpdateRequest.DeviceModelAbilityRequest> deviceModelAbilityRequests = modelRequest.getDeviceModelAbilitys();
                if (deviceModelAbilityRequests != null && deviceModelAbilityRequests.size() > 0) {

                    this.createOrUpdateModelAbilitys(deviceModelAbilityRequests, deviceModelPo.getId());
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
//            return new ApiResponse<>(RetCode.PARAM_ERROR, "保存型号出错");
            throw new RuntimeException("保存型号出错");
        }
    }


    /**
     * 增加 型号的 deviceId 配额
     *
     * @param devicePoolRequest
     * @param devicePoolRequest
     * @param devicePoolRequest
     * @return
     */
    public ApiResponse<Integer> createWxDeviceIdPools(DevicePoolRequest devicePoolRequest) {
        Boolean ret = true;
        try {
            Integer addCount = devicePoolRequest.getAddCount();
            Integer customerId = devicePoolRequest.getCustomerId();
            String productId = devicePoolRequest.getProductId();

            if (null == devicePoolRequest) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "参数不可为空");
            }
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
            } else if (null == queryCustomerPo.getAppid() || null == queryCustomerPo.getAppsecret()) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "客户公众号信息不存在");
            }

            DeviceModelPo queryDeviceModel = deviceModelMapper.selectByProductId(productId);
            if (null == queryDeviceModel) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "产品主键不存在");
            }
            return deviceOperateService.createWxDeviceIdPools(customerId, productId, addCount);

        } catch (Exception e) {
            ret = false;
            log.error("createWxDeviceIdPools.error = {}", e);
            throw new RuntimeException("增加 型号的 deviceId 配额");
        }
    }

    /**
     * 保存型号的自定义功能
     *
     * @param deviceModelAbilityRequests
     * @param modelId
     * @return
     */
    public Boolean createOrUpdateModelAbilitys(List<DeviceModelCreateOrUpdateRequest.DeviceModelAbilityRequest> deviceModelAbilityRequests, Integer modelId) {
        Boolean ret = true;
        try {
            //遍历当前 型号的能力
            deviceModelAbilityRequests.stream().forEach(deviceModelAbilityRequest -> {

                DeviceModelAbilityPo deviceModelAbilityPo = new DeviceModelAbilityPo();
                deviceModelAbilityPo.setModelId(modelId);
                deviceModelAbilityPo.setDefinedName(deviceModelAbilityRequest.getDefinedName());
                deviceModelAbilityPo.setAbilityId(deviceModelAbilityRequest.getAbilityId());
                deviceModelAbilityPo.setMinVal(deviceModelAbilityRequest.getMinVal());
                deviceModelAbilityPo.setMaxVal(deviceModelAbilityRequest.getMaxVal());

                //判断当前型号的功能 主键是否为空，不为空是更新，空是 保存
                //todo 选项未保存
                if (deviceModelAbilityRequest.getId() != null && deviceModelAbilityRequest.getId() > 0) {
                    deviceModelAbilityPo.setId(deviceModelAbilityRequest.getId());
                    deviceModelAbilityPo.setLastUpdateTime(System.currentTimeMillis());
                    deviceModelAbilityPo.setStatus(deviceModelAbilityRequest.getStatus()==null?CommonConstant.STATUS_YES:deviceModelAbilityRequest.getStatus());
                    deviceModelAbilityMapper.updateById(deviceModelAbilityPo);
                } else {
                    deviceModelAbilityPo.setStatus(deviceModelAbilityRequest.getStatus()==null?CommonConstant.STATUS_YES:deviceModelAbilityRequest.getStatus());
                    deviceModelAbilityPo.setCreateTime(System.currentTimeMillis());
                    deviceModelAbilityMapper.insert(deviceModelAbilityPo);
                }
                //遍历 当前 型号的功能项的自定义选项
                List<DeviceModelCreateOrUpdateRequest.DeviceModelAbilityOptionRequest> deviceModelAbilityOptionRequests = deviceModelAbilityRequest.getDeviceModelAbilityOptions();
                if (deviceModelAbilityOptionRequests != null && deviceModelAbilityOptionRequests.size() > 0) {
                    deviceModelAbilityOptionRequests.stream().forEach(deviceModelAbilityOptionRequest -> {

                        DeviceModelAbilityOptionPo deviceModelAbilityOptionPo = new DeviceModelAbilityOptionPo();
                        deviceModelAbilityOptionPo.setModelAbilityId(deviceModelAbilityPo.getId());
                        deviceModelAbilityOptionPo.setAbilityOptionId(deviceModelAbilityOptionRequest.getAbilityOptionId());
                        deviceModelAbilityOptionPo.setDefinedName(deviceModelAbilityOptionRequest.getDefinedName());
                        deviceModelAbilityOptionPo.setDefaultValue(deviceModelAbilityOptionRequest.getDefaultVal());
                        deviceModelAbilityOptionPo.setMinVal(deviceModelAbilityOptionRequest.getMinVal());
                        deviceModelAbilityOptionPo.setMaxVal(deviceModelAbilityOptionRequest.getMaxVal());

                        // 如果 有id 是 更新，否则是新增
                        if (deviceModelAbilityOptionRequest.getId() != null && deviceModelAbilityOptionRequest.getId() > 0) {
                            deviceModelAbilityOptionPo.setStatus(deviceModelAbilityOptionRequest.getStatus()==null?CommonConstant.STATUS_YES:deviceModelAbilityOptionRequest.getStatus());
                            deviceModelAbilityOptionPo.setId(deviceModelAbilityOptionRequest.getId());
                            deviceModelAbilityOptionPo.setLastUpdateTime(System.currentTimeMillis());
                            deviceModelAbilityOptionMapper.updateById(deviceModelAbilityOptionPo);
                        } else {
                            deviceModelAbilityOptionPo.setStatus(deviceModelAbilityOptionRequest.getStatus()==null?CommonConstant.STATUS_YES:deviceModelAbilityOptionRequest.getStatus());
                            deviceModelAbilityOptionPo.setCreateTime(System.currentTimeMillis());
                            deviceModelAbilityOptionMapper.insert(deviceModelAbilityOptionPo);
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

        //获取该二级域名的客户主键
        Integer customerId = customerService.obtainCustomerId(false);
        DeviceModelPo queryDeviceModelPo = new DeviceModelPo();
        queryDeviceModelPo.setName(request.getName());
        //如果查询条件未带入 客户主键，则默认查询当前客户的型号
        queryDeviceModelPo.setCustomerId(request.getCustomerId() == null ? customerId : request.getCustomerId());
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
            deviceModelVo.setProductQrCode(deviceModelPo.getProductQrCode());
            deviceModelVo.setFormatId(deviceModelPo.getFormatId());
            deviceModelVo.setAndroidFormatId(deviceModelPo.getAndroidFormatId());
            deviceModelVo.setTypeId(deviceModelPo.getTypeId());
            deviceModelVo.setTypeName(deviceModelPo.getTypeName());
            deviceModelVo.setRemark(deviceModelPo.getRemark());
            deviceModelVo.setStatus(deviceModelPo.getStatus());
            deviceModelVo.setVersion(deviceModelPo.getVersion());
            deviceModelVo.setChildModelIds(deviceModelPo.getChildModelIds());
            deviceModelVo.setIcon(deviceModelPo.getIcon());
            deviceModelVo.setModelNo(deviceModelPo.getModelNo());
            deviceModelVo.setId(deviceModelPo.getId());

//            List<DeviceModelAbilityVo> deviceModelAbilityVos = selectModelAbilitysByModelId(deviceModelPo.getId(),deviceModelPo.getTypeId());
//
//            deviceModelVo.setDeviceModelAbilitys(deviceModelAbilityVos);

//            //型号的版式
//            ModelFormatVo modelFormatVo = selectModelFormatPages(deviceModelPo.getId(), deviceModelPo.getFormatId());
//            deviceModelVo.setModelFormatVo(modelFormatVo);

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

    public ApiResponse<Integer> selectCount(Integer status) throws Exception {
        DeviceModelPo deviceModelPo = new DeviceModelPo();
        deviceModelPo.setStatus(status);
        return new ApiResponse<>(RetCode.OK, "查询总数成功", this.deviceModelMapper.selectCount(deviceModelPo));
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
            deviceModelVo.setCustomerName(deviceModelPo.getCustomerName());
            deviceModelVo.setProductId(deviceModelPo.getProductId());
            deviceModelVo.setProductQrCode(deviceModelPo.getProductQrCode());
            deviceModelVo.setFormatId(deviceModelPo.getFormatId());
            deviceModelVo.setAndroidFormatId(deviceModelPo.getAndroidFormatId());
            deviceModelVo.setTypeId(deviceModelPo.getTypeId());
            deviceModelVo.setTypeName(deviceModelPo.getTypeName());
            deviceModelVo.setRemark(deviceModelPo.getRemark());
            deviceModelVo.setStatus(deviceModelPo.getStatus());
            deviceModelVo.setVersion(deviceModelPo.getVersion());
            deviceModelVo.setChildModelIds(deviceModelPo.getChildModelIds());
            deviceModelVo.setIcon(deviceModelPo.getIcon());
            deviceModelVo.setModelNo(deviceModelPo.getModelNo());
            deviceModelVo.setId(deviceModelPo.getId());

            List<DeviceModelAbilityVo> deviceModelAbilityVos = selectModelAbilitysByModelId(deviceModelPo.getId(), deviceModelPo.getTypeId());
            deviceModelVo.setDeviceModelAbilitys(deviceModelAbilityVos);
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
            deviceModelVo.setCustomerName(deviceModelPo.getCustomerName());
            deviceModelVo.setProductId(deviceModelPo.getProductId());
            deviceModelVo.setProductQrCode(deviceModelPo.getProductQrCode());
            deviceModelVo.setFormatId(deviceModelPo.getFormatId());
            deviceModelVo.setAndroidFormatId(deviceModelPo.getAndroidFormatId());
            deviceModelVo.setTypeId(deviceModelPo.getTypeId());
            deviceModelVo.setTypeName(deviceModelPo.getTypeName());
            deviceModelVo.setRemark(deviceModelPo.getRemark());
            deviceModelVo.setStatus(deviceModelPo.getStatus());
            deviceModelVo.setVersion(deviceModelPo.getVersion());
            deviceModelVo.setChildModelIds(deviceModelPo.getChildModelIds());
            deviceModelVo.setIcon(deviceModelPo.getIcon());
            deviceModelVo.setModelNo(deviceModelPo.getModelNo());
            deviceModelVo.setId(deviceModelPo.getId());

            List<DeviceModelAbilityVo> deviceModelAbilityVos = selectModelAbilitysByModelId(deviceModelPo.getId(), deviceModelPo.getTypeId());
            deviceModelVo.setDeviceModelAbilitys(deviceModelAbilityVos);

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
            deviceModelVo.setProductQrCode(deviceModelPo.getProductQrCode());
            deviceModelVo.setFormatId(deviceModelPo.getFormatId());
            deviceModelVo.setAndroidFormatId(deviceModelPo.getAndroidFormatId());
            deviceModelVo.setTypeId(deviceModelPo.getTypeId());
            deviceModelVo.setTypeName(deviceModelPo.getTypeName());
            deviceModelVo.setRemark(deviceModelPo.getRemark());
            deviceModelVo.setStatus(deviceModelPo.getStatus());
            deviceModelVo.setVersion(deviceModelPo.getVersion());
            deviceModelVo.setChildModelIds(deviceModelPo.getChildModelIds());
            deviceModelVo.setIcon(deviceModelPo.getIcon());
            deviceModelVo.setModelNo(deviceModelPo.getModelNo());
            deviceModelVo.setId(deviceModelPo.getId());

            deviceModelVo.setCreateUserName(userService.getUserName(deviceModelPo.getCreateUser()));
            deviceModelVo.setLastUpdateUserName(userService.getUserName(deviceModelPo.getLastUpdateUser()));

            //型号的功能集
            List<DeviceModelAbilityVo> deviceModelAbilityVos = selectModelAbilitysByModelId(deviceModelPo.getId(), deviceModelPo.getTypeId());

            deviceModelVo.setDeviceModelAbilitys(deviceModelAbilityVos);

            //型号的版式
            ModelFormatVo modelFormatVo = selectModelFormatPages(deviceModelPo.getId(), deviceModelPo.getFormatId());
            deviceModelVo.setDeviceModelFormat(modelFormatVo);

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
    public List<DeviceModelAbilityVo> selectModelAbilitysByModelId(Integer modelId, Integer typeId) {

        /*查询型号的自定义能力项*/

        //查询型号的所有状态的 功能项
        List<DeviceModelAbilityPo> deviceModelAbilityPos = deviceModelAbilityMapper.selectByModelId(modelId);
        //查询 类型所有状态的功能项
        List<DeviceTypeAbilitysPo> deviceTypeAbilitysPos = deviceTypeAbilitysMapper.selectByTypeId(typeId);

        /* 以 型号能力项为原数据 对比 类型 的功能项，找出 类型删除的功能项*/
        List<DeviceModelAbilityVo> deviceModelAbilityVos = new ArrayList<DeviceModelAbilityVo>();
        if (deviceModelAbilityPos != null && deviceModelAbilityPos.size() > 0) {
            deviceModelAbilityVos = deviceModelAbilityPos.stream().map(deviceModelAbilityPo -> {

                DeviceModelAbilityVo deviceModelAbilityVo = new DeviceModelAbilityVo();
                deviceModelAbilityVo.setId(deviceModelAbilityPo.getId());
                deviceModelAbilityVo.setModelId(deviceModelAbilityPo.getModelId());
                deviceModelAbilityVo.setAbilityId(deviceModelAbilityPo.getAbilityId());
                deviceModelAbilityVo.setAbilityType(deviceModelAbilityPo.getAbilityType());
                deviceModelAbilityVo.setDefinedName(deviceModelAbilityPo.getDefinedName());
                deviceModelAbilityVo.setMinVal(deviceModelAbilityPo.getMinVal());
                deviceModelAbilityVo.setMaxVal(deviceModelAbilityPo.getMaxVal());
                deviceModelAbilityVo.setStatus(deviceModelAbilityPo.getStatus());

                /*对比型号的功能项与 类型的功能项*/
                if (deviceTypeAbilitysPos != null && deviceTypeAbilitysPos.size() > 0) {
                    for (int m = 0; m < deviceTypeAbilitysPos.size(); m++) {
                        DeviceTypeAbilitysPo deviceTypeAbilitysPo = deviceTypeAbilitysPos.get(m);
                        Integer typeAbilityId = deviceTypeAbilitysPo.getAbilityId();
                        //当 型号的功能项和 类型的功能项相等，则代表 正常
                        if (typeAbilityId.equals(deviceModelAbilityPo.getAbilityId())) {
                            //如果 此功能项状态正常，则更新状态位正常
                            if (deviceTypeAbilitysPo.getAbilityStatus().equals(CommonConstant.STATUS_YES)) {
                                deviceModelAbilityVo.setUpdateStatus(DeviceConstant.DEVICE_MODEL_ABILITY_UPDATE_NORMAL);
                            } else {
                                //不是正常，则证明已被禁用（列表已过滤了删除状态），则此更新状态位禁用。
                                deviceModelAbilityVo.setUpdateStatus(DeviceConstant.DEVICE_MODEL_ABILITY_UPDATE_DISABIE);
                            }

                            break;
                        }
                        //当 类型的功能项中 不存在 此功能项，则代表 ，类型的功能项被删除。
                        if (!typeAbilityId.equals(deviceModelAbilityPo.getAbilityId()) && m == deviceTypeAbilitysPos.size() - 1) {
                            deviceModelAbilityVo.setUpdateStatus(DeviceConstant.DEVICE_MODEL_ABILITY_UPDATE_MINUS);
                            break;
                        }
                    }
                }
                //查询 型号的能力项的 选项
                List<DeviceModelAbilityVo.DeviceModelAbilityOptionVo> deviceModelAbilityOptionVos = selectModelAbilityOptionsByModelAbilityId(deviceModelAbilityPo.getId(), deviceModelAbilityPo.getAbilityId());

                deviceModelAbilityVo.setDeviceModelAbilityOptions(deviceModelAbilityOptionVos);
                return deviceModelAbilityVo;
            }).collect(Collectors.toList());
        }

        /* 以 类型 能力项为原数据 对比 型号 的功能项，找出 类型新添加的功能项*/
        if (deviceTypeAbilitysPos != null && deviceTypeAbilitysPos.size() > 0) {
            for (int m = 0; m < deviceTypeAbilitysPos.size(); m++) {
                DeviceTypeAbilitysPo deviceTypeAbilitysPo = deviceTypeAbilitysPos.get(m);
                Integer typeAbilityId = deviceTypeAbilitysPo.getAbilityId();
                if (deviceModelAbilityPos != null && deviceModelAbilityPos.size() > 0) {
                    for (int a = 0; a < deviceModelAbilityPos.size(); a++) {
                        DeviceModelAbilityPo deviceModelAbilitypo = deviceModelAbilityPos.get(a);
                        if (typeAbilityId.equals(deviceModelAbilitypo.getAbilityId())) {
                            break;
                        }
                        if (!typeAbilityId.equals(deviceModelAbilitypo.getAbilityId()) && a == deviceModelAbilityPos.size() - 1) {
                            /*添加新增加的功能项及选项*/
                            DeviceModelAbilityVo deviceModelAbilityVo = operateNewModelAbility(modelId, typeAbilityId, deviceTypeAbilitysPo);
                            deviceModelAbilityVos.add(deviceModelAbilityVo);
                        }
                    }

                } else {
                    /*添加新增加的功能项及选项*/
                    DeviceModelAbilityVo deviceModelAbilityVo = operateNewModelAbility(modelId, typeAbilityId, deviceTypeAbilitysPo);
                    deviceModelAbilityVos.add(deviceModelAbilityVo);
                }
            }
        }


        return deviceModelAbilityVos;
    }

    /**
     * 查询型号时，将类型新添加的功能项及选项转换成 型号功能项及选项
     *
     * @param modelId
     * @return
     */
    private DeviceModelAbilityVo operateNewModelAbility(Integer modelId, Integer typeAbilityId, DeviceTypeAbilitysPo deviceTypeAbilitysPo) {

        DeviceModelAbilityVo deviceModelAbilityVo = new DeviceModelAbilityVo();
        deviceModelAbilityVo.setId(null);
        deviceModelAbilityVo.setModelId(modelId);
        deviceModelAbilityVo.setAbilityId(typeAbilityId);
        deviceModelAbilityVo.setAbilityType(deviceTypeAbilitysPo.getAbilityType());
        deviceModelAbilityVo.setDefinedName(deviceTypeAbilitysPo.getAbilityName());
        deviceModelAbilityVo.setMinVal(deviceTypeAbilitysPo.getMinVal());
        deviceModelAbilityVo.setMaxVal(deviceTypeAbilitysPo.getMaxVal());
        deviceModelAbilityVo.setStatus(null);
        deviceModelAbilityVo.setUpdateStatus(DeviceConstant.DEVICE_MODEL_ABILITY_UPDATE_ADD);


        List<DeviceAbilityOptionPo> deviceAbilityOptionPos = deviceAbilityOptionMapper.selectOptionsByAbilityId(typeAbilityId);
        if (deviceAbilityOptionPos != null && deviceAbilityOptionPos.size() > 0) {
            List<DeviceModelAbilityVo.DeviceModelAbilityOptionVo> deviceAbilityOptionVos = deviceAbilityOptionPos.stream().map(deviceAbilityOptionPo -> {
                DeviceModelAbilityVo.DeviceModelAbilityOptionVo deviceModelAbilityOptionVo = new DeviceModelAbilityVo.DeviceModelAbilityOptionVo();

                deviceModelAbilityOptionVo.setAbilityOptionId(deviceAbilityOptionPo.getId());
                deviceModelAbilityOptionVo.setDefinedName(deviceAbilityOptionPo.getOptionName());
                deviceModelAbilityOptionVo.setMaxVal(deviceAbilityOptionPo.getMaxVal());
                deviceModelAbilityOptionVo.setMinVal(deviceAbilityOptionPo.getMinVal());
                deviceModelAbilityOptionVo.setStatus(CommonConstant.STATUS_YES);

                return deviceModelAbilityOptionVo;
            }).collect(Collectors.toList());

            deviceModelAbilityVo.setDeviceModelAbilityOptions(deviceAbilityOptionVos);
        } else {
            deviceModelAbilityVo.setDeviceModelAbilityOptions(null);
        }

        return deviceModelAbilityVo;
    }

    /**
     * 根据 型号的能力主键 获取 自定义的选项
     *
     * @param modelAbilityId
     * @return
     */
    public List<DeviceModelAbilityVo.DeviceModelAbilityOptionVo> selectModelAbilityOptionsByModelAbilityId(Integer modelAbilityId, Integer ability) {

        List<DeviceModelAbilityOptionPo> deviceModelAbilityOptionPos = deviceModelAbilityOptionMapper.getOptionsByModelAbilityId(modelAbilityId);

        List<DeviceAbilityOptionPo> deviceAbilityOptionPos = deviceAbilityOptionMapper.selectOptionsByAbilityId(ability);

        List<DeviceModelAbilityVo.DeviceModelAbilityOptionVo> deviceModelAbilityOptionVos = new ArrayList<>();

        //遍历对比 功能项的选项
        if (deviceAbilityOptionPos != null && deviceAbilityOptionPos.size() > 0) {
            for (int i = 0; i < deviceAbilityOptionPos.size(); i++) {
                DeviceAbilityOptionPo deviceAbilityOptionPo = deviceAbilityOptionPos.get(i);

                DeviceModelAbilityVo.DeviceModelAbilityOptionVo deviceModelAbilityOptionVo = new DeviceModelAbilityVo.DeviceModelAbilityOptionVo();

                deviceModelAbilityOptionVo.setAbilityOptionId(deviceAbilityOptionPo.getId());
                deviceModelAbilityOptionVo.setDefinedName(deviceAbilityOptionPo.getOptionName());
                deviceModelAbilityOptionVo.setDefaultVal(deviceAbilityOptionPo.getDefaultValue());
                deviceModelAbilityOptionVo.setMinVal(deviceAbilityOptionPo.getMinVal());
                deviceModelAbilityOptionVo.setMaxVal(deviceAbilityOptionPo.getMaxVal());
                deviceModelAbilityOptionVo.setStatus(deviceAbilityOptionPo.getStatus());
                deviceModelAbilityOptionVo.setId(null);
                deviceModelAbilityOptionVo.setUpdateStatus(DeviceConstant.DEVICE_MODEL_ABILITY_UPDATE_ADD);

                if (deviceModelAbilityOptionPos != null && deviceModelAbilityOptionPos.size() > 0) {
                    for (int m = 0; m < deviceModelAbilityOptionPos.size(); m++) {
                        DeviceModelAbilityOptionPo deviceModelAbilityOptionPo = deviceModelAbilityOptionPos.get(m);
                        if (deviceModelAbilityOptionPo.getAbilityOptionId().equals(deviceAbilityOptionPo.getId())) {

                            deviceModelAbilityOptionVo.setDefinedName(deviceModelAbilityOptionPo.getDefinedName());
                            deviceModelAbilityOptionVo.setDefaultVal(deviceModelAbilityOptionPo.getDefaultValue());
                            deviceModelAbilityOptionVo.setMinVal(deviceModelAbilityOptionPo.getMinVal());
                            deviceModelAbilityOptionVo.setMaxVal(deviceModelAbilityOptionPo.getMaxVal());
                            deviceModelAbilityOptionVo.setStatus(deviceModelAbilityOptionPo.getStatus());
                            deviceModelAbilityOptionVo.setUpdateStatus(DeviceConstant.DEVICE_MODEL_ABILITY_UPDATE_NORMAL);
                            deviceModelAbilityOptionVo.setId(deviceModelAbilityOptionPo.getId());
                            break;
                        }
                    }

                }
                deviceModelAbilityOptionVos.add(deviceModelAbilityOptionVo);
            }

        } else {
            return null;
        }

        return deviceModelAbilityOptionVos;
    }


    /**
     * 根据主键 删除 型号
     *
     * @param modelId
     * @return
     */
    public ApiResponse<Boolean> deleteModelById(Integer modelId) {

        List<DevicePo> devicePos = deviceMapper.selectByModelId(modelId);
        if (devicePos != null && devicePos.size() > 0) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "当前型号已被分配设备，请召回后再进行删除！");
        }

        /*逻辑删除型号*/

        DeviceModelPo delModel = new DeviceModelPo();
        delModel.setId(modelId);
        delModel.setStatus(CommonConstant.STATUS_DEL);
        delModel.setLastUpdateTime(System.currentTimeMillis());
        deviceModelMapper.updateStatusById(delModel);

        return new ApiResponse<>(RetCode.OK, "删除成功");
    }

    /**
     * 根据主键 删除 型号的业务操作
     *
     * @param modelId
     * @return
     */
    public ApiResponse<Boolean> deleteModelOption(Integer modelId) throws Exception {

        List<DevicePo> devicePos = deviceMapper.selectByModelId(modelId);
        if (devicePos != null && devicePos.size() > 0) {
            ApiResponse<Boolean> ret = deviceOperateService.callBackDeviceList(devicePos);

            if (ret.getCode() != RetCode.OK) {
                throw new BusinessException(ret.getMsg());
            }
        }
        /*逻辑删除型号*/
        DeviceModelPo delModel = new DeviceModelPo();
        delModel.setId(modelId);
        delModel.setStatus(CommonConstant.STATUS_DEL);
        delModel.setLastUpdateTime(System.currentTimeMillis());
        deviceModelMapper.updateStatusById(delModel);

        return new ApiResponse<>(RetCode.OK, "删除成功");
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

                            deviceModelFormatItemPo.setAbilityId(modelFormatItemCreateRequest.getAbilityId());
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

                /*
                 *  查询版式的配置，和型号的配置项进行做对比，是否版式项有增删。*/
                //查询 版式配置表
                List<WxFormatItemPo> wxFormatItemPos = wxFormatItemMapper.selectByPageId(formatId, deviceModelFormatPagePo.getPageId());
                //查询 型号的自定义版式的配置项
                List<DeviceModelFormatItemPo> deviceModelFormatItempos = deviceModelFormatItemMapper.obtainModelFormatItems(deviceModelFormatPagePo.getId());
                List<ModelFormatVo.DeviceModelFormatItemVo> deviceModelFormatItemVos = new ArrayList<ModelFormatVo.DeviceModelFormatItemVo>();
                if (wxFormatItemPos != null && wxFormatItemPos.size() > 0) {
                    deviceModelFormatItemVos = wxFormatItemPos.stream().map(wxFormatItemPo -> {
                        ModelFormatVo.DeviceModelFormatItemVo deviceModelFormatItemVo = new ModelFormatVo.DeviceModelFormatItemVo();

                        deviceModelFormatItemVo.setModelFormatId(deviceModelFormatPagePo.getId());
                        deviceModelFormatItemVo.setItemId(wxFormatItemPo.getId());
                        deviceModelFormatItemVo.setShowName(wxFormatItemPo.getName());
                        deviceModelFormatItemVo.setShowStatus(DeviceConstant.DEVICE_MODEL_FORMAT_ITEM_SHOW_NO);
                        deviceModelFormatItemVo.setStatus(CommonConstant.STATUS_YES);
                        deviceModelFormatItemVo.setAbilityType(wxFormatItemPo.getAbilityType());

                        //遍历 对比 型号的版式的配置项
                        if (deviceModelFormatItempos != null && deviceModelFormatItempos.size() > 0) {
                            for (int i = 0; i < deviceModelFormatItempos.size(); i++) {
                                DeviceModelFormatItemPo deviceModelFormatItemPo = deviceModelFormatItempos.get(i);
                                //如果 配置项id相同
                                if (deviceModelFormatItemPo.getItemId().equals(wxFormatItemPo.getId())) {

                                    deviceModelFormatItemVo.setId(deviceModelFormatItemPo.getId());
                                    deviceModelFormatItemVo.setAbilityId(deviceModelFormatItemPo.getAbilityId());
                                    deviceModelFormatItemVo.setShowName(deviceModelFormatItemPo.getShowName());
                                    deviceModelFormatItemVo.setShowStatus(deviceModelFormatItemPo.getShowStatus());
                                    deviceModelFormatItemVo.setStatus(deviceModelFormatItemPo.getStatus());
                                    break;
                                }

                            }

                        }

                        return deviceModelFormatItemVo;
                    }).collect(Collectors.toList());

                    deviceModelFormatPageVo.setModelFormatItems(deviceModelFormatItemVos);
                }

                return deviceModelFormatPageVo;

            }).collect(Collectors.toList());

            modelFormatVo.setModelFormatPages(deviceModelFormatPageVos);
        }else{
            modelFormatVo = null;
        }
        return modelFormatVo;
    }
}
