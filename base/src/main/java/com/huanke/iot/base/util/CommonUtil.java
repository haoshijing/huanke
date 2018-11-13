package com.huanke.iot.base.util;

import com.huanke.iot.base.po.customer.CustomerPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @author caikun
 * @date 2018/10/18 下午8:51
 **/
@Service
@Slf4j
public final class CommonUtil {

    @Resource
    private HttpServletRequest httpServletRequest;

    @Value("${serverConfigHost}")
    private String serverConfigHost;

    /**
     * 获取二级域名
     * @return
     */
    public  String obtainSecondHost() {

        String requestHost =  httpServletRequest.getHeader("Host");
        log.info("当前域名是：{}",requestHost);
        String userHost = "";
        if(!StringUtils.isEmpty(requestHost)){
            int userHostIdx =   requestHost.indexOf("."+serverConfigHost);
            if(userHostIdx > -1){
                userHost = requestHost.substring(0, userHostIdx);
            }
        }

        return userHost;
    }

}
