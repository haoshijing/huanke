package com.huanke.iot.base.inteceptor;

import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityOptionPo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityOptionPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import lombok.extern.log4j.Log4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 此拦截器的目的： api缓存相关表的监听
 * @author zhaody
 *
 */
@Log4j
@Component
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class DbInterceptor implements Interceptor {
    @Value("${apiHost}")
    private String apiHost;
    @Value("${jobHost}")
    private String jobHost;

    /**
     * 监听的表范围
     */
    public static final List<Class<?>> CLASS_LIST = Stream.of(
            DeviceAbilityPo.class,
            DeviceAbilityOptionPo.class,
            DeviceModelPo.class,
            DeviceModelAbilityPo.class,
            DeviceModelAbilityOptionPo.class,
            CustomerPo.class)
            .collect(Collectors.toList());

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object entity = invocation.getArgs()[1];
        SqlCommandType commandType = mappedStatement.getSqlCommandType();
        Object proceed = invocation.proceed();
        this.doIntercept(entity, commandType);
        return proceed;
    }
    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }
    @Override
    public void setProperties(Properties properties) {
    }
    @SuppressWarnings("unchecked")
    private void doIntercept(Object entity, SqlCommandType commandType) throws Exception {
        if (commandType == SqlCommandType.INSERT || commandType == SqlCommandType.UPDATE) {
            if (entity == null || !CLASS_LIST.contains(entity.getClass())) {
                return;
            } else {
                //刷新API缓存
                refreshCache();
            }
        } else if (commandType == SqlCommandType.DELETE) {
            // delete 比较特殊, 实体类型是HashMap, 需要取出其中的class判断其实体类型
            if (entity.getClass().isAssignableFrom(HashMap.class)) {
                HashMap<String, Object> deletingEntityMap = (HashMap<String, Object>) entity;
                Class<?> entityClass = (Class<?>) deletingEntityMap.get("class");
                if (!CLASS_LIST.contains(entityClass)) {
                    return;
                } else {
                    //刷新API缓存
                    refreshCache();
                }
            }else{
                if (!CLASS_LIST.contains(entity.getClass())) { // 事实表明, 不全是HashMap
                    return;
                } else {
                    //刷新API缓存
                    refreshCache();
                }
            }
        }
    }
    private void refreshCache(){
        try {
            log.info("api缓存刷新");
            String url = String.format("http://"+apiHost + "/api/h5/api/flushCache");
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(url));

            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).
                    setConnectTimeout(3000).build();//设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
            HttpClients.createDefault().execute(httpGet);
        }catch (Exception e ){
            log.info("api缓存刷新调用失败");
        }
        try {
            log.info("job缓存刷新");
            String url2 = String.format("http://"+jobHost + "/job/flushCache/flushCache");
            HttpGet httpGet2 = new HttpGet();
            httpGet2.setURI(new URI(url2));

            RequestConfig requestConfig2 = RequestConfig.custom().setSocketTimeout(3000).
                    setConnectTimeout(3000).build();//设置请求和传输超时时间
            httpGet2.setConfig(requestConfig2);
            HttpClients.createDefault().execute(httpGet2);
        }catch (Exception e ){
            log.info("job缓存刷新调用失败");
        }
    }
}


