package com.huanke.iot.base.api;

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


    public ApiResponse(){

    }

    public ApiResponse(T data){
        this.code = 200;
        this.msg = "succ";
        this.data = data;
    }
}
