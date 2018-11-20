package com.huanke.iot.base.po.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 * 第三方设备
 *
 * @author onlymark
 * @create 2018-11-14 上午9:11
 */
@Data
public class ProjectExtraDevice {
    private Integer id;
    private String deviceNo;
    private Integer projectId;
    private String name;
    private String model;
    private String factory;
    private String direction;//说明书
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer createUser;
    private Integer updateUser;
}