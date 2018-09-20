package com.huanke.iot.manage.controller.customer;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.device.ablity.DeviceAblityService;
import com.huanke.iot.manage.vo.request.customer.CustomerQueryRequest;
import com.huanke.iot.manage.vo.request.customer.CustomerVo;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityVo;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceTypeAblitysVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 客户
 */
@RestController
@RequestMapping("/api/customer")
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private DeviceAblityService deviceAblityService;

    /**
     * 添加客户信息
     *
     * @return
     */
    @ApiOperation("添加客户信息")
    @PostMapping(value = "saveDetail")
    public ApiResponse<Integer> saveDetail(@RequestBody CustomerVo customerVo) {
        try {
            if (StringUtils.isBlank(customerVo.getName())) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "客户名称不能为空");
            }
            return this.customerService.saveDetail(customerVo);
        } catch (Exception e) {
            log.error("添加客户信息失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "添加客户信息失败");
        }
    }

    /**
     * 修改客户信息
     *
     * @return
     */
    @ApiOperation("修改客户信息")
    @PutMapping(value = "updateDetail")
    public ApiResponse<Integer> updateDetail(@RequestBody CustomerVo customerVo) {

        try {
            if (StringUtils.isBlank(customerVo.getName())) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "客户名称不能为空");
            }
            return customerService.saveDetail(customerVo);
        } catch (Exception e) {
            log.error("修改客户信息失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "修改客户信息失败");
        }
    }

    /**
     * 查询客户列表
     *
     * @param customerQueryRequest
     * @return 返回客户列表
     * @throws Exception
     */
    @ApiOperation("查询客户列表")
    @PostMapping(value = "/select")
    public ApiResponse<List<CustomerVo>> selectList(@RequestBody CustomerQueryRequest customerQueryRequest) throws Exception {
        List<CustomerVo> customerVos = customerService.selectList(customerQueryRequest);
        return new ApiResponse<>(customerVos);
    }

    /**
     * 查询客户列表-没有分页
     *
     * @param
     * @return 返回客户列表
     * @throws Exception
     */
    @ApiOperation("查询所有的客户")
    @PostMapping(value = "/selectAllCustomers")
    public ApiResponse<List<CustomerVo>> selectAllCustomers() throws Exception {
        List<CustomerVo> customerVos = customerService.selectAllCustomers();
        return new ApiResponse<>(customerVos);
    }

    /**
     * 根据客户主键查询客户详情
     *
     * @param id
     * @return
     * @throws Exception
     */
    @ApiOperation("根据客户主键查询客户详情")
    @GetMapping(value = "/selectById/{id}")
    public ApiResponse<CustomerVo> selectById(@PathVariable("id") Integer id) throws Exception {
        CustomerVo customerVo = customerService.selectById(id);
        return new ApiResponse<>(customerVo);
    }

    /**
     * 根据二级域名查询客户详情
     *
     * @param SLD
     * @return
     * @throws Exception
     */
    @ApiOperation("根据设备类型和功能项类型 查询功能列表")
    @GetMapping(value = "/selectBySLD/{SLD}")
    public ApiResponse<CustomerVo> selectBySLD(@PathVariable("SLD") String SLD) throws Exception {
//
        CustomerVo customerVo = customerService.selectBySLD(SLD);
        if (customerVo != null) {
            return new ApiResponse<>(customerVo);
        } else {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该客户不存在");
        }

//        return new ApiResponse<>(RetCode.PARAM_ERROR, "登录名不可为空");
    }

    /**
     * 根据二级域名查询客户详情
     *
     * @param SLD
     * @return
     * @throws Exception
     */
    @ApiOperation("根据设备类型和功能项类型 查询功能列表")
    @GetMapping(value = "/selectBackendConfigBySLD/{SLD}")
    public ApiResponse<CustomerVo.BackendLogo> selectBackendConfigBySLD(@PathVariable("SLD") String SLD) throws Exception {
//
        CustomerVo.BackendLogo backendLogoVo = customerService.selectBackendConfigBySLD(SLD);
        if (backendLogoVo != null) {
            return new ApiResponse<>(backendLogoVo);
        } else {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该客户不存在");
        }

//        return new ApiResponse<>(RetCode.PARAM_ERROR, "登录名不可为空");
    }

    @ApiOperation("根据Id删除客户")
    @DeleteMapping(value = "/deleteCustomerById/{id}")
    public ApiResponse<Boolean> deleteCustomerById(@PathVariable("id") Integer customerId) throws Exception {
        try {
            Boolean ret = customerService.deleteCustomerById(customerId);
            return new ApiResponse<>(ret);
        } catch (Exception e) {
            log.error("删除客户信息失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "删除客户信息失败");
        }
    }
}
