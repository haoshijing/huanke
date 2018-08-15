package com.huanke.iot.manage.common.security.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;
import java.util.UUID;

public class MySessionIdGenerator implements SessionIdGenerator {
    @Override
    public Serializable generateId(Session session) {
        String sessionId = UUID.randomUUID().toString().replace("-","");
        return sessionId;
    }
}
