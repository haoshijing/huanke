package com.huanke.iot.manage.controller.customer;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.vo.request.customer.CustomerVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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
    public ApiResponse<Boolean> saveDetail(@RequestBody CustomerVo customerVo) {
        if (StringUtils.isBlank(customerVo.getName())) {
            return new ApiResponse<>(RetCode.PARAM_ERROR,"客户名称不能为空");
        }
        this.customerService.saveDetail(customerVo);
        return new ApiResponse<>(true);
    }

    /**
     * 查询客户详情
     * @param id
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ApiResponse<CustomerVo> queryDetail(Integer id) {
        CustomerVo customerVo = new CustomerVo();

        return new ApiResponse<>(customerVo);
    }

    /**
     * 查询客户列表
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResponse<List<Map<String, Object>>> queryList() {


    }
}
