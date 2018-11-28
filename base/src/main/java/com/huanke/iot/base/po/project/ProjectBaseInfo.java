package com.huanke.iot.base.po.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 * 工程基础信息
 *
 * @author onlymark
 * @create 2018-11-14 上午9:07
 */
@Data
public class ProjectBaseInfo {
    private Integer id;
    private Integer customerId;
    private Integer projectId;
    private String name;
    private String description;
    private Date buildTime;
    private String buildAddress;
    private String gps;
    private String groupIds;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer createUser;
    private Integer updateUser;
}
