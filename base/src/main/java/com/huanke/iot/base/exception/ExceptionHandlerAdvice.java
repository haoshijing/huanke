package com.huanke.iot.base.exception;


import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author haoshijing
 * @version 2018年01月10日 10:16
 **/
@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @ResponseBody
    @ExceptionHandler
    public ApiResponse processException(UnauthorizedException e) {
        log.error("exception msg is:{}", e);
        return new ApiResponse(RetCode.AUTH_ERROR, "权限不足", null);
    }

    @ResponseBody
    @ExceptionHandler
    public ApiResponse processException(UnauthenticatedException e) {
        log.error("exception msg is:{}", e);
        return new ApiResponse(RetCode.NEED_LOGIN_ERROR, "请登录", null);
    }

    @ResponseBody
    @ExceptionHandler
    public ApiResponse processException(AccountException e){
        log.error("exception msg is:{}", e);
        return new ApiResponse(RetCode.PARAM_ERROR, e.getMessage(), null);
    }

    @ResponseBody
    @ExceptionHandler
    public ApiResponse processException(BusinessException e){
        log.error("exception msg is:{}", e);
        return new ApiResponse(RetCode.PARAM_ERROR, e.getMessage(), null);
    }

    @ResponseBody
    @ExceptionHandler
    public ApiResponse processException(Exception e){
        log.error("{123}",e);
        ApiResponse apiResponse = ApiResponse.responseError(e);
        return apiResponse;
    }


    //@ResponseBody
    //@ExceptionHandler
    //public ApiResponse processException(Exception e) {
    //    log.error("exception msg is:{}", e);
    //    ApiResponse apiResponse = ApiResponse.responseError(e);
    //    return apiResponse;
    //}
}

