package com.huanke.iot.api.controller.h5.req;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-11 上午9:46
 */
@Data
@NoArgsConstructor
public class HighTokenRequest {
    private Integer customerId;
    private String password;
}
