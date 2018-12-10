package com.huanke.iot.manage.service.customer;


import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
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

    public ApiResponse<BaseListVo> queryCustomerUserList(CustomerUserQueryRequest customerUserQueryRequest){
        BaseListVo baseListVo = new BaseListVo();
        List<CustomerUserListVo> customerUserListVoList =queryCustomerUser(customerUserQueryRequest);
        if(null == customerUserListVoList || 0 == customerUserListVoList.size()){
            return new ApiResponse<>(RetCode.OK,"暂无数据");
        }
        Integer totalCount = queryCount(customerUserQueryRequest);
        baseListVo.setDataList(customerUserListVoList);
        baseListVo.setTotalCount(totalCount);
        return new ApiResponse<>(RetCode.OK,"查询用户列表及总数成功",baseListVo);
    }

    public List<CustomerUserListVo> queryCustomerUser(CustomerUserQueryRequest customerUserQueryRequest){
        Integer offset = (customerUserQueryRequest.getPage() - 1) * customerUserQueryRequest.getLimit();
        Integer limit = customerUserQueryRequest.getLimit();
        Integer customerId = customerService.obtainCustomerId(false);
        CustomerUserPo queryPo = new CustomerUserPo();
        if(null != customerUserQueryRequest){
            BeanUtils.copyProperties(customerUserQueryRequest,queryPo);
            queryPo.setNickname(customerUserQueryRequest.getUserName());
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
    public Integer queryCount(CustomerUserQueryRequest customerUserQueryRequest){
        Integer customerId = customerService.obtainCustomerId(false);
        CustomerUserPo queryPo = new CustomerUserPo();
        if(null != customerUserQueryRequest){
            BeanUtils.copyProperties(customerUserQueryRequest,queryPo);
            queryPo.setNickname(customerUserQueryRequest.getUserName());
        }
        if (customerUserQueryRequest.getCustomerId() == null) {
            queryPo.setCustomerId(customerId);
        }
        Integer cutomerUserCount = this.customerUserMapper.selectCount(queryPo);
        return cutomerUserCount;
    }
}
