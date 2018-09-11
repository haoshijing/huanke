package com.huanke.iot.api.service.device.basic;

import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-11 上午11:44
 */
@Repository
@Slf4j
public class DeviceModelService {
    @Autowired
    private DeviceModelMapper deviceModelMapper;
}
