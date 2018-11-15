package com.huanke.iot.manage.service.customer;


import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.DeviceCustomerUserRelationMapper;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.manage.vo.request.customer.CustomerUserQueryRequest;
import com.huanke.iot.manage.vo.response.device.BaseListVo;
import com.huanke.iot.manage.vo.response.device.customer.CustomerUserListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户
 */
@Slf4j
@Repository
public class CustomerUserService {
    @Autowired
    private CustomerUserMapper customerUserMapper;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private DeviceCustomerUserRelationMapper deviceCustomerUserRelationMapper;

    public List<CustomerUserListVo> queryCustomerUser(CustomerUserQueryRequest customerUserQueryRequest){
        BaseListVo baseListVo = new BaseListVo();
        Integer offset = (customerUserQueryRequest.getPage() - 1) * customerUserQueryRequest.getLimit();
        Integer limit = customerUserQueryRequest.getLimit();
        Integer customerId = customerService.obtainCustomerId(false);
        CustomerUserPo queryPo = new CustomerUserPo();
        if(null != customerUserQueryRequest){
            BeanUtils.copyProperties(customerUserQueryRequest,queryPo);
        }
        if (customerUserQueryRequest.getCustomerId() == null) {
            queryPo.setCustomerId(customerId);
        }
        List<CustomerUserPo> customerUserPoList = this.customerUserMapper.selectList(queryPo,limit,offset);
        if(null == customerUserPoList || 0 == customerUserPoList.size()){
            return null;
        }
        List<CustomerUserListVo> customerUserListVoList = customerUserPoList.stream().map(eachPo ->{
            CustomerUserListVo customerUserListVo = new CustomerUserListVo();
            BeanUtils.copyProperties(eachPo,customerUserListVo);
            return customerUserListVo;
        }).collect(Collectors.toList());
        return customerUserListVoList;
    }
}
