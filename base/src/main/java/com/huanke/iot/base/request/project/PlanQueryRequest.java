package com.huanke.iot.base.request.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 * 字典请求类
 *
 * @author onlymark
 * @create 2018-11-14 上午10:16
 */
@Data
public class PlanQueryRequest {

    private String name;
    private Integer isRule;
    private Integer warnLevel;
    private Integer status;
    private Integer linkProjectId;
    private Date createTime;
    private String projectName;

    private Integer currentPage = 1;
    private Integer limit = 10;

}
