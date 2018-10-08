package com.huanke.iot.manage.controller.statistic;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.manage.service.customer.CustomerUserService;
import com.huanke.iot.manage.service.device.operate.DeviceOperateService;
import com.huanke.iot.manage.service.device.typeModel.DeviceTypeService;
import com.huanke.iot.manage.vo.response.device.customer.CustomerUserVo;
import com.huanke.iot.manage.vo.response.device.operate.DeviceStatisticsVo;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceTypeVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author caikun
 * @date 2018/10/8 下午9:06
 **/

@RestController
@RequestMapping("/api/statistics")
@Slf4j
public class StatisticController {

    @Autowired
    private CustomerUserService customerUserService;

    @Autowired
    private DeviceTypeService deviceTypeService;

    @Autowired
    private DeviceOperateService deviceOperateService;

    @ApiOperation("用户统计")
    @GetMapping(value = "/selectCustomerUserCount")
    public ApiResponse<List<CustomerUserVo>> selectCustomerUserCount() {
        List<CustomerUserVo> deviceTypePercentList = customerUserService.selectCustomerUserCount();

        return new ApiResponse<>(deviceTypePercentList);
    }

    @ApiOperation("设备类型统计")
    @GetMapping(value = "/selectTypePercent")
    public ApiResponse<List<DeviceTypeVo.DeviceTypePercent>> selectTypePercent() {
        List<DeviceTypeVo.DeviceTypePercent> deviceTypePercentList = deviceTypeService.selectTypePercent();

        return new ApiResponse<>(deviceTypePercentList);
    }

    @ApiOperation("设备统计")
    @GetMapping(value = "/selectDeviceCount")
    public ApiResponse<List<DeviceStatisticsVo>> selectDeviceCount() {
        List<DeviceStatisticsVo> deviceStatisticsVos = deviceOperateService.selectDeviceCount();

        return new ApiResponse<>(deviceStatisticsVos);
    }
}
