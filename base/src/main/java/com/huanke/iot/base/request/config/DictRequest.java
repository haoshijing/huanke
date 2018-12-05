package com.huanke.iot.base.request.config;

import lombok.Data;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-11-14 下午1:48
 */
@Data
public class DictRequest {
    private Integer id;
    private Integer value;
    private String label;
    private String type;
    private String description;
    private Integer sort;
    private String remarks;
    private Integer isDelete;//是否删除：0-启用；1-删除，2-禁用
}
