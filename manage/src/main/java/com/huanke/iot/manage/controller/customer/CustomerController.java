package com.huanke.iot.manage.controller.customer;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.vo.request.customer.CustomerQueryRequest;
import com.huanke.iot.manage.vo.request.customer.CustomerVo;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityVo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 添加客户信息
     * @return
     */
    @ApiOperation("添加客户信息")
    @PostMapping(value = "")
    public ApiResponse<Integer> saveDetail(@RequestBody CustomerVo customerVo) {
        if (StringUtils.isBlank(customerVo.getName())) {
            return new ApiResponse<>(RetCode.PARAM_ERROR,"客户名称不能为空");
        }
        return this.customerService.saveDetail(customerVo);
    }

    /**
     * 修改客户信息
     * @return
     */
    @ApiOperation("添加客户信息")
    @PutMapping(value = "")
    public ApiResponse<Boolean> updateDetail(@RequestBody CustomerVo customerVo) {
        if (StringUtils.isBlank(customerVo.getName())) {
            return new ApiResponse<>(RetCode.PARAM_ERROR,"客户名称不能为空");
        }
        this.customerService.saveDetail(customerVo);
        return new ApiResponse<>(true);
    }

    /**
     * 查询客户列表
     * @param customerQueryRequest
     * @return 返回客户列表
     * @throws Exception
     */
    @ApiOperation("查询客户列表")
    @PostMapping(value = "/select")
    public ApiResponse<List<CustomerVo>> selectList(@RequestBody CustomerQueryRequest customerQueryRequest) throws Exception{
        List<CustomerVo> customerVos =  customerService.selectList(customerQueryRequest);
        return new ApiResponse<>(customerVos);
    }


    /**
     * 根据客户主键查询客户详情
     * @param id
     * @return
     * @throws Exception
     */
    @ApiOperation("根据客户主键查询客户详情")
    @GetMapping(value = "/selectById/{id}")
    public ApiResponse<CustomerVo> selectById(@PathVariable("id")Integer id) throws Exception{
        CustomerVo customerVo =  customerService.selectById(id);
        return new ApiResponse<>(customerVo);
    }

    @ApiOperation("根据Id删除功能")
    @DeleteMapping(value = "/deleteCustomerById/{id}")
    public ApiResponse<Boolean> deleteCustomerById(@PathVariable("id") Integer customerId) throws Exception{
        Boolean ret =  customerService.deleteCustomerById(customerId);
        return new ApiResponse<>(ret);
    }
}
