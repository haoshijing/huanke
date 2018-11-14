package com.huanke.iot.base.po.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 * 任务规则表
 *
 * @author onlymark
 * @create 2018-11-14 上午9:40
 */
@Data
public class ProjectJobRule {
    private Integer id;
    private Integer manageId;
    private String name;
    private String description;
    private Integer typeId;
    private Integer warnLevel;
    private Integer status;//1-正常；2-删除；3-禁用
    private Date createTime;
    private Date updateTime;
    private Integer createUser;
    private Integer updateUser;
}