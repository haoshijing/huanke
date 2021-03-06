package com.huanke.iot.base.resp.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-11-16 上午9:42
 */
@Data
public class JobRspPo {
    private Integer id;
    private String name;
    private Integer type;   //关联类型：0-不关联；1-关联设备；2-关联工程
    private Integer linkProjectId;
    private Integer linkDeviceId;
    private Integer customerId;
    private String linkProjectName;
    private String description;
    private String ruleName;
    private String projectTypeName;
    private String customerName;
    private Integer isRule;
    private Integer ruleId;
    private Integer warnLevel;
    private Integer sourceType;
    private Date finalTime;
    private Integer flowStatus;
    private Date createTime;
    private Date updateTime;
    private String createName;
    private String updateName;
    private Boolean editEnable;


    @Data
    public static class JobCountVo {
        private String date;
        private Integer jobCount;
    }
}
