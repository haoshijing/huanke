package com.huanke.iot.manage.vo.response.device.data;

import lombok.Data;

import java.util.Date;

@Data
public class DashJobVo {
    private Integer jobCount;
    private Integer finishJobCount;
    private Date time;
}
