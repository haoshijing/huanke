package com.huanke.iot.base.po.format;

import com.huanke.iot.base.constant.CommonConstant;
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
    private Integer status = CommonConstant.STATUS_YES;
    private Long createTime;
    private Long lastUpdateTime;
}
