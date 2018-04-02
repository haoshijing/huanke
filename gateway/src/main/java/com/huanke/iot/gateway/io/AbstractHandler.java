package com.huanke.iot.gateway.io;

/**
 * @author haoshijing
 * @version 2018年04月02日 15:17
 **/
public abstract  class AbstractHandler<D> {
    public abstract void  handlerDealData(String topic, D data);
}
