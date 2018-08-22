package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

/**
 * @author sixiaojun
 * @version 2018-08-15
 * 查询设备列表返回类
 **/
@Data
public class DeviceListQueryRequest {
    /**
     * 分页
     */
    private Integer page;
    /**
     * 每页显示数量
     */
    private Integer limit;
}
