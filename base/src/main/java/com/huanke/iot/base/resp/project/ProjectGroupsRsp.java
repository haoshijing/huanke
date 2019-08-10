package com.huanke.iot.base.resp.project;

import lombok.Data;

import java.util.List;

/**
 * 描述:
 * 工程设备项目信息返回类
 *
 * @author onlymark
 * @create 2018-11-29 上午10:23
 */
@Data
public class ProjectGroupsRsp {
    private Integer id;
    private Integer groupId;
    private String projectNo;
    private String name;
    private String introduction;
    private String location;
    private Long CreateTime;
    private String belongTo;
    private List<LinkGroupDeviceRspPo> deviceList;

}
