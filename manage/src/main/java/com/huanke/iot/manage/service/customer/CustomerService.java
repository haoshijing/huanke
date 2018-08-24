package com.huanke.iot.manage.service.customer;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.dao.customer.*;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAblityMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.po.customer.*;
import com.huanke.iot.manage.vo.request.customer.CustomerQueryRequest;
import com.huanke.iot.manage.vo.request.customer.CustomerVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

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
     *
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
        customerPo.setPublicId(customerVo.getPublicId());  //todo

        customerPo.setAppid(customerVo.getAppid());
        customerPo.setAppsecret(customerVo.getAppsecret());
        customerPo.setSLD(customerVo.getSLD());
        customerPo.setTypeIds(customerVo.getTypeIds());
        customerPo.setModelIds(customerVo.getModelIds());

        customerPo.setStatus(CommonConstant.STATUS_YES);
        // 先保存 客户信息
        //如果有客户主键信息 为更新，否则为新增
        if (customerVo.getId() != null && customerVo.getId() > 0) {
            customerPo.setId(customerVo.getId());
            customerPo.setLastUpdateTime(System.currentTimeMillis());
            this.customerMapper.updateById(customerPo);

        } else {
            customerPo.setCreateTime(System.currentTimeMillis());
            this.customerMapper.insert(customerPo);
        }

        /*H5配置信息*/
        CustomerVo.H5Config h5Config = customerVo.getH5Config();
        // 先查询 该客户 有没有相关的配置
        WxConfigPo wxConfigPo = wxConfigMapper.selectConfigByCustomerId(customerVo.getId());


        //若 存在 该h5配置 则进行更新，否则为新增。
        if(wxConfigPo!=null){
            BeanUtils.copyProperties(h5Config,wxConfigPo);

            wxConfigPo.setLastUpdateTime(System.currentTimeMillis());
            this.wxConfigMapper.updateById(wxConfigPo);
        }else{
            wxConfigPo = new WxConfigPo();
            BeanUtils.copyProperties(h5Config,wxConfigPo);
            wxConfigPo.setCustomerId(customerPo.getId());
            wxConfigPo.setCreateTime(System.currentTimeMillis());
            this.wxConfigMapper.insert(wxConfigPo);
        }

        /*安卓配置信息*/
        CustomerVo.AndroidConfig androidConfig = customerVo.getAndroidConfig();
        //先查询 是否存在 安卓配置
        AndroidConfigPo androidConfigPo = androidConfigMapper.selectConfigByCustomerId(customerPo.getId());
        //如果存在该安卓配置，则进行更新 否则进行新增
        if(androidConfigPo!=null){
            BeanUtils.copyProperties(androidConfig,androidConfigPo);
            androidConfigPo.setLastUpdateTime(System.currentTimeMillis());
            androidConfigMapper.updateById(androidConfigPo);
        }else{
            androidConfigPo = new AndroidConfigPo();
            BeanUtils.copyProperties(androidConfig,androidConfigPo);
            androidConfigPo.setCreateTime(System.currentTimeMillis());
            androidConfigPo.setCustomerId(customerPo.getId());
            androidConfigMapper.insert(androidConfigPo);
        }
        /*安卓场景列表*/
        //遍历保存安卓场景列表
        List<CustomerVo.AndroidScene> androidSceneList = androidConfig.getAndroidSceneList();
        if(androidSceneList!=null&&androidSceneList.size()>0){
            for (CustomerVo.AndroidScene androidScene : androidSceneList) {

                AndroidScenePo androidScenePo = new AndroidScenePo();
                //如果场景不为空,且主键不为空 则是更新，否则新增
                if(androidScene!=null&&androidScene.getId()!=null&&androidScene.getId()>0){
                    BeanUtils.copyProperties(androidScene,androidScenePo);
                    androidScenePo.setLastUpdateTime(System.currentTimeMillis());
                    androidSceneMapper.updateById(androidScenePo);
                }else{
                    androidScenePo = new AndroidScenePo();
                    BeanUtils.copyProperties(androidScene,androidScenePo);
                    androidScenePo.setCustomerId(customerPo.getId());
                    androidScenePo.setConfigId(androidConfigPo.getId());
                    androidConfigPo.setCreateTime(System.currentTimeMillis());
                    androidSceneMapper.insert(androidScenePo);
                }

                /*安卓场景-图册*/
//                List<AndroidSceneImgPo> toAddAndroidSceneImgPoList = new ArrayList<>();
                //遍历保存场景图册
                List<CustomerVo.AndroidSceneImg> androidSceneImgList = androidScene.getAndroidSceneImgList();
                if(androidSceneImgList!=null&&androidSceneImgList.size()>0){
                    for (CustomerVo.AndroidSceneImg androidSceneImg : androidSceneImgList) {
                        AndroidSceneImgPo androidSceneImgPo = new AndroidSceneImgPo();
                        if(androidSceneImg!=null&&androidSceneImg.getId()!=null&&androidSceneImg.getId()>0){
                            BeanUtils.copyProperties(androidSceneImg,androidSceneImgPo);
                            //客户id
                            androidSceneImgPo.setCustomerId(customerPo.getId());
                            //安卓配置id
                            androidSceneImgPo.setConfigId(androidConfigPo.getId());
                            //场景id
                            androidSceneImgPo.setAndroidSceneId(androidScenePo.getId());
                            androidSceneImgPo.setLastUpdateTime(System.currentTimeMillis());
                            androidSceneImgMapper.updateById(androidSceneImgPo);
                        }else{
                            BeanUtils.copyProperties(androidSceneImg,androidSceneImgPo);
                            //客户id
                            androidSceneImgPo.setCustomerId(customerPo.getId());
                            //安卓配置id
                            androidSceneImgPo.setConfigId(androidConfigPo.getId());
                            //场景id
                            androidSceneImgPo.setAndroidSceneId(androidScenePo.getId());
                            androidSceneImgPo.setCreateTime(System.currentTimeMillis());
                            androidSceneImgMapper.insert(androidSceneImgPo);
                        }

//                        toAddAndroidSceneImgPoList.add(androidSceneImgPo);
                    }
                }

            }
        }

//        this.androidSceneImgMapper.insertBatch(toAddAndroidSceneImgPoList);

        //管理后台配置信息
        CustomerVo.BackendConfig backendConfig = customerVo.getBackendConfig();
        //查询是否存在该 管理后台配置
        BackendConfigPo backendConfigPo = backendConfigMapper.selectConfigByCustomerId(customerPo.getId());
        //若不为空 说明存在 管理后台配置
        if(backendConfigPo!=null){
            BeanUtils.copyProperties(backendConfig,backendConfigPo);
            backendConfigPo.setLastUpdateTime(System.currentTimeMillis());
            backendConfigMapper.updateById(backendConfigPo);
        }else{
            backendConfigPo = new BackendConfigPo();
            BeanUtils.copyProperties(backendConfig,backendConfigPo);
            backendConfigPo.setCustomerId(customerPo.getId());
            backendConfigPo.setCreateTime(System.currentTimeMillis());
            backendConfigMapper.insert(backendConfigPo);
        }

        return new ApiResponse<>(customerPo.getId());
    }


    /**
     * 查询客户列表
     *
     * @param request
     * @return
     */
    public List<CustomerVo> selectList(CustomerQueryRequest request) {

        CustomerPo queryCustomerPo = new CustomerPo();
        BeanUtils.copyProperties(request,queryCustomerPo);
        Integer offset = (request.getPage() - 1) * request.getLimit();
        Integer limit = request.getLimit();
        List<CustomerPo> customerPos = customerMapper.selectList(queryCustomerPo,limit,offset);
        List<CustomerVo> customerVos = customerPos.stream().map(customerPo -> {
            CustomerVo customerVo = new CustomerVo();
            BeanUtils.copyProperties(customerPo,customerVo);
            return customerVo;
        }).collect(Collectors.toList());

        return  customerVos;
    }

    /**
     * 根据主键查询 客户
     *
     * @param customerId
     * @return
     */
    public CustomerVo selectById(Integer customerId) {
        CustomerVo customerVo = new CustomerVo();
        CustomerPo customerPo = customerMapper.selectById(customerId);
        BeanUtils.copyProperties(customerPo,customerVo);
        return customerVo;
    }

    /**
     * 根据二级域名查询 客户
     *
     * @param sld
     * @return
     */
    public CustomerVo selectById(String sld) {
        CustomerVo customerVo = new CustomerVo();
        CustomerPo customerPo = customerMapper.selectBySLD(sld);
        BeanUtils.copyProperties(customerPo,customerVo);
        return customerVo;
    }
}
