package com.huanke.iot.manage.vo.response.ablity;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAblityVo {

    private Integer id;
    private String ablityName;
    private String dirValue;
    private Integer writeStatus; //可读写状态
}
