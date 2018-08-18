package com.huanke.iot.manage.controller.customer;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.vo.request.customer.SaveCutomerRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户
 */
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 保存客户信息
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResponse<Boolean> saveDetail(@RequestBody SaveCutomerRequest saveCutomerRequest) {
        if (StringUtils.isBlank(saveCutomerRequest.getName())) {
            return new ApiResponse<>(RetCode.PARAM_ERROR,"客户名称不能为空");
        }


        this.customerService.saveDetail(saveCutomerRequest);
        return new ApiResponse<>(true);
    }
}
