package com.huanke.iot.base.po.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 * 工程实施记录表
 *
 * @author onlymark
 * @create 2018-11-14 上午9:22
 */
@Data
public class ProjectImplementLog {
    private Integer id;
    private Integer projectId;
    private Integer typeId;
    private String description;
    private Date implTime;
    private String imgList;//图册
    private String fileList;//文件json
    private Date createTime;
    private Date updateTime;
    private Integer createUser;
    private Integer updateUser;
}
