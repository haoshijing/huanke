package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.req.HighTokenRequest;
import com.huanke.iot.api.service.device.basic.DeviceHighService;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述:
 * 设备高级设置
 *
 * @author onlymark
 * @create 2018-09-11 上午9:40
 */
@RequestMapping("/h5/high")
@RestController
@Slf4j
public class DeviceHighController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DeviceHighService deviceHighService;

    /**
     * 获取高级设置token
     * @param request
     * @return
     */
    @RequestMapping("getToken")
    public Object getToken(@RequestBody HighTokenRequest request){
        Integer customerId = request.getCustomerId();
        String password = request.getPassword();
        log.info("获取高级设置token：customer={}, password1={}", customerId, password);
        String token;
        try {
            token = deviceHighService.getHighToken(customerId, password);
        } catch (Exception e) {
            log.error("密码错误");
            return new ApiResponse<>(RetCode.ERROR, "密码错误");
        }
        return new ApiResponse<>(token);
    }

    

    /**
     * 删除高级设置token
     * @param token
     * @return
     */
    @RequestMapping("delToken/{token}")
    public Object delToken(@PathVariable("token") String token){
        stringRedisTemplate.delete(token);
        return new ApiResponse<>();
    }
}
