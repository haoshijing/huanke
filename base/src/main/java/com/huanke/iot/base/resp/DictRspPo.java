package com.huanke.iot.base.resp;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-11-14 下午3:25
 */
@Data
public class DictRspPo {
    private Integer id;
    private String label;
    private String description;
    private Date createTime;
    private Date updateTime;
    private String createName;
    private String updateName;
    private Integer isDelete;
}
