package com.huanke.iot.manage.controller.device;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.manage.controller.device.response.DeviceGroupVo;
import com.huanke.iot.manage.service.device.DeviceGroupService;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haoshijing
 * @version 2018年05月23日 22:35
 **/
@RestController
@RequestMapping("/manage/group")
public class DeviceGroupController {

    @Autowired
    private DeviceGroupService deviceGroupService;
    public ApiResponse<DeviceGroupVo> selectList(){
        return new ApiResponse(Lists.newArrayList());

    }

    public ApiResponse<Integer> selectCount(){
        return new ApiResponse(0);
    }

}
