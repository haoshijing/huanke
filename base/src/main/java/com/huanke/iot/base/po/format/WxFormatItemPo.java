package com.huanke.iot.base.po.format;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/27 13:13
 */
@Data
public class WxFormatItemPo {
    private Integer id;
    private String name;
    private Integer status;
    private Long createTime;
    private Long lastUpdateTime;
}
