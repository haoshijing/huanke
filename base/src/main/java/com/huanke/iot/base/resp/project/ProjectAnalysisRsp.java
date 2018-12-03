package com.huanke.iot.base.resp.project;

import lombok.Data;

/**
 * 描述:
 * 工程分析返回rsp
 *
 * @author onlymark
 * @create 2018-12-01 上午9:14
 */
@Data
public class ProjectAnalysisRsp {
    private Integer projectGroupNum; //工程数量
    private Integer deviceNum;  //设备数量
    private Integer maintenanceCount;   //维保次数
    private Integer deviceWarnCount;    //设备告警次数
    private String onlineDeviceProportion;  //在线设备占比图
    private String powerDeviceProportion;    //开机设备占比图
}
