package com.huanke.iot.manage.vo.request.device.typeModel;


import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

import java.util.List;

/**
 * @author caikun
 * @version 2018年08月16日 23:51
 **/
@Data
public class DeviceModelCreateOrUpdateRequest {

    private Integer id;
    private String name; //名称
    private String modelNo; //名称
    private Integer typeId; //类型id
    private Integer customerId;
    private String productId;
    private Integer formatId;
    private String icon; //缩略图
    private String version;
    private String description;
    private Integer status = CommonConstant.STATUS_YES;
    private String remark;
//    private List<DeviceModelFormatConfigCreateRequest> deviceModelFormatConfigs;

    private List<DeviceModelAblityRequest> deviceModelAblitys;

    private DeviceModelFormatCreateRequest deviceModelFormat;

    /**
     * 型号的功能
     */
    @Data
    public static class DeviceModelAblityRequest {

        private Integer id;
        private Integer ablityId;
        private String definedName;
        private Integer minVal;
        private Integer maxVal;
        private Integer status = CommonConstant.STATUS_YES;

        private List<DeviceModelAblityOptionRequest> deviceModelAblityOptions;
    }

    @Data
    public static class DeviceModelAblityOptionRequest {

        private Integer id;
        private Integer ablityOptionId;
        private String definedName;
        private Integer minVal;
        private Integer maxVal;
        private Integer status = CommonConstant.STATUS_YES;

    }


}
