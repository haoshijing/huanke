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
public class JobRequest {
    private Integer id;
    private String type; //关联类型：0-不关联；1-关联设备；2-关联工程
    private Integer linkDeviceId;
    private Integer linkProjectId;
    private String name;
    private String description;
    private Integer isRule;
    private Integer warnLevel;
    private Integer sourceType;//来源类型：1-计划维保；2-H5端反馈；3-设备告警
    private Date finalTime;
    private String imgList;
    private List<Integer> enableUserList;
    private Integer warnStatus;
    private Integer flowStatus;
}
