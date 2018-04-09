package com.huanke.iot.api;

import com.huanke.iot.api.service.device.basic.DeviceBindService;
import com.huanke.iot.api.util.MessageUtil;
import com.huanke.iot.api.util.SignUtil;
import com.huanke.iot.api.wechat.req.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author haoshijing
 * @version 2018年04月09日 13:49
 **/
@Controller
@Slf4j
@RequestMapping("/device/callback")
public class DeviceController {
    @Autowired
    DeviceBindService deviceBindService;
    @ResponseBody
    @RequestMapping("/onEvent")
    public String  checkCode(HttpServletRequest request){
        try {
            String method = request.getMethod();
            if(StringUtils.equalsIgnoreCase(method,"get")) {
                String signature = request.getParameter("signature");
                String timestamp = request.getParameter("timestamp");
                String nonce = request.getParameter("nonce");
                String echostr = request.getParameter("echostr");
                boolean checkOk = SignUtil.checkSignature(signature, timestamp, nonce);
                if (checkOk) {
                    return echostr;
                }
            }else if(StringUtils.equalsIgnoreCase("post",method)){
                return processPostRequest(request);
            }

        }catch (Exception e){
            log.error("",e);
        }
        return "";
    }

    private String processPostRequest(HttpServletRequest request) {
        String respMessage = null;
        //默认返回的文本消息内容
        String respContent = "请求处理异常，请稍后尝试！";
        try {
            //xml请求解析
            Map<String,String> requestMap = MessageUtil.pareXml(request);
            String msgType = requestMap.get("MsgType");
            // reqMap = {DeviceType=gh_7f3ba47c70a3, DeviceID=gh_7f3ba47c70a3_f1c1cd2015ab27b6, Con
           // tent=, CreateTime=1523200569, Event=unbind, ToUserName=gh_7f3ba47c70a3, FromUserName=okOTjwpDwxJR666hVWnj_L_jp87w, MsgType=device_event, SessionID=0, OpenID=okOTjwpDwxJR666hVWnj_L_jp87w}

        if(StringUtils.equals(msgType,"device_event")){
            String event = requestMap.get("Event");
            deviceBindService.handlerDeviceEvent(requestMap,event);
        }
        } catch (Exception e) {

        }
        return respMessage;
    }

}
