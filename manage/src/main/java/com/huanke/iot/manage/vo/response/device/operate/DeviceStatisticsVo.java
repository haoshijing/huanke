package com.huanke.iot.manage.vo.response.device.operate;

import lombok.Data;

/**
 * @author caikun
 * @date 2018/10/8 下午9:15
 **/
@Data
public class DeviceStatisticsVo {
    private String month;
    private Long deviceCount;
    private Long addCount;
    private String addPercent;
}
