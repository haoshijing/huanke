package com.huanke.iot.api.controller.h5.response;

import lombok.Data;


@Data
public class EnergyPageVo {
    private String energyId;
    private String pageId;
    private String funcName;
    private String pageMatchId;
    private Double data;
    private String type;

    public EnergyPageVo(String energyId, String pageId, String funcName, String pageMatchId, String type) {
        this.energyId = energyId;
        this.pageId = pageId;
        this.funcName = funcName;
        this.pageMatchId = pageMatchId;
        this.type = type;
    }
}
