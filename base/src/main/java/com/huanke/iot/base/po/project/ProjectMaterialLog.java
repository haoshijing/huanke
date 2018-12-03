package com.huanke.iot.base.po.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 * 材料操作记录表
 *
 * @author onlymark
 * @create 2018-11-14 上午9:46
 */
@Data
public class ProjectMaterialLog {
    private Integer id;
    private Integer type;//1-创建；2-追加；3-维保使用
    private Integer jobLogId;
    private Integer materialId;//'材料表id'
    private Integer handerNums;//'操作数量'
    private Integer currentNums;//'操作后数量'
    private Date createTime;
    private Date updateTime;
    private Integer createUser;
    private Integer updateUser;
}