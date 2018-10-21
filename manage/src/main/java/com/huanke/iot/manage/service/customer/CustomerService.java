package com.huanke.iot.manage.service.customer;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.*;
import com.huanke.iot.base.po.customer.*;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.util.CommonUtil;
import com.huanke.iot.manage.service.user.UserService;
import com.huanke.iot.manage.vo.request.customer.CustomerQueryRequest;
import com.huanke.iot.manage.vo.request.customer.CustomerVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 客户
 */
@Slf4j
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
    private WxBgImgMapper wxBgImgMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private UserService userService;

@Autowired
    private CommonUtil commonUtil;

    private static final String CUSTOMERID_PREIX = "customerId.";

    @Value("${skipRemoteHost}")
    private String skipRemoteHost;

    @Value("${defaultPassWord}")
    private String defaultPassWord;

    /**
     * 保存详情
     *
     * @param customerVo
     */
    public ApiResponse<Integer> saveDetail(CustomerVo customerVo) {

        //获取当前二级域名的客户主键
        Integer curCustomerId = obtainCustomerId(false);
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

        //先验证二级域名是否重复 如果重复 不允许添加
        if (StringUtils.isNotBlank(customerVo.getSLD())) {
            CustomerPo queryCustomer = customerMapper.selectBySLD(customerVo.getSLD());
            //当库中存在 该二级域名的客户，且id 和 保存的不一致时。不允许添加
            if (queryCustomer != null&&!queryCustomer.getId().equals(customerVo.getId())) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "已存在该二级域名");
            }
        }

        // 先保存 客户信息
        //如果有客户主键信息 为更新，否则为新增
        if (customerVo.getId() != null && customerVo.getId() > 0) {
            customerPo.setId(customerVo.getId());
            customerPo.setLastUpdateTime(System.currentTimeMillis());
            this.customerMapper.updateById(customerPo);

        } else {
            customerPo.setStatus(CommonConstant.STATUS_YES);
            customerPo.setCreateTime(System.currentTimeMillis());
            customerPo.setParentCustomerId(curCustomerId); //如果是通过二级域名访问 并新增客户，则需要保存其父级 客户的主键

            String loginName = customerPo.getLoginName();
            if(StringUtils.isNotEmpty(loginName)){
                boolean hasSameUser = userService.hasSameUser(loginName);
                if(!hasSameUser){
                    User user = new User();
                    user.setNickName(customerPo.getName());
                    user.setSecondDomain(customerPo.getSLD());
                    user.setUserName(loginName);
                    user.setPassword(defaultPassWord);
                    userService.createUser(user);
                }else {
                    return new ApiResponse<>(RetCode.PARAM_ERROR, "用户名已存在！");
                }

            }else{
                return new ApiResponse<>(RetCode.PARAM_ERROR, "登录名称不可为空！");
            }

            this.customerMapper.insert(customerPo);
        }

        /*H5配置信息*/
        CustomerVo.H5Config h5Config = customerVo.getH5Config();
        // 先查询 该客户 有没有相关的配置
        WxConfigPo wxConfigPo = wxConfigMapper.selectConfigByCustomerId(customerVo.getId());


        //若 存在 该h5配置 则进行更新，否则为新增。
        if (wxConfigPo != null) {
            if(null!=h5Config){
                BeanUtils.copyProperties(h5Config, wxConfigPo);
            }

            wxConfigPo.setLastUpdateTime(System.currentTimeMillis());
            this.wxConfigMapper.updateById(wxConfigPo);
        } else {
            wxConfigPo = new WxConfigPo();
            BeanUtils.copyProperties(h5Config, wxConfigPo);
            wxConfigPo.setCustomerId(customerPo.getId());
            wxConfigPo.setStatus(CommonConstant.STATUS_YES);
            wxConfigPo.setCreateTime(System.currentTimeMillis());
            this.wxConfigMapper.insert(wxConfigPo);
        }

        /*H5背景图片*/
        List<CustomerVo.H5BgImg> h5BgImgList = h5Config.getH5BgImgList();
        if(h5BgImgList!=null&&h5BgImgList.size()>0){
            for (CustomerVo.H5BgImg h5BgImg : h5BgImgList) {

                WxBgImgPo h5BgImgPo = new WxBgImgPo();
                //如果场景不为空,且主键不为空 则是更新，否则新增
                if(h5BgImg!=null&&h5BgImg.getId()!=null&&h5BgImg.getId()>0){
                    BeanUtils.copyProperties(h5BgImg,h5BgImgPo);
                    h5BgImgPo.setLastUpdateTime(System.currentTimeMillis());
                    wxBgImgMapper.updateById(h5BgImgPo);
                }else{
                    h5BgImgPo = new WxBgImgPo();
                    BeanUtils.copyProperties(h5BgImg,h5BgImgPo);
                    h5BgImgPo.setCustomerId(customerPo.getId());
                    h5BgImgPo.setConfigId(wxConfigPo.getId());
                    h5BgImgPo.setCreateTime(System.currentTimeMillis());
                    h5BgImgPo.setStatus(CommonConstant.STATUS_YES);
                    wxBgImgMapper.insert(h5BgImgPo);
                }

            }
        }

        /*安卓配置信息*/
        CustomerVo.AndroidConfig androidConfig = customerVo.getAndroidConfig();
        //先查询 是否存在 安卓配置
        AndroidConfigPo androidConfigPo = androidConfigMapper.selectConfigByCustomerId(customerPo.getId());
        //如果存在该安卓配置，则进行更新 否则进行新增
        if (androidConfigPo != null) {
            BeanUtils.copyProperties(androidConfig, androidConfigPo);
            androidConfigPo.setLastUpdateTime(System.currentTimeMillis());
            androidConfigMapper.updateById(androidConfigPo);
        } else {
            androidConfigPo = new AndroidConfigPo();
            BeanUtils.copyProperties(androidConfig, androidConfigPo);
            androidConfigPo.setCreateTime(System.currentTimeMillis());
            androidConfigPo.setCustomerId(customerPo.getId());
            androidConfigPo.setStatus(CommonConstant.STATUS_YES);
            androidConfigMapper.insert(androidConfigPo);
        }
        /*安卓场景列表*/
        //遍历保存安卓场景列表
        List<CustomerVo.AndroidScene> androidSceneList = androidConfig.getAndroidSceneList();
        if (androidSceneList != null && androidSceneList.size() > 0) {
            for (CustomerVo.AndroidScene androidScene : androidSceneList) {

                AndroidScenePo androidScenePo = new AndroidScenePo();
                //如果场景不为空,且主键不为空 则是更新，否则新增
                if (androidScene != null && androidScene.getId() != null && androidScene.getId() > 0) {
                    BeanUtils.copyProperties(androidScene, androidScenePo);
                    androidScenePo.setLastUpdateTime(System.currentTimeMillis());
                    androidSceneMapper.updateById(androidScenePo);
                } else {
                    androidScenePo = new AndroidScenePo();
                    BeanUtils.copyProperties(androidScene, androidScenePo);
                    androidScenePo.setCustomerId(customerPo.getId());
                    androidScenePo.setConfigId(androidConfigPo.getId());
                    androidScenePo.setCreateTime(System.currentTimeMillis());
                    androidScenePo.setStatus(CommonConstant.STATUS_YES);
                    androidSceneMapper.insert(androidScenePo);
                }

                /*安卓场景-图册*/
//                List<AndroidSceneImgPo> toAddAndroidSceneImgPoList = new ArrayList<>();
                //遍历保存场景图册
                List<CustomerVo.AndroidSceneImg> androidSceneImgList = androidScene.getAndroidSceneImgList();
                if (androidSceneImgList != null && androidSceneImgList.size() > 0) {
                    for (CustomerVo.AndroidSceneImg androidSceneImg : androidSceneImgList) {
                        AndroidSceneImgPo androidSceneImgPo = new AndroidSceneImgPo();
                        if (androidSceneImg != null && androidSceneImg.getId() != null && androidSceneImg.getId() > 0) {
                            BeanUtils.copyProperties(androidSceneImg, androidSceneImgPo);
                            //客户id
                            androidSceneImgPo.setCustomerId(customerPo.getId());
                            //安卓配置id
                            androidSceneImgPo.setConfigId(androidConfigPo.getId());
                            //场景id
                            androidSceneImgPo.setAndroidSceneId(androidScenePo.getId());
                            androidSceneImgPo.setLastUpdateTime(System.currentTimeMillis());
                            androidSceneImgMapper.updateById(androidSceneImgPo);
                        } else {
                            BeanUtils.copyProperties(androidSceneImg, androidSceneImgPo);
                            //客户id
                            androidSceneImgPo.setCustomerId(customerPo.getId());
                            //安卓配置id
                            androidSceneImgPo.setConfigId(androidConfigPo.getId());
                            //场景id
                            androidSceneImgPo.setAndroidSceneId(androidScenePo.getId());
                            androidSceneImgPo.setCreateTime(System.currentTimeMillis());
                            androidSceneImgPo.setStatus(CommonConstant.STATUS_YES);
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
        if (backendConfigPo != null) {
            BeanUtils.copyProperties(backendConfig, backendConfigPo);
            backendConfigPo.setLastUpdateTime(System.currentTimeMillis());
            backendConfigMapper.updateById(backendConfigPo);
        } else {
            backendConfigPo = new BackendConfigPo();
            BeanUtils.copyProperties(backendConfig, backendConfigPo);
            backendConfigPo.setCustomerId(customerPo.getId());
            backendConfigPo.setCreateTime(System.currentTimeMillis());
            backendConfigPo.setStatus(CommonConstant.STATUS_YES);
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

        //获取当前二级域名的客户主键
        Integer curCustomerId = obtainCustomerId(false);

        CustomerPo queryCustomerPo = new CustomerPo();
        BeanUtils.copyProperties(request, queryCustomerPo);
        Integer offset = (request.getPage() - 1) * request.getLimit();
        Integer limit = request.getLimit();

        //如果 当前二级域名客户存在，则 只查询 该客户的客户。
        if(curCustomerId!=null){
            queryCustomerPo.setParentCustomerId(curCustomerId);
        }
        //如果 没有指定状态，则默认查出 非删除的数据
        if (queryCustomerPo.getStatus() == null) {
            queryCustomerPo.setStatus(CommonConstant.STATUS_YES);
        }
        List<CustomerPo> customerPos = customerMapper.selectList(queryCustomerPo, limit, offset);
        List<CustomerVo> customerVos = customerPos.stream().map(customerPo -> {
            CustomerVo customerVo = new CustomerVo();
            BeanUtils.copyProperties(customerPo, customerVo);
            return customerVo;
        }).collect(Collectors.toList());

        return customerVos;
    }

    /**
     * 查询客户列表
     *
     * @param
     * @return
     */
    public List<CustomerVo> selectAllCustomers() {
        //获取当前二级域名的客户主键
        Integer curCustomerId = obtainCustomerId(false);
        log.info("selectAllCustomers--> curCustomerId = "+curCustomerId);
        List<CustomerPo> customerPos = customerMapper.selectAllCustomers(curCustomerId);
        if(customerPos!=null&&customerPos.size()>0){
            List<CustomerVo> customerVos = customerPos.stream().map(customerPo -> {
                CustomerVo customerVo = new CustomerVo();
                BeanUtils.copyProperties(customerPo, customerVo);
                return customerVo;
            }).collect(Collectors.toList());
            return customerVos;
        }else {
            return null;
        }


    }
    /**
     * 根据主键查询 客户
     *
     * @param customerId
     * @return
     */
    public CustomerVo selectById(Integer customerId) {
        CustomerVo customerVo = new CustomerVo();
        //先查询客户
        CustomerPo customerPo = customerMapper.selectById(customerId);

        //如果存在 此客户 则进行 查询相关配置项
        if (customerPo != null && customerPo.getId() > 0) {
            BeanUtils.copyProperties(customerPo, customerVo);
            /*H5配置信息*/
            CustomerVo.H5Config h5ConfigVo = new CustomerVo.H5Config();
            WxConfigPo resultWxConfigPo = wxConfigMapper.selectConfigByCustomerId(customerVo.getId());
            if (resultWxConfigPo != null) {
                BeanUtils.copyProperties(resultWxConfigPo, h5ConfigVo);

                WxBgImgPo queryWxBgImgPo = new WxBgImgPo();
                queryWxBgImgPo.setConfigId(resultWxConfigPo.getId());
                //默认查询有效
                queryWxBgImgPo.setStatus(CommonConstant.STATUS_YES);
                List<WxBgImgPo> resultWxBgImgPoList = wxBgImgMapper.selectListByConfigId(queryWxBgImgPo);

                //当图片列表不为空 遍历 h5端图片列表
                if(resultWxBgImgPoList!=null&&resultWxBgImgPoList.size()>0){
                    List<CustomerVo.H5BgImg> h5BgImgList = resultWxBgImgPoList.stream().map(resultWxBgImgPo -> {
                        CustomerVo.H5BgImg h5BgImgVo = new CustomerVo.H5BgImg();

                        BeanUtils.copyProperties(resultWxBgImgPo,h5BgImgVo);
                        return h5BgImgVo;

                    }).collect(Collectors.toList());

                    h5ConfigVo.setH5BgImgList(h5BgImgList);
                }else{
                    h5ConfigVo.setH5BgImgList(null);
                }
            }
            customerVo.setH5Config(h5ConfigVo);

            /*安卓配置信息*/
            CustomerVo.AndroidConfig androidConfigVo = new CustomerVo.AndroidConfig();
            AndroidConfigPo resultAndroidConfigPo = androidConfigMapper.selectConfigByCustomerId(customerPo.getId());
            if (resultAndroidConfigPo != null) {
                BeanUtils.copyProperties(resultAndroidConfigPo, androidConfigVo);
                //查询 安卓场景
                AndroidScenePo queryAndroidScenePo = new AndroidScenePo();
                queryAndroidScenePo.setConfigId(resultAndroidConfigPo.getId());
                //默认查询有效
                queryAndroidScenePo.setStatus(CommonConstant.STATUS_YES);
                List<AndroidScenePo> resultAndroidScenePos = androidSceneMapper.selectListByConfigId(queryAndroidScenePo);

                //当场景列表 不为空的时候 遍历改场景列表，并去查询场景的图片等。
                if (resultAndroidScenePos != null && resultAndroidScenePos.size() > 0) {
                    List<CustomerVo.AndroidScene> androidSceneVoList = resultAndroidScenePos.stream().map(resultAndroidScenePo -> {
                        CustomerVo.AndroidScene androidSceneVo = new CustomerVo.AndroidScene();

                        BeanUtils.copyProperties(resultAndroidScenePo, androidSceneVo);

                        AndroidSceneImgPo queryAndroidSceneImgPo = new AndroidSceneImgPo();
                        //场景id
                        queryAndroidSceneImgPo.setAndroidSceneId(resultAndroidScenePo.getId());
                        //默认查询 有效
                        queryAndroidSceneImgPo.setStatus(CommonConstant.STATUS_YES);
                        List<AndroidSceneImgPo> androidSceneImgPoList = androidSceneImgMapper.selectListBySceneId(queryAndroidSceneImgPo);
                        //如果长场景图片列表不为空，则遍历。
                        if (androidSceneImgPoList != null && androidSceneImgPoList.size() > 0) {
                            List<CustomerVo.AndroidSceneImg> androidSceneImgVoList = androidSceneImgPoList.stream().map(androidSceneImgPo ->
                            {
                                CustomerVo.AndroidSceneImg androidSceneImgVo = new  CustomerVo.AndroidSceneImg();

                                BeanUtils.copyProperties(androidSceneImgPo,androidSceneImgVo);
                                return androidSceneImgVo;
                            }).collect(Collectors.toList());
                            //设置 场景图片列表
                            androidSceneVo.setAndroidSceneImgList(androidSceneImgVoList);
                        }

                        return androidSceneVo;
                    }).collect(Collectors.toList());
                    //设置查询到的 场景列表
                    androidConfigVo.setAndroidSceneList(androidSceneVoList);
                }else{
                    androidConfigVo.setAndroidSceneList(null);
                }
                customerVo.setAndroidConfig(androidConfigVo);
            }else{
                customerVo.setAndroidConfig(null);
            }

            /*管理后台配置信息*/
            CustomerVo.BackendConfig backendConfigVo = new  CustomerVo.BackendConfig();
            BackendConfigPo backendConfigPo = backendConfigMapper.selectConfigByCustomerId(customerPo.getId());
            if(backendConfigPo!=null){
                BeanUtils.copyProperties(backendConfigPo,backendConfigVo);
                customerVo.setBackendConfig(backendConfigVo);
            }else{
                customerVo.setBackendConfig(null);
            }

        }
        return customerVo;
    }

    public ApiResponse<Boolean> updateWebsiteInfo(CustomerVo.BackendConfig backendConfig)throws Exception{
        //根据当前的customerId查询与客户相关的信息
        Integer customerId = obtainCustomerId(false);
        BackendConfigPo backendConfigPo = this.backendConfigMapper.selectConfigByCustomerId(customerId);
        //如果为非客户，返回错误信息，超级管理员不修改logo和tile
        if(null == backendConfigPo){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"未查到客户信息");
        }
        backendConfigPo.setLogo(backendConfig.getLogo());
        backendConfigPo.setName(backendConfig.getName());
        this.backendConfigMapper.updateById(backendConfigPo);
        return new ApiResponse<>(RetCode.OK,"更新成功",true);
    }


    /**
     * 根据二级域名查询 客户
     *
     * @param SLD
     * @return
     */
    public CustomerVo selectBySLD(String SLD) {
        CustomerVo customerVo = new CustomerVo();
        CustomerPo customerPo = customerMapper.selectBySLD(SLD);
        if(null!=customerPo){
            BeanUtils.copyProperties(customerPo, customerVo);
            return customerVo;
        }else{
            return  null;
        }
    }

    /**
     * 根据二级域名查询 客户的后台配置
     *
     * @param SLD
     * @return
     */
    public CustomerVo.BackendLogo selectBackendConfigBySLD(String SLD) {
        CustomerVo.BackendLogo backendLogo = new CustomerVo.BackendLogo();
        BackendConfigPo backendConfigPo = backendConfigMapper.selectBackendConfigBySLD(SLD);
        if(null!=backendConfigPo){
            backendLogo.setLogo(backendConfigPo.getLogo());
            backendLogo.setName(backendConfigPo.getName());
            return backendLogo;
        }else{
            return  null;
        }
    }



    public Boolean deleteCustomerById(Integer customerId) {
        Boolean ret = false;
        //先删除 该 功能
        CustomerPo delCustomerPo = new CustomerPo();
        delCustomerPo.setStatus(CommonConstant.STATUS_DEL);
        delCustomerPo.setId(customerId);
        ret = customerMapper.updateStatusById(delCustomerPo) > 0;
        return ret;
    }

    public Integer obtainCustomerId(boolean fromServer){

        String customerId;
        String userHost = commonUtil.obtainSecondHost();
        if(StringUtils.isNotBlank(userHost)){
            if(!StringUtils.contains(skipRemoteHost,userHost)){
                String customerKey = CUSTOMERID_PREIX+userHost;
                if(!"www".equals(userHost)){
                    if(!fromServer){
                        try{
                            customerId = stringRedisTemplate.opsForValue().get(customerKey);
                            if(null!=customerId){
                                return Integer.parseInt(customerId);
                            }else{
                                return obtainCustomerId(true);
                            }
                        }catch (Exception e){
                            log.error("从Redis获取当前域名的客户主键失败={}", e);
                            return  obtainCustomerId(true);
                        }


                    }else{
                        CustomerPo customerPo = customerMapper.selectBySLD(userHost);
                        if(customerPo!=null){
                            customerId = customerPo.getId().toString();
                            try{
                                stringRedisTemplate.opsForValue().set(customerKey, customerId);
                                stringRedisTemplate.expire(customerKey, 7000, TimeUnit.SECONDS);
                            }catch (Exception e){
                                log.error("往Redis存储当前域名的客户主键失败={}",e);
                                return Integer.parseInt(customerId);
                            }

                            return Integer.parseInt(customerId);
                        }
                    }

                }else {
                    return null;
                }
            }

        }else{
            return null;
        }


        return null;
    }

}
