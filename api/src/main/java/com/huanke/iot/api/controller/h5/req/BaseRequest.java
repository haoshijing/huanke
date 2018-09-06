package com.huanke.iot.api.controller.h5.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 描述:
 * 基本请求类
 *
 * @author onlymark
 * @create 2018-09-06 下午2:28
 */
@Data
public class BaseRequest<T> {
    @NotNull
    private T value;
}
