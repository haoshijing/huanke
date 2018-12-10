package com.huanke.iot.manage.vo.response.format;

import com.huanke.iot.base.constant.CommonConstant;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "缩略图", name = "icon")
    private String icon;
    private String previewImg;

    @ApiModelProperty(value = "状态", name = "state", required = true)
    private String typeIds;
    private String customerIds;
    private String version;
    private Integer type;
    private Integer owerType;
    private String description;
    private Integer status = CommonConstant.STATUS_YES;
    private List<WxFormatPageVo> wxFormatPageVos;
    private Integer createUserId;
    private String createUserName;
    private Long createTime;

    @Data
    public static class WxFormatPageVo {
        private Integer id;
        private Integer pageNo;
        private String name;
        private String showImg;
        private Integer status = CommonConstant.STATUS_YES;
        private List<WxFormatItemVo> wxFormatItemVos;

    }

    @Data
    public static class WxFormatItemVo {
        private Integer id;
        //        private Integer formatId;
        private String name;
        private Integer abilityType;
        private Integer showSelect;
        private Integer status = CommonConstant.STATUS_YES;
    }

}
