package com.huanke.iot.manage.service.device.operate;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.dao.device.stat.DeviceSensorStatMapper;
import com.huanke.iot.base.enums.FuncTypeEnums;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import com.huanke.iot.base.po.device.stat.DeviceSensorStatPo;
import com.huanke.iot.manage.vo.request.device.operate.DeviceDataQueryRequest;
import com.huanke.iot.manage.vo.response.device.BaseListVo;
import com.huanke.iot.manage.vo.response.device.data.DeviceOperLogVo;
import com.huanke.iot.manage.vo.response.device.data.DeviceSensorStatVo;
import com.huanke.iot.manage.vo.response.device.data.DeviceWorkLogVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author haoshijing
 * @version 2018年05月30日 13:25
 **/
@Service
@Slf4j
public class DeviceDataService {

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;

    @Autowired
    private DeviceSensorStatMapper deviceSensorStatMapper;

    @Autowired
    private CustomerUserMapper customerUserMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    public ApiResponse<List<DeviceOperLogVo>> queryOperLog(DeviceDataQueryRequest request) throws Exception{
        DeviceOperLogPo queryPo = new DeviceOperLogPo();
        queryPo.setDeviceId(request.getDeviceId());
        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();
        List<DeviceOperLogPo> deviceOperLogPoList = deviceOperLogMapper.selectList(queryPo,limit,offset);
        if(null == deviceOperLogPoList || 0 == deviceOperLogPoList.size()){
            return new ApiResponse<>(RetCode.OK,"暂无数据");
        }
        List<DeviceOperLogVo> deviceOperLogVoList = deviceOperLogPoList.stream().filter(temp ->{
            FuncTypeEnums funcTypeEnums = FuncTypeEnums.getByCode(temp.getFuncId());
            if(funcTypeEnums!=null) return false;
            else return true;
        }).map(deviceOperLogPo -> {
            DeviceOperLogVo deviceOperLogVo = new DeviceOperLogVo();
            BeanUtils.copyProperties(deviceOperLogPo,deviceOperLogVo);
            deviceOperLogVo.setOperateTime(deviceOperLogPo.getCreateTime());
            //获取操作人名字
            if(null != deviceOperLogPo.getOperUserId()){
                //根据操作来源读取不同的客户名称，1-1-h5,2-安卓,3-管理端，1、2读取用户表，3读取客户表
                if(1 == deviceOperLogPo.getOperType() || 2 == deviceOperLogPo.getOperType()){
                    CustomerUserPo customerUserPo=this.customerUserMapper.selectByUserId(deviceOperLogPo.getOperUserId());
                    if(null != customerUserPo) {
                        deviceOperLogVo.setOperName(customerUserPo.getNickname());
                    }else {
                        deviceOperLogVo.setOperName("未知用户");
                    }
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
                if(null != deviceOperLogPo.getFuncValue()) {
                    Integer valueIndex = Integer.parseInt(deviceOperLogPo.getFuncValue());
                    //设置操作的具体值
                    deviceOperLogVo.setFuncValue(ranges[valueIndex]);
                }else {
                    deviceOperLogVo.setFuncValue("无操作值记录");
                }
            }
            else {
                //设置操作的具体值
                deviceOperLogVo.setFuncValue("暂未定义");
            }
            return deviceOperLogVo;
        }).collect(Collectors.toList());
        return new ApiResponse<>(RetCode.OK,"设备日志查询成功",deviceOperLogVoList);
    }

    public ApiResponse<BaseListVo> queryOperLogList(DeviceDataQueryRequest request) throws Exception{
        BaseListVo baseListVo = new BaseListVo();
        DeviceOperLogPo queryPo = new DeviceOperLogPo();
        queryPo.setDeviceId(request.getDeviceId());
        ApiResponse<List<DeviceOperLogVo>> operLogRtn = queryOperLog(request);
        if (operLogRtn != null && operLogRtn.getCode() != RetCode.OK) {
            return new ApiResponse<>(RetCode.ERROR, operLogRtn.getMsg());
        }
        Integer totalCount = this.deviceOperLogMapper.selectCount(queryPo);
        baseListVo.setDataList(operLogRtn.getData());
        baseListVo.setTotalCount(totalCount);
        return new ApiResponse<>(RetCode.OK,"查询设备操作日志与总数成功",baseListVo);
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
            DevicePo devicePo = this.deviceMapper.selectById(eachPo.getDeviceId());
            if(null != devicePo){
                deviceSensorStatVo.setName(devicePo.getName());
            }
            return deviceSensorStatVo;
        }).collect(Collectors.toList());
        return new ApiResponse<>(RetCode.OK,"查询成功",deviceSensorStatVoList);
    }

    public ApiResponse<BaseListVo> queryDeivceSensorDataList(DeviceDataQueryRequest request)throws Exception{
        BaseListVo baseListVo = new BaseListVo();
        DeviceSensorStatPo queryPo = new DeviceSensorStatPo();
        queryPo.setDeviceId(request.getDeviceId());
        ApiResponse<List<DeviceSensorStatVo>> sensorDataRtn = queryDeivceSensorData(request);
        if (sensorDataRtn != null && sensorDataRtn.getCode() != RetCode.OK) {
            return new ApiResponse<>(RetCode.ERROR, sensorDataRtn.getMsg());
        }
        Integer totalCount = this.deviceSensorStatMapper.selectCount(queryPo);
        baseListVo.setDataList(sensorDataRtn.getData());
        baseListVo.setTotalCount(totalCount);
        return new ApiResponse<>(RetCode.OK,"查询设备操作日志与总数成功",baseListVo);
    }
    //按页查询设备工作日志（开关机时间、上离线时间）
    public ApiResponse<List<DeviceWorkLogVo>> queryDeviceWorkData(DeviceDataQueryRequest request){
        DeviceOperLogPo queryPo = new DeviceOperLogPo();
        queryPo.setDeviceId(request.getDeviceId());
        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();
        List<DeviceWorkLogVo> deviceWorkLogVoList =new ArrayList<>();
        List<DeviceOperLogPo> workDataListList = this.deviceOperLogMapper.selectWorkDataList(queryPo,limit,offset);
        if(null != workDataListList && 0 < workDataListList.size()) {
            workDataListList.stream().forEach(eachPo -> {
                DeviceWorkLogVo deviceWorkLogVo = new DeviceWorkLogVo();
                if(eachPo.getFuncId().equals("410")) {
                    deviceWorkLogVo.setDeviceStatus(eachPo.getFuncValue().equals("0") ? "离线" : "上线");
                }else {
                    if(null != eachPo.getFuncValue()) {
                        deviceWorkLogVo.setDeviceStatus(eachPo.getFuncValue().equals("0") ? "关机" : "开机");
                    }else {
                        deviceWorkLogVo.setDeviceStatus("未知定时操作");
                    }
                }
                deviceWorkLogVo.setCreateTime(eachPo.getCreateTime());
                log.info("当前设备信息：{}", deviceWorkLogVo.getDeviceStatus());
                deviceWorkLogVoList.add(deviceWorkLogVo);
            });
        }
        return new ApiResponse<>(RetCode.OK,"查询工作日志成功",deviceWorkLogVoList);
    }

    public ApiResponse<BaseListVo> queryDeviceWorkDataList(DeviceDataQueryRequest request){
        BaseListVo baseListVo = new BaseListVo();
        DeviceOperLogPo queryPo = new DeviceOperLogPo();
        queryPo.setDeviceId(request.getDeviceId());
        queryPo.setFuncId("210");
        ApiResponse<List<DeviceWorkLogVo>> workLogRtn = queryDeviceWorkData(request);
        if (workLogRtn != null && workLogRtn.getCode() != RetCode.OK) {
            return new ApiResponse<>(RetCode.ERROR, workLogRtn.getMsg());
        }
        Integer powerCount = this.deviceOperLogMapper.selectCount(queryPo);
        queryPo.setFuncId("410");
        Integer onLineOffLineCount = this.deviceOperLogMapper.selectCount(queryPo);
        baseListVo.setDataList(workLogRtn.getData());
        baseListVo.setTotalCount(powerCount+onLineOffLineCount);
        return new ApiResponse<>(RetCode.OK,"查询设备操作日志与总数成功",baseListVo);
    }
}
