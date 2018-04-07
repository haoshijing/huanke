package com.huanke.iot.api.wechart.js.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequestMapping("/device/callback")
@Controller
@Slf4j
public class DeviceWxController {

    @ResponseBody
    @RequestMapping("/bind")
    public String  bindDevice(HttpServletRequest httpServletRequest){
        try {
            String xml = new String(getRequestPostBytes(httpServletRequest));
            log.info("xml = {}", xml);
            return xml;
        }catch (Exception e){
            log.error("",e);
        }
        return "";
    }

    public  byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {

            int readlen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }
}
