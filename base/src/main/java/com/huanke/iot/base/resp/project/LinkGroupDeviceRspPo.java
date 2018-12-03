package com.huanke.iot.base.resp.project;

import lombok.Data;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-11-29 上午11:10
 */
@Data
public class LinkGroupDeviceRspPo {
    private String name;
    private String mac;
    private Integer workStatus;
    private String belongTo;
    private Long lastOnlineTime;
}
