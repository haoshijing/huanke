package com.huanke.iot.manage.service.device;

import com.huanke.iot.manage.request.DeviceQueryRequest;
import com.huanke.iot.manage.response.DeviceTypeVo;
import com.huanke.iot.manage.response.DeviceVo;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DeviceTypeService {
    public List<DeviceTypeVo> selectList(DeviceQueryRequest deviceQueryRequest) {
        return Lists.newArrayList();
    }

    public Integer selectCount(DeviceQueryRequest deviceQueryRequest) {
        return 0;
    }
}
