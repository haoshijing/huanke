package com.huanke.iot.manage.vo.request.device.group;

import com.huanke.iot.manage.vo.request.device.operate.DeviceQueryRequest;
import lombok.Data;

import java.util.List;

@Data
public class GroupCreateOrUpdateRequest {

    private Integer id;
    /**
     * 集群名称
     */
    private String name;

    /**
     * B端客户id
     */
    private Integer customerId;

    private String introduction;

    private String remark;

    private String createLocation;

    private List<ImageVideo> imageVideoList;

    private DeviceQueryRequest deviceQueryRequest;

    @Data
    public static class ImageVideo{
        private String imgVideo;
    }

}
