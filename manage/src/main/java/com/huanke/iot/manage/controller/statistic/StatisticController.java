package com.huanke.iot.manage.controller.statistic;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.manage.service.customer.CustomerUserService;
import com.huanke.iot.manage.service.device.operate.DeviceOperateService;
import com.huanke.iot.manage.service.device.typeModel.DeviceTypeService;
import com.huanke.iot.manage.service.statistic.StatisticService;
import com.huanke.iot.manage.vo.request.device.operate.DeviceHomePageStatisticVo;
import com.huanke.iot.manage.vo.request.device.operate.DeviceLocationCountRequest;
import com.huanke.iot.manage.vo.response.device.customer.CustomerUserVo;
import com.huanke.iot.manage.vo.response.device.operate.DeviceLocationCountVo;
import com.huanke.iot.manage.vo.response.device.operate.DeviceOnlineStatVo;
import com.huanke.iot.manage.vo.response.device.operate.DeviceStatisticsVo;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceModelVo;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceTypeVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

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



















    private static int count=0;

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = Integer.valueOf(scanner.nextLine());
        if(N==0){
            return;
        }else{
            Queen(N);
            System.out.println("总共:"+count+"中组合!");
        }
        scanner.close();
    }

    public static void Queen(int N){
        int[][] cheers = new int[N][N];
        _cheers(cheers, 0,N);
        System.out.println("**********************");
        outPutCheers(cheers);
        System.out.println("**********************");
    }

    public static void _cheers(int[][] cheers,int row,int N){
        if(row+1>N){
            count++;
            System.out.println("第"+count+"种:");
            outPutCheers(cheers);
            System.out.println("-------------------------------------");
            return;
        }

        for(int column=0;column<N;column++){
            boolean flag = true;
            //中上
            for(int i=row;i>=0;i--){
                if(cheers[i][column]==1){
                    flag = false;break;
                }
            }
            if(!flag) continue;
            //左上
            for(int i=row,j=column;;i--,j--){
                if(i<0||j<0){
                    break;
                }
                if(cheers[i][j]==1){
                    flag = false;break;
                }
            }
            if(!flag) continue;
            //右上
            for(int i=row,j=column;;i--,j++){
                if(i<0||j>=N) break;
                if(cheers[i][j]==1){
                    flag = false;break;
                }
            }
            if(!flag) continue;
            cheers[row][column]=1;
            _cheers(cheers, row+1, N);
            cheers[row][column]=0;
        }
    }
    public static void outPutCheers(int[][] cheers){
        for(int i=0;i<cheers.length;i++){
            for(int j=0;j<cheers[i].length;j++){
                System.out.print(cheers[i][j]+" ");
            }
            System.out.println();
        }
    }


}
