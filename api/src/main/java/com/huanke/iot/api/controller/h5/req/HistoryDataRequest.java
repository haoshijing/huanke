package com.huanke.iot.api.controller.h5.req;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述:
 * 历史信息请求
 *
 * @author onlymark
 * @create 2018-10-12 下午8:05
 */
@Data
@NoArgsConstructor
public class HistoryDataRequest {
    private Integer deviceId;
}
