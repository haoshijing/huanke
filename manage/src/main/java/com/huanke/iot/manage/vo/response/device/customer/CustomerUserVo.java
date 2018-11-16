package com.huanke.iot.manage.vo.response.device.customer;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Caik
 * @date 2018/10/8 13:39
 */
@Data
public class CustomerUserVo {

    @Data
    public static class CustomerUserCountVo {
        private String month;
        private Long userCount;
        private Long addCount;
        private String addPercent;
    }

    @Data
    public static class CustomerUserMonthLiveCountVo {
        private String month;
        private BigDecimal userLiveCount;
    }

    @Data
    public static class CustomerUserHourLiveCountVo {
        private String hour;
        private BigDecimal userLiveCount;
    }
}
