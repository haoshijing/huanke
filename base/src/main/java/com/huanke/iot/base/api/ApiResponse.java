package com.huanke.iot.base.api;

import com.huanke.iot.base.constant.RetCode;
import lombok.Data;

/**
 * @author haoshijing
 * @version 2018年03月28日 16:32
 **/
@Data
public class ApiResponse<T> {
    private Integer code;
    private String msg;
    private T data;

    public static ApiResponse PARAM_ERROR = new ApiResponse(RetCode.CODE_ERROR,"参数错误");

    public ApiResponse() {
        this.code = RetCode.OK;
        this.msg = "";
        this.data = null;
    }

    public ApiResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }

    public ApiResponse(T data) {
        this.code = RetCode.OK;
        this.msg = "";
        this.data = data;
    }

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.msg = message;
        this.data = data;
    }

    public static ApiResponse responseError(Exception e) {
        return new ApiResponse(RetCode.ERROR, "服务器发生错误", null);
    }

}
