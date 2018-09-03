package com.huanke.iot.manage.vo.request.device.typeModel;


import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

import java.util.List;

/**
 * @author caikun
 * @version 2018年08月16日 23:51
 **/
@Data
public class DeviceModelFormatCreateRequest {

//    private Integer modelId;
//    private Integer formatId;
    List<DeviceModelFormatPageCreateRequest> modelFormatPages;


    @Data
    public static class DeviceModelFormatPageCreateRequest {
        private Integer id;
        private Integer modelId;
        private Integer formatId;
        private Integer pageId;
        private Integer showStatus;
        private String showName;
        private Integer status = CommonConstant.STATUS_YES;
        List<DeviceModelFormatItemCreateRequest> modelFormatItems;

    }

    @Data
    public static class DeviceModelFormatItemCreateRequest {
        private Integer id;
        private Integer modelFormatId;
        private Integer pageId;
        private Integer itemId;
        private Integer ablityId;
        private Integer showStatus;
        private String showName;
        private Integer status = CommonConstant.STATUS_YES;
    }
}