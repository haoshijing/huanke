package com.huanke.iot.manage.vo.request.device.group;

import lombok.Data;

import java.util.List;

@Data
public class FuncListMessage {
    private String msg_id;
    private String msg_type;
    private List<FuncItemMessage> datas;
    @Data
    public static class FuncItemMessage {
        private String type;
        private String value;
        private String childid;

    }
}
