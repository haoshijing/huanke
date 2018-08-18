package com.huanke.iot.manage.service.customer;

import com.huanke.iot.base.dao.customer.*;
import com.huanke.iot.base.po.customer.*;
import com.huanke.iot.manage.vo.request.customer.SaveCutomerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 保存详情
     * @param saveCutomerRequest
     */
    public void saveDetail(SaveCutomerRequest saveCutomerRequest) {

        //客户信息
        CustomerPo customerPo = new CustomerPo();
        customerPo.setName(saveCutomerRequest.getName());
        customerPo.setLoginName(saveCutomerRequest.getLoginName());
        customerPo.setUserType(saveCutomerRequest.getUserType());
        customerPo.setRemark(saveCutomerRequest.getRemark());
        customerPo.setPublicName(saveCutomerRequest.getPublicName());
        customerPo.setAppid(saveCutomerRequest.getAppid());
        customerPo.setAppsecret(saveCutomerRequest.getAppsecret());
        customerPo.setSLD(saveCutomerRequest.getSLD());
        customerPo.setCreateTime(System.currentTimeMillis());
        customerPo.setLastUpdateTime(System.currentTimeMillis());
        this.customerMapper.insert(customerPo);

        //TODO 设备型号信息



        //H5配置信息
        SaveCutomerRequest.H5Config h5Config = saveCutomerRequest.getH5Config();
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
        SaveCutomerRequest.AndroidConfig androidConfig = saveCutomerRequest.getAndroidConfig();
        List<SaveCutomerRequest.AndroidScene> androidSceneList = androidConfig.getAndroidSceneList();

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
        for (SaveCutomerRequest.AndroidScene androidScene : androidSceneList) {
            AndroidScenePo androidScenePo = new AndroidScenePo();
            androidScenePo.setConfigId(androidConfigPo.getId());
            androidScenePo.setCustomerId(customerPo.getId());
            androidScenePo.setName(androidScene.getName());
            androidScenePo.setImgsCover("https://"+bucketUrl+"/" + androidScene.getImgsCoverKey());
            androidScenePo.setDescribe(androidScene.getDescribe());
            androidScenePo.setCreateTime(System.currentTimeMillis());
            androidScenePo.setLastUpdateTime(System.currentTimeMillis());
            this.androidSceneMapper.insert(androidScenePo);

            List<SaveCutomerRequest.AndroidSceneImg> androidSceneImgList = androidScene.getAndroidSceneImgList();
            for (SaveCutomerRequest.AndroidSceneImg androidSceneImg : androidSceneImgList) {
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
        SaveCutomerRequest.BackendConfig backendConfig = saveCutomerRequest.getBackendConfig();
        BackendConfigPo backendConfigPo = new BackendConfigPo();
        backendConfigPo.setName(backendConfig.getName());
        backendConfigPo.setLogo("https://"+bucketUrl+"/" + backendConfig.getLogoKey());
        backendConfigPo.setType(backendConfig.getType());
        backendConfigPo.setEnableStatus(backendConfig.getEnableStatus());
        backendConfigPo.setCustomerId(customerPo.getId());
        backendConfigPo.setCreateTime(System.currentTimeMillis());
        backendConfigPo.setLastUpdateTime(System.currentTimeMillis());
        this.backendConfigMapper.insert(backendConfigPo);
    }
}
