package com.huanke.iot.manage.vo.response;

import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年05月30日 13:19
 **/
@Data
public class DeviceOperLogVo {
    private Integer id;
    private String operNickname;
    private String operTime;
    private String dealRet;
    private String funcValue;
    private String funcId;
}
