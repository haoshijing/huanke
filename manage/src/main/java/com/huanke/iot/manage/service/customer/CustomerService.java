package com.huanke.iot.manage.service.customer;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.dao.customer.*;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAblityMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.po.customer.*;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAblityPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.manage.vo.request.customer.CustomerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 客户
 */
@Repository
public class CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Value("${bucketUrl}")
    private String bucketUrl;

    @Autowired
    private WxConfigMapper wxConfigMapper;

    @Autowired
    private BackendConfigMapper backendConfigMapper;

    @Autowired
    private AndroidConfigMapper androidConfigMapper;

    @Autowired
    private AndroidSceneImgMapper androidSceneImgMapper;

    @Autowired
    private AndroidSceneMapper androidSceneMapper;

    @Autowired
    private DeviceModelMapper deviceModelMapper;

    @Autowired
    private DeviceModelAblityMapper deviceModelAblityMapper;

    /**
     * 保存详情
     * @param customerVo
     */
    public ApiResponse<Integer> saveDetail(CustomerVo customerVo) {

        //客户信息
        CustomerPo customerPo = new CustomerPo();
        customerPo.setName(customerVo.getName());
        customerPo.setLoginName(customerVo.getLoginName());
        customerPo.setUserType(customerVo.getUserType());
        customerPo.setRemark(customerVo.getRemark());
        customerPo.setPublicName(customerVo.getPublicName());
        customerPo.setAppid(customerVo.getAppid());
        customerPo.setAppsecret(customerVo.getAppsecret());
        customerPo.setSLD(customerVo.getSLD());
        //如果有客户主键信息 为更新，否则为新增
        if(customerPo.getId()!=null&&customerPo.getId()>0){
            customerPo.setLastUpdateTime(System.currentTimeMillis());
            this.customerMapper.updateById(customerPo);


        }else{
            customerPo.setCreateTime(System.currentTimeMillis());
            this.customerMapper.insert(customerPo);

        }

        List<CustomerVo.DeviceModel> deviceModelList = customerVo.getDeviceModelList();
        List<DeviceModelAblityPo> toAddAbilityPoList = new ArrayList<>();
        for (CustomerVo.DeviceModel deviceModel : deviceModelList) {

            DeviceModelPo deviceModelPo = new DeviceModelPo();
            deviceModelPo.setName(deviceModel.getName());
            deviceModelPo.setTypeId(deviceModel.getTypeId());
            deviceModelPo.setCustomerId(customerPo.getId());
            deviceModelPo.setProductId(deviceModel.getProductId());
            deviceModelPo.setIcon("https://"+bucketUrl+"/" + deviceModel.getIconKey());
            deviceModelPo.setCreateTime(System.currentTimeMillis());
            deviceModelPo.setRemark(deviceModel.getRemark());
            deviceModelPo.setLastUpdateTime(System.currentTimeMillis());
            this.deviceModelMapper.insert(deviceModelPo);

            List<CustomerVo.DeviceModelAbility> deviceModelAbilityList = deviceModel.getDeviceModelAbilityList();
            for (CustomerVo.DeviceModelAbility deviceModelAbility : deviceModelAbilityList) {
                DeviceModelAblityPo deviceModelAblityPo = new DeviceModelAblityPo();
                deviceModelAblityPo.setModelId(deviceModelPo.getId());
                deviceModelAblityPo.setAblityId(deviceModelAbility.getAblityId());
                deviceModelAblityPo.setDefinedName(deviceModelAbility.getDefinedName());
                deviceModelAblityPo.setCreateTime(System.currentTimeMillis());
                deviceModelAblityPo.setLastUpdateTime(System.currentTimeMillis());
                toAddAbilityPoList.add(deviceModelAblityPo);
            }
        }
        this.deviceModelAblityMapper.insertBatch(toAddAbilityPoList);


        //H5配置信息
        CustomerVo.H5Config h5Config = customerVo.getH5Config();
        WxConfigPo wxConfigPo = new WxConfigPo();
        wxConfigPo.setCustomerId(customerPo.getId());
        wxConfigPo.setPassword(h5Config.getPassword());
        wxConfigPo.setDefaultTeamName(h5Config.getDefaultTeamName());
        wxConfigPo.setHtmlTypeId(h5Config.getHtmlTypeId());
        wxConfigPo.setBackgroundImg("https://"+bucketUrl+"/" + h5Config.getBackgroundImgKey());
        wxConfigPo.setThemeName(h5Config.getThemeName());
        wxConfigPo.setLogo("https://"+bucketUrl+"/" + h5Config.getLogoKey());
        wxConfigPo.setVersion(h5Config.getVersion());
        wxConfigPo.setCreateTime(System.currentTimeMillis());
        wxConfigPo.setLastUpdateTime(System.currentTimeMillis());
        this.wxConfigMapper.insert(wxConfigPo);


        //安卓配置信息
        CustomerVo.AndroidConfig androidConfig = customerVo.getAndroidConfig();
        List<CustomerVo.AndroidScene> androidSceneList = androidConfig.getAndroidSceneList();

        AndroidConfigPo androidConfigPo = new AndroidConfigPo();
        androidConfigPo.setCustomerId(customerPo.getId());
        androidConfigPo.setName(androidConfig.getName());
        androidConfigPo.setLogo("https://" + bucketUrl + "/" + androidConfig.getLogoKey());
        androidConfigPo.setQrcode("https://" + bucketUrl + "/" + androidConfig.getQrcodeKey());
        androidConfigPo.setVersion(androidConfig.getVersion());
        androidConfigPo.setCreateTime(System.currentTimeMillis());
        androidConfigPo.setLastUpdateTime(System.currentTimeMillis());
        androidConfigPo.setDeviceChangePassword(androidConfig.getDeviceChangePassword());
        this.androidConfigMapper.insert(androidConfigPo);

        List<AndroidSceneImgPo> toAddAndroidSceneImgPoList = new ArrayList<>();
        for (CustomerVo.AndroidScene androidScene : androidSceneList) {
            AndroidScenePo androidScenePo = new AndroidScenePo();
            androidScenePo.setConfigId(androidConfigPo.getId());
            androidScenePo.setCustomerId(customerPo.getId());
            androidScenePo.setName(androidScene.getName());
            androidScenePo.setImgsCover("https://"+bucketUrl+"/" + androidScene.getImgsCoverKey());
            androidScenePo.setDescribe(androidScene.getDescribe());
            androidScenePo.setCreateTime(System.currentTimeMillis());
            androidScenePo.setLastUpdateTime(System.currentTimeMillis());
            this.androidSceneMapper.insert(androidScenePo);

            List<CustomerVo.AndroidSceneImg> androidSceneImgList = androidScene.getAndroidSceneImgList();
            for (CustomerVo.AndroidSceneImg androidSceneImg : androidSceneImgList) {
                AndroidSceneImgPo androidSceneImgPo = new AndroidSceneImgPo();
                androidSceneImgPo.setConfigId(androidConfigPo.getId());
                androidSceneImgPo.setAndroidSceneId(androidScenePo.getId());
                androidSceneImgPo.setCustomerId(customerPo.getId());
                androidSceneImgPo.setName(androidSceneImg.getName());
                androidSceneImgPo.setImgVideo("https://"+bucketUrl+"/" + androidSceneImg.getImgVideoKey());
                androidSceneImgPo.setDescribe(androidSceneImg.getDescribe());
                androidSceneImgPo.setCreateTime(System.currentTimeMillis());
                androidSceneImgPo.setLastUpdateTime(System.currentTimeMillis());
                toAddAndroidSceneImgPoList.add(androidSceneImgPo);
            }
        }
        this.androidSceneImgMapper.insertBatch(toAddAndroidSceneImgPoList);

        //管理后台配置信息
        CustomerVo.BackendConfig backendConfig = customerVo.getBackendConfig();
        BackendConfigPo backendConfigPo = new BackendConfigPo();
        backendConfigPo.setName(backendConfig.getName());
        backendConfigPo.setLogo("https://"+bucketUrl+"/" + backendConfig.getLogoKey());
        backendConfigPo.setType(backendConfig.getType());
        backendConfigPo.setEnableStatus(backendConfig.getEnableStatus());
        backendConfigPo.setCustomerId(customerPo.getId());
        backendConfigPo.setCreateTime(System.currentTimeMillis());
        backendConfigPo.setLastUpdateTime(System.currentTimeMillis());
        this.backendConfigMapper.insert(backendConfigPo);

        return new ApiResponse<>(customerPo.getId());
    }


    /**
     * 查询客户列表
     * @param currentPage
     * @param limit
     * @return
     */
    public List<CustomerPo> queryList(Integer currentPage, Integer limit) {
        Integer offset = (currentPage - 1) * limit;
        return this.customerMapper.selectList(null, currentPage, offset);
    }
}
