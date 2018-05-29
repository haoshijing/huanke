package com.huanke.iot.manage.controller.response;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年05月29日 18:55
 **/
@Data
public class DashBoardIndexVo {
    /**
     * 设备总数
     */
    private Integer deviceCount;

    /**
     * 用户总数
     */
    private Integer userCount;
}
