package com.huanke.iot.base.resp.project;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-11-16 上午9:42
 */
@Data
public class PlanRspPo {
    private Integer id;
    private String name;
    private String description;
    private Integer linkType; // 关联类型：0-不关联；1-关联设备；2-关联工程
    private Integer isRule;
    private Integer ruleId;
    private String ruleName;
    private Integer linkDeviceId;
    private Integer linkProjectId;
    private Integer warnLevel;
    private Integer cycleType;
    private Integer cycleNums;
    private Date warnDate;
    private Integer overTimeDays;
    private String enableUsers;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private String createName;
    private String updateName;
    private List<Integer> enableUserList;
}
