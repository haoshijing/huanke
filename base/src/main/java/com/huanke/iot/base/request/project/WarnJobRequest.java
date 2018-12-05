package com.huanke.iot.base.request.project;

import lombok.Data;

/**
 * 描述:
 * 报警列表查询
 *
 * @author onlymark
 * @create 2018-12-05 上午7:20
 */
@Data
public class WarnJobRequest {
    private Integer currentPage = 1;
    private Integer limit = 10;
}
