package com.huanke.iot.base.po.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 * 材料表
 *
 * @author onlymark
 * @create 2018-11-14 上午9:43
 */
@Data
public class ProjectMaterialInfo {
    private Integer id;
    private Integer typeId;//1-材料类；2-耗材类
    private  String projectNo;
    private  String name;
    private Integer nums;
    private Integer status;//1-正常；2-删除
    private Date createTime;
    private Date updateTime;
    private Integer createUser;
    private Integer updateUser;
}