package com.huanke.iot.manage.controller.customer;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.customer.CustomerUserService;
import com.huanke.iot.manage.vo.request.customer.CustomerUserQueryRequest;
import com.huanke.iot.manage.vo.response.device.BaseListVo;
import com.huanke.iot.manage.vo.response.device.customer.CustomerUserVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Caik
 * @date 2018/10/8 13:36
 */
@RestController
@RequestMapping("/api/customerUser")
@Slf4j
public class CustomerUserController {
    @Autowired
    private CustomerUserService customerUserService;

    @ApiOperation("查询用户列表及总数")
    @PostMapping(value = "/selectCustomerUser")
    public ApiResponse<BaseListVo> selectCustomerUser(@RequestBody CustomerUserQueryRequest customerUserQueryRequest){
        try{
            return this.customerUserService.queryCustomerUserList(customerUserQueryRequest);
        }catch (Exception e){
            log.error("查询用户列表及总数异常：{}",e);
            return new ApiResponse<>(RetCode.ERROR,"查询用户列表及总数失败");
        }
    }
}
