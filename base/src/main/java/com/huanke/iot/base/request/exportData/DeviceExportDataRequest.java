package com.huanke.iot.base.request.exportData;

import lombok.Data;

/**
 * 描述:
 * 字典请求类
 *
 * @author onlymark
 * @create 2018-11-14 上午10:16
 */
@Data
public class DeviceExportDataRequest {
    private Integer currentPage = 1;
    private Integer limit = 10;
}
