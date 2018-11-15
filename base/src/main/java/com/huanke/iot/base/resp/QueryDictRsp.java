package com.huanke.iot.base.resp;

import lombok.Data;

/**
 * 描述:
 * 查字典返回类
 *
 * @author onlymark
 * @create 2018-11-15 下午2:40
 */
@Data
public class QueryDictRsp {
    private Integer id;
    private String label;
    private Integer value;
}
