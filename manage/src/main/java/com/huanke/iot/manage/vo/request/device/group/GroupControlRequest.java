package com.huanke.iot.manage.vo.request.device.group;

import lombok.Data;

import java.util.List;

@Data
public class GroupControlRequest {
    private List<Integer> deviceIdList;
    private String funcId;
    private String value;
}
