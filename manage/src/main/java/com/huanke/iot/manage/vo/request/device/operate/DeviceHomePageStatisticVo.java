package com.huanke.iot.manage.vo.request.device.operate;

import io.swagger.models.auth.In;
import lombok.Data;

/**
 * @author Caik
 * @date 2018/10/26 13:53
 * 首页统计返回对象
 */
@Data
public class DeviceHomePageStatisticVo {

    /*当前设备总数*/
    private Integer deviceTotalCount;
    /*今日设备新增数*/
    private Integer todayDeviceAddCount;
    /*今日设备订单*/
    private Integer todayDeviceOrderCount;
    /*今日设备故障数*/
    private Integer todayDeviceBugCount;

    /*当前用户总人数*/
    private Integer totalUserCount;
    /*昨日用户增长数*/
    private Integer preDayUserAddCount;
    /*今日活跃用户数*/
    private Integer todayUserLiveCount;
    /*今日订单数*/
    private Integer todayOrderCount;
    /*在线设备数*/
    private Integer deviceOnlineCount;
    /*离线设备数*/
    private Integer deviceOfflineCount;
    /*设备在线率*/
    private String deviceOnlinePercent;

    /*PM 2.5 传感器总数*/
    private Integer totalPMCount;
    /*优良环境比率*/
    private Integer envPercent;
    /*当前设备告警数*/
    private Integer nowDeviceAlarmCount;
    /*今日分润账单数*/
    private Integer todayReceiveBillCount;


}
