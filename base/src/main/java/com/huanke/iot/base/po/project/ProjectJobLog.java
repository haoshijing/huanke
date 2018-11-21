package com.huanke.iot.base.po.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 * 任务流程历史表
 *
 * @author onlymark
 * @create 2018-11-14 上午9:37
 */
@Data
public class ProjectJobLog {
    private Integer id;
    private Integer jobId;
    private String description;
    private String imgList;
    private Integer operateType;//1-生成；2-处理；3-提交审核；4-通过；5-驳回；6-忽略
    private Date createTime;
    private Date updateTime;
    private Integer createUser;
    private Integer updateUser;
}
