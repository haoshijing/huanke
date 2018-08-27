package com.huanke.iot.manage.vo.response.format;

import lombok.Data;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/27 22:25
 */
@Data
public class WxFormatVo {
    private Integer id;
    private String name;
    private String htmlUrl;
    private String icon;
    private String previewImg;
    private String typeIds;
    private String customerIds;
    private String version;
    private Integer type;
    private Integer owerType;
    private Integer status;
    private List<WxFormatItemVo> wxFormatItemVos;

    @Data
    public class WxFormatItemVo{
        private Integer id;
        private Integer formatId;
        private String name;
        private Integer status;
    }
}
