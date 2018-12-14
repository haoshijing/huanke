package com.huanke.iot.manage.controller.statistic;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.manage.service.customer.CustomerUserService;
import com.huanke.iot.manage.service.device.operate.DeviceOperateService;
import com.huanke.iot.manage.service.device.typeModel.DeviceTypeService;
import com.huanke.iot.manage.service.statistic.StatisticService;
import com.huanke.iot.manage.vo.request.device.operate.DeviceHomePageStatisticVo;
import com.huanke.iot.manage.vo.request.device.operate.DeviceLocationCountRequest;
import com.huanke.iot.manage.vo.response.device.WeatherVo;
import com.huanke.iot.manage.vo.response.device.customer.CustomerUserVo;
import com.huanke.iot.manage.vo.response.device.operate.DeviceLocationCountVo;
import com.huanke.iot.manage.vo.response.device.operate.DeviceOnlineStatVo;
import com.huanke.iot.manage.vo.response.device.operate.DeviceStatisticsVo;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceModelVo;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceTypeVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author caikun
 * @date 2018/10/8 下午9:06
 **/

@RestController
@RequestMapping("/api/statistics")
@Slf4j
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @ApiOperation("每月新增用户统计")
    @GetMapping(value = "/customerUserCountPerMonth")
    public ApiResponse<List<CustomerUserVo>> customerUserCountPerMonth() {
        List<CustomerUserVo> deviceTypePercentList = statisticService.selectUserCountPerMonth();

        return new ApiResponse<>(deviceTypePercentList);
    }

    @ApiOperation("每月活跃用户统计")
    @GetMapping(value = "/selectLiveCustomerUserCountPerMonth")
    public ApiResponse<List<CustomerUserVo.CustomerUserMonthLiveCountVo>> selectLiveCustomerUserCountPerMonth() {
        List<CustomerUserVo.CustomerUserMonthLiveCountVo>
        customerUserMonthLiveCountVos = statisticService.selectLiveCustomerUserCountPerMonth();

        return new ApiResponse<>(customerUserMonthLiveCountVos);
    }

    @ApiOperation("每天活跃用户统计")
    @GetMapping(value = "/selectLiveCustomerUserCountPerHour")
    public ApiResponse<List<CustomerUserVo.CustomerUserHourLiveCountVo>> selectLiveCustomerUserCountPerHour() {
        List<CustomerUserVo.CustomerUserHourLiveCountVo>
                customerUserHourLiveCountVos = statisticService.selectLiveCustomerUserCountPerHour();

        return new ApiResponse<>(customerUserHourLiveCountVos);
    }

    @ApiOperation("设备类型统计")
    @GetMapping(value = "/typePercent")
    public ApiResponse<List<DeviceTypeVo.DeviceTypePercent>> typePercent() {
        List<DeviceTypeVo.DeviceTypePercent> deviceTypePercentList = statisticService.selectTypePercentPerMonth();

        return new ApiResponse<>(deviceTypePercentList);
    }


    @ApiOperation("设备型号统计")
    @GetMapping(value = "/modelPercent")
    public ApiResponse<List<DeviceModelVo.DeviceModelPercent>> modelPercent() {
        List<DeviceModelVo.DeviceModelPercent> deviceModelPercentList = statisticService.selectModelPercent();

        return new ApiResponse<>(deviceModelPercentList);
    }


    @ApiOperation("每月新增设备统计")
    @GetMapping(value = "/deviceCountPerMonth")
    public ApiResponse<List<DeviceStatisticsVo>> deviceCountPerMonth() {
        List<DeviceStatisticsVo> deviceStatisticsVos = statisticService.selectDeviceCountPerMonth();

        return new ApiResponse<>(deviceStatisticsVos);
    }

    @ApiOperation("今日新增设备统计")
    @GetMapping(value = "/newDeviceCountOfToday")
    public ApiResponse<Integer> newDeviceCountOfToday(){
        return this.statisticService.selectDeviceByDay();
    }
    @ApiOperation("设备区域统计")
    @PostMapping(value = "/deviceLocationCount")
    public ApiResponse<DeviceLocationCountVo> deviceLocationCount(@RequestBody DeviceLocationCountRequest daeviceLocationCountRequest){
        return this.statisticService.queryLocationCount(daeviceLocationCountRequest);
    }
    @ApiOperation("首页统计")
    @GetMapping(value = "/queryHomePageStatistic")
    public ApiResponse<DeviceHomePageStatisticVo> queryHomePageStatistic(){
        return this.statisticService.queryHomePageStatistic();
    }

    @ApiOperation("天气接口")
    @PostMapping(value = "/queryWeather")
    public ApiResponse<WeatherVo> queryHomePageStatistic(@RequestBody String location){
        if(StringUtils.isNotEmpty(location)) {
            return new ApiResponse(this.statisticService.queryWeather(location));
        }
        return null;
    }
}
