package com.huanke.iot.base.request.project;

import lombok.Data;

import java.util.List;

/**
 * 描述:
 * 规则请求类
 *
 * @author onlymark
 * @create 2018-11-14 下午4:40
 */
@Data
public class RuleRequest {
    private Integer id;
    private String name;
    private String description;
    private Integer useType;//1-计划，2-用户反馈，3-设备告警
    private Integer typeId;
    private Integer warnLevel;//1-一级；2-二级；3-三级
    private Integer status;//1-正常；2-删除；3-禁用

    private List<String> monitorValues;//监听的设备指令集
}
