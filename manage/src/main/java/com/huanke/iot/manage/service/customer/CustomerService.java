package com.huanke.iot.manage.service.customer;

import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.manage.vo.request.customer.SaveCutomerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 客户
 */
@Repository
public class CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

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
        int id = this.customerMapper.insert(customerPo);

        //设备型号信息


        //H5配置信息


        //安卓配置信息


        //管理后台配置信息


    }
}
