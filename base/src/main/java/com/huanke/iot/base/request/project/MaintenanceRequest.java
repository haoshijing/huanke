package com.huanke.iot.base.request.project;

import lombok.Data;

/**
 * 描述:
 * 工程维保
 *
 * @author onlymark
 * @create 2018-11-21 上午8:43
 */
@Data
public class MaintenanceRequest {
    private Integer projectId;
    private Integer currentPage = 1;
    private Integer limit = 10;
}
