package com.huanke.iot.manage.vo.response.device.customer;

import lombok.Data;

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
}
