package com.huanke.iot.api.wechart.js.controller;

import com.huanke.iot.api.wechart.js.util.MessageUtil;
import com.huanke.iot.api.wechart.js.util.SignUtil;
import com.huanke.iot.api.wechart.js.wechat.req.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;


@RequestMapping("/device/callback")
@Controller
@Slf4j
public class DeviceWxController {

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
            //发送方账号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            //公众账号
            String toUserName = requestMap.get("ToUserName");
            //消息类型
            String msgType = requestMap.get("MsgType");

            //默认回复此文本消息
            TextMessage defaultTextMessage = new TextMessage();
            defaultTextMessage.setToUserName(fromUserName);
            defaultTextMessage.setFromUserName(toUserName);
            defaultTextMessage.setCreateTime(System.currentTimeMillis());
            defaultTextMessage.setMsgType(MessageUtil.MESSSAGE_TYPE_TEXT);
            defaultTextMessage.setFuncFlag(0);
            // 由于href属性值必须用双引号引起，这与字符串本身的双引号冲突，所以要转义
            defaultTextMessage.setContent("欢迎访问<a href=\"http://blog.csdn.net/j086924\">jon的博客</a>!");
            // defaultTextMessage.setContent(getMainMenu());
            // 将文本消息对象转换成xml字符串
            respMessage = MessageUtil.textMessageToXml(defaultTextMessage);

            //文本消息
            if(msgType.equals(MessageUtil.MESSSAGE_TYPE_TEXT)) {
                //respContent = "Hi,您发送的是文本消息！";
                //回复文本消息
                TextMessage textMessage = new TextMessage();
                //这里需要注意，否则无法回复消息给用户了
                textMessage.setToUserName(fromUserName);
                textMessage.setFromUserName(toUserName);
                textMessage.setCreateTime(new Date().getTime());
                textMessage.setMsgType(MessageUtil.MESSSAGE_TYPE_TEXT);
                textMessage.setFuncFlag(0);
                respContent = "Hi，你发的消息是：" + requestMap.get("Content");
                textMessage.setContent(respContent);
                respMessage = MessageUtil.textMessageToXml(textMessage);
            } else if(msgType.equals(MessageUtil.MESSSAGE_TYPE_EVENT)){
                //事件类型
                String eventType = requestMap.get("Event");
                //订阅
                if(eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)){
                    respContent = "谢谢关注！";
                }
                //取消订阅
                else if(eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)){
                    //
                    System.out.println("取消订阅");

                }
                else if(eventType.equals(MessageUtil.EVENT_TYPE_CLICK)){
                    //自定义菜单消息处理
                    System.out.println("自定义菜单消息处理");
                }
            }

        } catch (Exception e) {

        }
        return respMessage;
    }

}
