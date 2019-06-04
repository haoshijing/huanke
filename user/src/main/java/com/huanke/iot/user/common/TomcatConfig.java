package com.huanke.iot.user.common;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * tomcat配置
 *
 * @author onlymark
 * @create 2019-05-28 下午3:23
 */
@Component
public class TomcatConfig extends TomcatEmbeddedServletContainerFactory
{
    public EmbeddedServletContainer getEmbeddedServletContainer(ServletContextInitializer... initializers)
    {
        //设置端口
        return super.getEmbeddedServletContainer(initializers);
    }

    protected void customizeConnector(Connector connector)
    {
        super.customizeConnector(connector);
        Http11NioProtocol protocol = (Http11NioProtocol)connector.getProtocolHandler();
        //设置最大连接数
        protocol.setMaxConnections(200);
        //设置最大线程数
        protocol.setMaxThreads(300);
        //连接超时时间
        protocol.setConnectionTimeout(20000);
        //连接生存时间设置
        protocol.setKeepAliveTimeout(100000);
    }
}