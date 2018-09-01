package com.huanke.iot.manage.vo.request.device.typeModel;


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
    private Integer productId;
    private Integer formatId;
    private String icon; //缩略图
    private String version;
    private String description;
    private Integer status;
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
        private Integer status;

        private List<DeviceModelAblityOptionRequest> deviceModelAblityOptions;
    }

    @Data
    public static class DeviceModelAblityOptionRequest {

        private Integer id;
        private Integer ablityOptionId;
        private String definedName;
        private Integer status;

    }


}
