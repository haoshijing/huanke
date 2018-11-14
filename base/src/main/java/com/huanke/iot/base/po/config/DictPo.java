package com.huanke.iot.base.po.config;

import lombok.Data;

import java.util.Date;

/**
 * @author onlymark
 * @version 2018年09月13日 09:22
 **/

@Data
public class DictPo {
    /**
     * id
     */
    private Integer id;
    private Integer value;
    private String label;
    private String type;
    private String description;
    private Integer sort;
    private String remarks;
    private Date createTime;
    private Date updateTime;
    private Integer createUserId;
    private Integer updateUserId;
    private Integer isDelete;

}