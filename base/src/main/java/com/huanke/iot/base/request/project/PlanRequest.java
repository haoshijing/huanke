package com.huanke.iot.base.request.project;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-11-14 下午1:48
 */
@Data
public class PlanRequest {
    private Integer id;
    private String name;
    private String description;
    private Integer isRule;
    private Integer ruleId;
    private Integer linkType;
    private Integer linkDeviceId;
    private Integer linkProjectId;
    private Date nextExecuteTime;
    private Integer cycleType;
    private Integer overTimeDays;
    private List<Integer> enableUserList;
    private Integer status;
}
