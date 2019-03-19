package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

/**
 * 描述:
 * 能源请求数据req
 *
 * @author onlymark
 * @create 2019-03-19 下午1:53
 */
@Data
public class EnergySetDataReq {
    private String energyId;
    private Double data;
}
