package com.huanke.iot.manage.service.device.operate;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.dao.device.stat.DeviceSensorStatMapper;
import com.huanke.iot.base.enums.FuncTypeEnums;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import com.huanke.iot.base.po.device.stat.DeviceSensorStatPo;
import com.huanke.iot.manage.vo.request.device.operate.DeviceDataQueryRequest;
import com.huanke.iot.manage.vo.response.device.data.DeviceOperLogVo;
import com.huanke.iot.manage.vo.response.device.data.DeviceSensorStatVo;
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
public class DeviceDataService {

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;

    @Autowired
    private DeviceSensorStatMapper deviceSensorStatMapper;

    @Autowired
    private CustomerUserMapper customerUserMapper;

    @Autowired
    private CustomerMapper customerMapper;

    public ApiResponse<List<DeviceOperLogVo>> queryOperLogList(DeviceDataQueryRequest request) throws Exception{
        DeviceOperLogPo queryPo = new DeviceOperLogPo();
        queryPo.setDeviceId(request.getDeviceId());
        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();
        List<DeviceOperLogPo> deviceOperLogPoList = deviceOperLogMapper.selectList(queryPo,limit,offset);
        if(null == deviceOperLogPoList || 0 == deviceOperLogPoList.size()){
            return new ApiResponse<>(RetCode.OK,"暂无数据");
        }
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
                    CustomerPo customerPo = this.customerMapper.selectById(deviceOperLogPo.getOperUserId());
                    deviceOperLogVo.setOperName(customerPo.getName());
                }
            }
            FuncTypeEnums funcTypeEnums = FuncTypeEnums.getByCode(deviceOperLogVo.getFuncId());
            //设置操作名称名称
            deviceOperLogVo.setFuncName(funcTypeEnums.getMark());
            if(!funcTypeEnums.getRange().equals("")){
                String[] ranges = funcTypeEnums.getRange().split(",");
                Integer valueIndex = Integer.parseInt(deviceOperLogPo.getFuncValue());
                //设置操作的具体值
                deviceOperLogVo.setFuncValue(ranges[valueIndex]);
            }
            else {
                //设置操作的具体值
                deviceOperLogVo.setFuncValue("暂未定义");
            }
            return deviceOperLogVo;
        }).collect(Collectors.toList());
        return new ApiResponse<>(RetCode.OK,"设备日志查询成功",deviceOperLogVoList);
    }
    //按页和开始结束时间查询设备数据
    public ApiResponse<List<DeviceSensorStatVo>> queryDeivceSensorData(DeviceDataQueryRequest deviceDataQueryRequest)throws Exception{
        DeviceSensorStatPo deviceSensorStatPo =new DeviceSensorStatPo();
        deviceSensorStatPo.setDeviceId(deviceDataQueryRequest.getDeviceId());
        Integer offset = (deviceDataQueryRequest.getPage() - 1)*deviceDataQueryRequest.getLimit();
        Integer limit = deviceDataQueryRequest.getLimit();
        List<DeviceSensorStatPo> deviceSensorStatPoList = this.deviceSensorStatMapper.selectList(deviceSensorStatPo,limit,offset);
        if(null == deviceSensorStatPoList || 0 == deviceSensorStatPoList.size()){
            return new ApiResponse<>(RetCode.OK,"暂无数据");
        }
        List<DeviceSensorStatVo> deviceSensorStatVoList = deviceSensorStatPoList.stream().map(eachPo ->{
            DeviceSensorStatVo deviceSensorStatVo = new DeviceSensorStatVo();
            BeanUtils.copyProperties(eachPo,deviceSensorStatVo);
            return deviceSensorStatVo;
        }).collect(Collectors.toList());
        return new ApiResponse<>(RetCode.OK,"查询成功",deviceSensorStatVoList);
    }
}
