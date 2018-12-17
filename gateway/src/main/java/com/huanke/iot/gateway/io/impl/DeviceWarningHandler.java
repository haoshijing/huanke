package com.huanke.iot.gateway.io.impl;

import com.huanke.iot.gateway.io.AbstractHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class DeviceWarningHandler extends AbstractHandler {
    @Override
    protected String getTopicType() {
        return "warning";
    }

    @Override
    public void doHandler(String topic, byte[] payloads) {

    }
}
