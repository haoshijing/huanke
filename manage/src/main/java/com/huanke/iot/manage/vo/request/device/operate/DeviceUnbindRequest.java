package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

import java.util.List;

/**
 * @author caikun
 * @date 2018/9/8 下午7:44
 **/
@Data
public class DeviceUnbindRequest {

    public List<deviceVo> deviceVos;

    @Data
    public static class deviceVo{
        public Integer deviceId;
        public String mac;
    }
}
