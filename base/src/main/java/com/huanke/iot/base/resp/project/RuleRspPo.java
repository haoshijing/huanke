package com.huanke.iot.base.resp.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-11-16 上午9:42
 */
@Data
public class RuleRspPo {
    private Integer id;
    private String name;
    private Integer typeId;
    private String description;
    private Integer warnLevel;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private String createName;
    private String updateName;
}
