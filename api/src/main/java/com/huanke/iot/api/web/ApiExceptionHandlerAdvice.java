package com.huanke.iot.api.web;



import com.huanke.iot.api.requestcontext.UserRequestContext;
import com.huanke.iot.api.requestcontext.UserRequestContextHolder;
import com.huanke.iot.base.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author haoshijing
 * @version 2018年01月10日 10:16
 **/
@Slf4j
@ControllerAdvice
public class ApiExceptionHandlerAdvice {
    @ResponseBody
    @ExceptionHandler
    public ApiResponse processException(HttpMessageNotReadableException e){
        UserRequestContext requestContext = UserRequestContextHolder.get();
        log.info(requestContext.getRequestInfo());
        log.error("Exception!!{"+e.getClass().getName()+"}:"+e.getCause().getMessage());
        ApiResponse apiResponse = ApiResponse.responseError(e);
        return apiResponse;
    }
}

