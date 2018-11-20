package com.huanke.iot.base.po.statstic;

import lombok.Data;

/**
 * @author caikun
 * @date 2018/11/16 下午11:51
 **/
@Data
public class StatsticCustomerUserLivePo {

    private Integer id;
    private Integer customerId;
    private Integer userLiveCount;
    private Integer statisticYear;
    private Integer statisticMonth;
    private Integer statisticDay;
    private Integer statisticHour;
    private Integer statisticMin;
    private Integer statsticSec;


}
