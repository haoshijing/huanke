package com.huanke.iot.manage.controller.customer;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.util.CommonUtil;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.vo.request.customer.CustomerQueryRequest;
import com.huanke.iot.manage.vo.request.customer.CustomerVo;
import com.huanke.iot.manage.vo.response.device.customer.CustomerListVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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
    private CommonUtil commonUtil;

    @Value("${skipRemoteHost}")
    private String skipRemoteHost;
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
    public ApiResponse<CustomerListVo> selectList(@RequestBody CustomerQueryRequest customerQueryRequest) throws Exception {
        CustomerListVo customerListVo = customerService.selectList(customerQueryRequest);
        return new ApiResponse<>(customerListVo);
    }

    /**
     * 查询客户列表-没有分页 下拉框用
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

    @ApiOperation("根据客户管理员主键查询客户详情")
    @GetMapping(value = "/selectByUserId/{userId}")
    public ApiResponse<CustomerVo> selectByUserId(@PathVariable("userId") Integer userId) throws Exception {
        return customerService.selectByUserId(userId);
    }

    @ApiOperation("用户自己修改基本信息")
    @PostMapping(value = "/updateOwnerBaseInfo")
    public ApiResponse<Boolean> updateOwnerBaseInfo(@RequestBody CustomerVo customerVo){
        try {
            return this.customerService.updateOwnerBaseInfo(customerVo);
        }catch (Exception e){
            log.error("更改用户基本信息异常 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"客户基本信息更改错误");
        }
    }

    @ApiOperation("用户自己修改H5APP设置")
    @PostMapping(value = "/updateOwnerH5Info")
    public ApiResponse<Boolean> updateOwnerH5Info(@RequestBody CustomerVo.H5Config h5Config){
        try {
            return this.customerService.updateOwnerH5Info(h5Config);
        }catch (Exception e){
            log.error("更改用户H5APP设置异常 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"客户H5APP设置更改错误");
        }
    }

    @ApiOperation("用户自己修改安卓APP设置")
    @PostMapping(value = "/updateOwnerAndroidInfo")
    public ApiResponse<Boolean> updateOwnerAndroidInfo(@RequestBody CustomerVo.AndroidConfig androidConfig){
        try {
            return this.customerService.updateOwnerAndroidInfo(androidConfig);
        }catch (Exception e){
            log.error("更改用户安卓APP设置异常 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"客户安卓APP设置更改错误");
        }
    }

    @ApiOperation("用户自己修改后台设置")
    @PostMapping(value = "/updateOwnerBackendInfo")
    public ApiResponse<Boolean> updateOwnerBackendInfo(@RequestBody CustomerVo.BackendConfig backendConfig){
        try {
            return this.customerService.updateOwnerBackendInfo(backendConfig);
        }catch (Exception e){
            log.error("更改用户后台设置异常 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"客户后台设置更改错误");
        }
    }

    @ApiOperation("用户自己修改logo和title")
    @PostMapping(value = "/updateWebsiteInfo")
    public ApiResponse<Boolean> updateWebsiteInfo(@RequestBody CustomerVo.BackendLogo backendLogo){
        try {
            return this.customerService.updateWebsiteInfo(backendLogo);
        }catch (Exception e){
            log.error("更改用户logo和title异常 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"客户logo和title更改错误");
        }
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
     * @return
     * @throws Exception
     */
    @ApiOperation("根据设备类型和功能项类型 查询功能列表")
    @GetMapping(value = "/selectBackendConfigBySLD")
    public ApiResponse<CustomerVo.BackendLogo> selectBackendConfigBySLD() throws Exception {

        String userHost = commonUtil.obtainSecondHost();
        CustomerVo.BackendLogo backendLogoVo = null;
        if(StringUtils.isNotBlank(userHost)){
            if(!StringUtils.contains(skipRemoteHost,userHost)){
                backendLogoVo = customerService.selectBackendConfigBySLD(userHost);
            }
        }else{
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该客户不存在");
        }

        return new ApiResponse<>(backendLogoVo);

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
