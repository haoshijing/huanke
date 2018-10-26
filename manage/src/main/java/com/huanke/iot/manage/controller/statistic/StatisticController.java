package com.huanke.iot.manage.controller.statistic;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.manage.service.customer.CustomerUserService;
import com.huanke.iot.manage.service.device.operate.DeviceOperateService;
import com.huanke.iot.manage.service.device.typeModel.DeviceTypeService;
import com.huanke.iot.manage.service.statistic.StatisticService;
import com.huanke.iot.manage.vo.request.device.operate.DeviceHomePageStatisticVo;
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
    private StatisticService statisticService;
    
    @ApiOperation("每月新增用户统计")
    @GetMapping(value = "/customerUserCountPerMonth")
    public ApiResponse<List<CustomerUserVo>> customerUserCountPerMonth() {
        List<CustomerUserVo> deviceTypePercentList = statisticService.selectUserCountPerMonth();

        return new ApiResponse<>(deviceTypePercentList);
    }

    @ApiOperation("设备类型统计")
    @GetMapping(value = "/typePercent")
    public ApiResponse<List<DeviceTypeVo.DeviceTypePercent>> typePercent() {
        List<DeviceTypeVo.DeviceTypePercent> deviceTypePercentList = statisticService.selectTypePercentPerMonth();

        return new ApiResponse<>(deviceTypePercentList);
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
        return this.statisticService.selectDeviceByDay(null);
    }


    @ApiOperation("首页统计")
    @GetMapping(value = "/queryHomePageStatistic")
    public ApiResponse<DeviceHomePageStatisticVo> queryHomePageStatistic(){
        return this.statisticService.queryHomePageStatistic();
    }


}
