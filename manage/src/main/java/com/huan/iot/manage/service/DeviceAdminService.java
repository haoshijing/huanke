package com.huan.iot.manage.service;

import com.huan.iot.manage.response.DeviceVo;
import com.huanke.iot.base.dao.DeviceDao;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author haoshijing
 * @version 2018年04月04日 17:47
 **/
public class DeviceAdminService {

    @Resource
    private DeviceDao deviceDao;
    public List<DeviceVo> selectList(){
        return null;
    }
}
