package com.huanke.iot.api.wechat.req;

import lombok.Getter;
import lombok.Setter;

public class TextMessage extends BaseMessage {
    //消息内容
    private String content;
    @Getter
    @Setter
    private Integer funcFlag;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
