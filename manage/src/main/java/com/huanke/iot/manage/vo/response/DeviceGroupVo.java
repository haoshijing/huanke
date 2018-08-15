package com.huanke.iot.manage.vo.response;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年05月23日 22:37
 **/
@Data
public class DeviceGroupVo {
    private Integer id;
    private String groupName;
    private String icon;
    private Integer shareCount;
    private String masterNickname;
}
