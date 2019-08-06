package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.req.MaBiaoConfigReq;
import com.huanke.iot.base.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/setting")
@RestController
@Slf4j
public class DeviceMbSettingController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/setMaBiaoConfig")
    public ApiResponse<Boolean> setConfig(@RequestBody MaBiaoConfigReq maBiaoConfigReq){

        if(CollectionUtils.isNotEmpty(maBiaoConfigReq.getDeviceIds())){

            maBiaoConfigReq.getDeviceIds().forEach(
                    deviceId->{
                        stringRedisTemplate.opsForHash().put("mabiaoconfig",deviceId
                                ,maBiaoConfigReq.getPath());
                    }
            );
        }
        return new ApiResponse<>(true);
    }
}
