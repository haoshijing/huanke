package com.huanke.iot.manage.service.device.operate;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import com.huanke.iot.manage.vo.request.device.operate.DeviceLogQueryRequest;
import com.huanke.iot.manage.vo.response.device.operate.DeviceOperLogVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author haoshijing
 * @version 2018年05月30日 13:25
 **/
@Service
public class DeviceOperLogService {

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;

    @Autowired
    private CustomerUserMapper customerUserMapper;

    @Autowired
    private CustomerMapper customerMapper;

    public ApiResponse<List<DeviceOperLogVo>> queryOperLogList(DeviceLogQueryRequest request) throws Exception{
        DeviceOperLogPo queryPo = new DeviceOperLogPo();
        queryPo.setDeviceId(request.getDeviceId());
        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();
        List<DeviceOperLogPo> deviceOperLogPoList = deviceOperLogMapper.selectList(queryPo,limit,offset);
        List<DeviceOperLogVo> deviceOperLogVoList = deviceOperLogPoList.stream().map(deviceOperLogPo -> {
            DeviceOperLogVo deviceOperLogVo = new DeviceOperLogVo();
            BeanUtils.copyProperties(deviceOperLogPo,deviceOperLogVo);
            deviceOperLogVo.setOperateTime(deviceOperLogPo.getCreateTime());
            //获取操作人名字
            if(null != deviceOperLogPo.getOperUserId()){
                //根据操作来源读取不同的客户名称，1-1-h5,2-安卓,3-管理端，1、2读取用户表，3读取客户表
                if(1 == deviceOperLogPo.getOperType() || 2 == deviceOperLogPo.getOperType()){
                    CustomerUserPo customerUserPo=this.customerUserMapper.selectByUserId(deviceOperLogPo.getOperUserId());
                    deviceOperLogVo.setOperName(customerUserPo.getNickname());
                }else {
                    CustomerPo customerPo = this.customerMapper.selectById(deviceOperLogPo.getId());
                    deviceOperLogVo.setOperName(customerPo.getName());
                }
            }
            return deviceOperLogVo;
        }).collect(Collectors.toList());
        return new ApiResponse<>(RetCode.OK,"设备日志查询成功",deviceOperLogVoList);
    }
}
