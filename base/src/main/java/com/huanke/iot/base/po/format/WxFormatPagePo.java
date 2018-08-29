package com.huanke.iot.base.po.format;

import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/27 13:13
 */
@Data
public class WxFormatPagePo {
    private Integer id;
    private Integer formatId;
    private String name;
    private Integer pageNo;
    private String showImg;
    private Integer status;
    private Long createTime;
    private Long lastUpdateTime;
}
