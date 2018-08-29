package com.huanke.iot.manage.vo.response.format;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @ApiModelProperty(value="缩略图",name="icon")
    private String icon;
    private String previewImg;

    @ApiModelProperty(value="状态",name="state",required=true)
    private String typeIds;
    private String customerIds;
    private String version;
    private Integer type;
    private Integer owerType;
    private String description;
    private Integer status;
    private List<WxFormatPageVo> wxFormatPageVos;

    @Data
    public static class WxFormatPageVo {
        private Integer id;
        private Integer pageNo;
        private String name;
        private String showImg;
        private Integer status;
        private List<WxFormatItemVo> wxFormatItemVos;

    }

    @Data
    public static class WxFormatItemVo {
        private Integer id;
        //        private Integer formatId;
        private String name;
        private Integer status;
    }
}
