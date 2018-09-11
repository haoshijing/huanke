package com.huanke.iot.api.service.device.basic;

import com.huanke.iot.base.dao.customer.WxConfigMapper;
import com.huanke.iot.base.po.customer.WxConfigPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-11 上午10:00
 */
@Repository
@Slf4j
public class DeviceHighService {
    @Autowired
    private WxConfigMapper wxConfigMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String getHighToken(Integer customerId, String password) throws Exception {
        WxConfigPo wxConfigPo = wxConfigMapper.getByJoinId(customerId, password);
        if(wxConfigPo == null){
            throw new Exception("密码错误");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(token, customerId + "-" + password);
        stringRedisTemplate.expire(token, 10, TimeUnit.HOURS);
        return token;
    }
}
