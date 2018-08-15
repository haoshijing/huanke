package com.huanke.iot.manage.service;

import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.user.AppUserMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.manage.controller.response.DashBoardIndexVo;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author haoshijing
 * @version 2018年05月29日 20:14
 **/

@Service
public class DashBoardIndexService {

    private DefaultEventExecutorGroup defaultEventExecutorGroup = new DefaultEventExecutorGroup(8,
            new DefaultThreadFactory("DashBoardIndexThread"));


    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private AppUserMapper appUserMapper;
    public DashBoardIndexVo obtainData() {
        DashBoardIndexVo dashBoardIndexVo = new DashBoardIndexVo();
        Future <Integer> deviceCountF = defaultEventExecutorGroup.submit(()->{

            Integer deviceTotalCount = deviceMapper.selectCount(new DevicePo());
            return deviceTotalCount;
        }).addListener((future)->{
            Integer count  = (Integer)future.get();
            dashBoardIndexVo.setDeviceCount(count);
        });

        Future <Integer> onlineDeviceF = defaultEventExecutorGroup.submit(()->{
            DevicePo devicePo = new DevicePo();
            devicePo.setOnlineStatus(1);
            Integer deviceTotalCount = deviceMapper.selectCount(devicePo);
            return deviceTotalCount;
        }).addListener((future)->{
            Integer count  = (Integer)future.get();
            dashBoardIndexVo.setOnlineCount(count);
        });
        Future <Integer> userCountF =   defaultEventExecutorGroup.submit(()->{
            return appUserMapper.selectCount(new AppUserPo());
        }).addListener(future -> {
            Integer count  = (Integer)future.get();
            dashBoardIndexVo.setUserCount(count);
        });

        try{
            deviceCountF.get();
            userCountF.get();
            onlineDeviceF.get();
        }catch (InterruptedException e){

        }catch (ExecutionException e){

        }
        return dashBoardIndexVo;
    }


}
