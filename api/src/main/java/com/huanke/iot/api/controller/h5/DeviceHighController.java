package com.huanke.iot.api.controller.h5;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.api.controller.h5.req.ChildDeviceRequest;
import com.huanke.iot.api.controller.h5.req.HighTokenRequest;
import com.huanke.iot.api.controller.h5.response.ChildDeviceVo;
import com.huanke.iot.api.controller.h5.response.DeviceTypeVo;
import com.huanke.iot.api.service.device.basic.DeviceHighService;
import com.huanke.iot.api.service.device.basic.DeviceService;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @Autowired
    private DeviceService deviceService;

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
            log.error("获取token异常：msg={}", e.getMessage());
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

    /**
     * 返回客户类型列表
     * @return
     */
    @RequestMapping("typeList/{customerId}")
    public Object typeList(@PathVariable("customerId") Integer customerId){
        log.info("返回客户类型列表：customerId={}", customerId);
        List<DeviceTypeVo> deviceTypeVoList = deviceHighService.getTypeListByCustomerId(customerId);
        return new ApiResponse<>(deviceTypeVoList);
    }

    /**
     * 添加从设备
     * @return
     */
    @RequestMapping("addChildDevice")
    public Object addChildDevice(@RequestBody ChildDeviceRequest request){
        log.info("添加从设备：request={}", JSON.toJSONString(request));
        try {
            deviceHighService.addChildDevice(request);
        } catch (Exception e) {
            log.error( e.getMessage());
            return new ApiResponse<>(RetCode.ERROR, "设备地址已存在");
        }
        return new ApiResponse<>();
    }

    /**
     * 从设备列表
     * @return
     */
    @RequestMapping("childDeviceList/{hostDeviceId}")
    public Object childDeviceList(@PathVariable("hostDeviceId") Integer hostDeviceId){
        log.info("从设备列表：hostDeviceId={}", hostDeviceId);
        List<ChildDeviceVo> childDeviceVoList = deviceHighService.childDeviceList(hostDeviceId);
        return new ApiResponse<>(childDeviceVoList);
    }

    /**
     * 删除从设备
     * @return
     */
    @RequestMapping("delChildDevice/{childDeviceId}")
    public Object delChildDevice(@PathVariable("childDeviceId") Integer childDeviceId){
        log.info("删除从设备：childDeviceId={}", childDeviceId);
        deviceService.deleteById(childDeviceId);
        return new ApiResponse<>();
    }

}