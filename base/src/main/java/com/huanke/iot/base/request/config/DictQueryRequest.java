package com.huanke.iot.base.request.config;

import lombok.Data;

/**
 * 描述:
 * 字典请求类
 *
 * @author onlymark
 * @create 2018-11-14 上午10:16
 */
@Data
public class DictQueryRequest {
    private String name;
    private String type;
    private Integer status;
    private Integer currentPage = 1;
    private Integer limit = 10;
    private Integer isDelete;
}
