package com.huanke.iot.api.util;

import com.huanke.iot.api.vo.WechatDeviceVo;
import com.huanke.iot.api.wechat.WechartUtil;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class ObtainProductInfoService {

    @Resource
    private WechartUtil wechartUtil;

    public WechatDeviceVo obtainDeviceInfo(String productId){

            return null;
    }
}
