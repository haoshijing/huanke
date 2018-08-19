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
    private String name;
    private Integer typeId; //类型id
    private Integer customerId;
    private Integer productId;
    private String icon;
    private String version;
    private Integer status;
    private String remark;

    private List<DeviceModelAblityRequest> deviceModelAblityRequests;

    /**
     * 型号的功能
     */
    @Data
    public static class DeviceModelAblityRequest {

        private Integer id;
        private Integer modelId;
        private Integer ablityId;
        private String definedName;
        private Integer status;
        private Long createTime;
        private Long lastUpdateTime;

//        private Integer modelAblityId;
//        private String definedName;
//        private Integer status;
    }
    
}
