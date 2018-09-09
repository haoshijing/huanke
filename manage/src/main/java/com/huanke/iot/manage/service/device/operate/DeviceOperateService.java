package com.huanke.iot.manage.service.device.operate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.customer.WxConfigMapper;
import com.huanke.iot.base.dao.device.*;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.customer.WxConfigPo;
import com.huanke.iot.base.po.device.DeviceCustomerRelationPo;
import com.huanke.iot.base.po.device.DeviceCustomerUserRelationPo;
import com.huanke.iot.base.po.device.DeviceIdPoolPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.manage.vo.request.device.operate.*;
import com.huanke.iot.manage.service.wechart.WechartUtil;
import com.huanke.iot.manage.vo.response.device.DeviceListVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DeviceOperateService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceGroupItemMapper deviceGroupItemMapper;

    @Autowired
    private DeviceGroupMapper deviceGroupMapper;

    @Autowired
    private DeviceCustomerRelationMapper deviceCustomerRelationMapper;

    @Autowired
    private DeviceCustomerUserRelationMapper deviceCustomerUserRelationMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerUserMapper customerUserMapper;

    @Autowired
    private DeviceIdPoolMapper deviceIdPoolMapper;

    @Autowired
    private DeviceModelMapper deviceModelMapper;

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Autowired
    private DeviceTeamMapper deviceTeamMapper;

    @Autowired
    private DeviceTeamItemMapper deviceTeamItemMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private WechartUtil wechartUtil;

    @Autowired
    private WxConfigMapper wxConfigMapper;

    /**
     * 2018-08-15
     * sixiaojun
     * 支持批量或单个添加
     *
     * @param deviceLists
     * @return
     */
    public Boolean createDevice(List<DeviceCreateOrUpdateRequest.DeviceUpdateList> deviceLists) {
        List<DevicePo> devicePoList = deviceLists.stream().map(device -> {
            DevicePo insertPo = new DevicePo();
            insertPo.setName(device.getName());
            insertPo.setTypeId(device.getTypeId());
            insertPo.setMac(device.getMac());
            //设定绑定状态为未绑定
            insertPo.setBindStatus(DeviceConstant.BIND_STATUS_NO);
            //设定工作状态为空闲
            insertPo.setWorkStatus(DeviceConstant.WORKING_STATUS_NO);
            insertPo.setStatus(CommonConstant.STATUS_YES);
            //设定在线状态为离线
            insertPo.setOnlineStatus(DeviceConstant.ONLINE_STATUS_NO);
            //设定启用状态为禁用
            insertPo.setEnableStatus(DeviceConstant.ENABLE_STATUS_NO);
            insertPo.setHardVersion(device.getHardVersion());
            insertPo.setBirthTime(device.getBirthTime());
            insertPo.setCreateTime(System.currentTimeMillis());
            insertPo.setLastUpdateTime(System.currentTimeMillis());
            return insertPo;
        }).collect(Collectors.toList());
        //批量插入
        Boolean ret = deviceMapper.insertBatch(devicePoList) > 0;
        if (ret) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 2018-08-15
     * sixiaojun
     * 根据前台请求按页查询设备数据
     *
     * @param deviceListQueryRequest
     * @return list
     */
    public List<DeviceListVo> queryDeviceByPage(DeviceListQueryRequest deviceListQueryRequest) {

        Integer offset = (deviceListQueryRequest.getPage() - 1) * deviceListQueryRequest.getLimit();
        Integer limit = deviceListQueryRequest.getLimit();
        //查询所有数据相关数据，要求DevicePo所有值为null，所以新建一个空的DevicePo
        DevicePo queryPo = new DevicePo();
        if(deviceListQueryRequest!=null){
            BeanUtils.copyProperties(deviceListQueryRequest,queryPo);
        }
        queryPo.setStatus(null);
        List<DevicePo> devicePos = deviceMapper.selectList(queryPo, limit, offset);
        List<DeviceListVo> deviceQueryVos = devicePos.stream().map(devicePo -> {
            DeviceCustomerRelationPo deviceCustomerRelationPo;
            DeviceListVo deviceQueryVo = new DeviceListVo();
            deviceQueryVo.setName(devicePo.getName());
            deviceQueryVo.setMac(devicePo.getMac());
            if (null != deviceTypeMapper.selectById(devicePo.getTypeId())) {
                deviceQueryVo.setTypeId(devicePo.getTypeId());
                deviceQueryVo.setDeviceType(deviceTypeMapper.selectById(devicePo.getTypeId()).getName());
            } else {
                deviceQueryVo.setDeviceType("未查询到该类型");
            }
            deviceCustomerRelationPo = deviceCustomerRelationMapper.selectByDeviceId(devicePo.getId());
            if (null != deviceCustomerRelationPo) {
                Integer customerId = deviceCustomerRelationPo.getCustomerId();
                deviceQueryVo.setCustomerId(customerId);
                deviceQueryVo.setCustomerName(customerMapper.selectById(customerId).getName());
                deviceQueryVo.setModelName(deviceModelMapper.selectByCustomerId(customerId).getName());
            }
            deviceQueryVo.setModelId(devicePo.getModelId());
            deviceQueryVo.setBindStatus(devicePo.getBindStatus());
            deviceQueryVo.setEnableStatus(devicePo.getEnableStatus());
            deviceQueryVo.setWorkStatus(devicePo.getWorkStatus());
            deviceQueryVo.setOnlineStatus(devicePo.getOnlineStatus());
            deviceQueryVo.setStatus(devicePo.getStatus());
            if (null != this.deviceGroupItemMapper.selectByDeviceId(devicePo.getId())) {
                deviceQueryVo.setGroupId(this.deviceGroupItemMapper.selectByDeviceId(devicePo.getId()).getGroupId());
                deviceQueryVo.setGroupName(this.deviceGroupMapper.selectByDeviceId(devicePo.getId()).getName());
            } else {
                deviceQueryVo.setGroupId(-1);
                deviceQueryVo.setGroupName("无集群");
            }
            deviceQueryVo.setId(devicePo.getId());
            deviceQueryVo.setCreateTime(devicePo.getCreateTime());
            deviceQueryVo.setLastUpdateTime(devicePo.getLastUpdateTime());
            DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = this.deviceCustomerUserRelationMapper.selectByDeviceId(devicePo.getId());
            if (null != deviceCustomerUserRelationPo) {
                CustomerUserPo customerUserPo = this.customerUserMapper.selectByOpenId(deviceCustomerUserRelationPo.getOpenId());
                deviceQueryVo.setUserOpenId(customerUserPo.getOpenId());
                deviceQueryVo.setUserName(customerUserPo.getNickname());
            }
            deviceQueryVo.setLocation(devicePo.getLocation());
            return deviceQueryVo;
        }).collect(Collectors.toList());
        return deviceQueryVos;
    }

    /**
     * 删除设备及与设备相关的信息
     * 删除设备需 先解除 绑定关系、组关系、群关系、解除客户分配关系（召回）、最后删除
     * 2018-08-21
     * sixiaojun
     *
     * @param deviceLists
     * @return
     */
    public Integer deleteDevice(List<DeviceCreateOrUpdateRequest.DeviceUpdateList> deviceLists) {
        deviceLists.stream().forEach(device -> {

            //先从设备表中删除该mac地址的设备
            DevicePo devicePo = deviceMapper.selectByMac(device.getMac());
            if (deviceMapper.deleteDevice(devicePo) > 0) {
                deviceGroupItemMapper.deleteDeviceById(devicePo.getId());
                //如果当前设备已被分配给客户
                if (null != deviceCustomerRelationMapper.selectByDeviceId(devicePo.getId())) {
                    //从客户关系表中删除记录
                    deviceCustomerRelationMapper.deleteDeviceById(devicePo.getId());
                }
            }
        });
        //返回本次删除的设备总数
        return deviceLists.size();
    }


    /**
     * 删除设备及与设备相关的信息
     * 删除设备需 先解除 绑定关系、组关系、群关系、解除客户分配关系（召回）、最后删除
     * 2018-08-21
     * sixiaojun
     *
     * @param deviceVo
     * @return
     */
    public ApiResponse<Boolean> deleteOneDevice(DeviceUnbindRequest.deviceVo deviceVo) {

        try {
            Integer deviceId = deviceVo.deviceId;
            String mac = deviceVo.mac;
            //解除 用户绑定关系、组关系
            ApiResponse<Boolean> result = untieOneDeviceToUser(deviceVo);
            //解绑成功之后，开始 解除群关系 并 进行解除 客户关系。
            if(RetCode.OK==result.getCode()){
                //删除 该设备的群绑定关系
                this.deviceGroupItemMapper.deleteItemsByDeviceId(deviceId);
                //解除客户分配关系
                deviceCustomerRelationMapper.deleteDeviceById(deviceId);

                //更改 设备信息
                DevicePo queryDevicePo = deviceMapper.selectById(deviceId);
                if(queryDevicePo!=null){

                    // 删除该设备的 微信deviceid
                    queryDevicePo.setWxDeviceId(null);
                    queryDevicePo.setWxDevicelicence(null);
                    queryDevicePo.setWxQrticket(null);

                    //设定绑定状态为未绑定
                    queryDevicePo.setBindStatus(DeviceConstant.BIND_STATUS_NO);
                    //设定工作状态为空闲
                    queryDevicePo.setWorkStatus(DeviceConstant.WORKING_STATUS_NO);
                    //设定在线状态为离线
                    queryDevicePo.setOnlineStatus(DeviceConstant.ONLINE_STATUS_NO);
                    //设定启用状态为禁用
                    queryDevicePo.setEnableStatus(DeviceConstant.ENABLE_STATUS_NO);

                    queryDevicePo.setStatus(CommonConstant.STATUS_DEL);

                    queryDevicePo.setLastUpdateTime(System.currentTimeMillis());

                    deviceMapper.updateById(queryDevicePo);
                }

            }else{
                return result;
            }
            return new ApiResponse<>(RetCode.OK, "删除成功");
        } catch (Exception e) {
            log.error("设备删除失败-{}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备删除失败");
        }
    }

    /**
     * 设备恢复（单个）
     * @param deviceVo
     * @return
     */
    public ApiResponse<Boolean> recoverDevice(DeviceUnbindRequest.deviceVo deviceVo) {
        try {

            Integer deviceId = deviceVo.deviceId;
            String mac = deviceVo.mac;
            if(null==deviceId||deviceId<=0||StringUtils.isBlank(mac)){
                return new ApiResponse<>(RetCode.PARAM_ERROR, "参数不可为空");
            }

            //查询该设备详情 进行验证
            DevicePo queryDevicePo = deviceMapper.selectById(deviceId);

            if(null==queryDevicePo){
                return new ApiResponse<>(RetCode.PARAM_ERROR, "当前设备不存在");
            }else if(!mac.equals(queryDevicePo.getMac())){
                return new ApiResponse<>(RetCode.PARAM_ERROR, "设备主键与mac地址不匹配");
            }

            //设定绑定状态为未绑定
            queryDevicePo.setBindStatus(DeviceConstant.BIND_STATUS_NO);
            //设定工作状态为空闲
            queryDevicePo.setWorkStatus(DeviceConstant.WORKING_STATUS_NO);
            queryDevicePo.setStatus(CommonConstant.STATUS_YES);
            //设定在线状态为离线
            queryDevicePo.setOnlineStatus(DeviceConstant.ONLINE_STATUS_NO);
            //设定启用状态为禁用
            queryDevicePo.setEnableStatus(DeviceConstant.ENABLE_STATUS_NO);
            queryDevicePo.setCreateTime(System.currentTimeMillis());
            queryDevicePo.setLastUpdateTime(System.currentTimeMillis());

            deviceMapper.updateById(queryDevicePo);
            return new ApiResponse<>(RetCode.OK, "设备恢复成功");

        }catch (Exception e){
            log.error("设备恢复失败-{}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备恢复失败");
        }
    }
    /**
     * 将设备列表中的设备分配给设备型号，并与当前客户关联
     * 2018-08-21
     * sixiaojun
     *
     * @param deviceAssignToCustomerRequest
     * @return
     */
    public ApiResponse<Boolean> assignDeviceToCustomer(DeviceAssignToCustomerRequest deviceAssignToCustomerRequest) {
        Boolean ret = true;
        //获取设备列表
        List<DeviceQueryRequest.DeviceQueryList> deviceList = deviceAssignToCustomerRequest.getDeviceQueryRequest().getDeviceList();
        if (deviceList != null && deviceList.size() > 0) {
            //首先查询device_pool表中 该客户的该型号下 是否存在足够数量的device_id和device_license
            DeviceIdPoolPo deviceIdPoolPo = new DeviceIdPoolPo();

            deviceIdPoolPo.setCustomerId(deviceAssignToCustomerRequest.getCustomerId());
            deviceIdPoolPo.setProductId(deviceAssignToCustomerRequest.getProductId());
            deviceIdPoolPo.setStatus(DeviceConstant.WXDEVICEID_STATUS_NO);
            Integer devicePoolCount = deviceIdPoolMapper.selectCount(deviceIdPoolPo);
            //若当前设备池中的数量不够，则向微信公众号请求所需要的新的设备证书
            if (deviceList.size() > devicePoolCount) {
                Integer addCount = deviceList.size() - devicePoolCount;
                //获取数据
                ApiResponse<Boolean> result = createWxDeviceIdPools(deviceAssignToCustomerRequest.getCustomerId(), deviceAssignToCustomerRequest.getProductId(), addCount);
                if (result == null || RetCode.PARAM_ERROR == result.getCode()) {
                    return new ApiResponse<>(RetCode.PARAM_ERROR, result.getMsg(), false);
                }
            }

            //当pool中的证书数量充足时进行分配
            Integer offset = 0;
            List<DeviceCustomerRelationPo> deviceCustomerRelationPoList = new ArrayList<>();
            List<DevicePo> devicePoList = new ArrayList<>();
            List<DeviceIdPoolPo> deviceIdPoolPoList = new ArrayList<>();
            for (DeviceQueryRequest.DeviceQueryList device : deviceList) {
                DeviceCustomerRelationPo deviceCustomerRelationPo = new DeviceCustomerRelationPo();
                deviceCustomerRelationPo.setCustomerId(deviceAssignToCustomerRequest.getCustomerId());
                deviceCustomerRelationPo.setDeviceId(deviceMapper.selectByMac(device.getMac()).getId());
                deviceCustomerRelationPo.setCreateTime(System.currentTimeMillis());
                deviceCustomerRelationPo.setLastUpdateTime(System.currentTimeMillis());
                //记录本次添加所有客户与添加设备的关系
                deviceCustomerRelationPoList.add(deviceCustomerRelationPo);
                //从pool中获取设备id和证书
                DeviceIdPoolPo queryPoolPo = new DeviceIdPoolPo();
                queryPoolPo.setStatus(DeviceConstant.WXDEVICEID_STATUS_NO);
                //每次查找当前池中未使用第一条license
                List<DeviceIdPoolPo> poolPoList = deviceIdPoolMapper.selectList(queryPoolPo, 1, offset);
                if(poolPoList!=null&&poolPoList.size()>0) {
                    DeviceIdPoolPo resultPo = poolPoList.get(0);

                    //记录本次使用的pool状态为已占用
                    resultPo.setStatus(DeviceConstant.WXDEVICEID_STATUS_YES);
                    deviceIdPoolPoList.add(resultPo);
                    offset++;
                    //在设备表中更新deviceModelId字段，将设备与设备型号表关联
                    DevicePo devicePo = new DevicePo();
                    devicePo.setId(deviceMapper.selectByMac(device.getMac()).getId());
                    devicePo.setModelId(deviceAssignToCustomerRequest.getModelId());
                    devicePo.setProductId(deviceAssignToCustomerRequest.getProductId());
                    devicePo.setWxDeviceId(resultPo.getWxDeviceId());
                    devicePo.setWxDevicelicence(resultPo.getWxDeviceLicence());
                    devicePo.setWxQrticket(resultPo.getWxQrticket());
                    //刷新最新更新时间
                    devicePo.setLastUpdateTime(System.currentTimeMillis());
                    //记录本次需要更新的设备
                    devicePoList.add(devicePo);
                }
            }
            //device_customer_relation表中进行批量插入
            this.deviceCustomerRelationMapper.insertBatch(deviceCustomerRelationPoList);
            //批量更新设备表
            this.deviceMapper.updateBatch(devicePoList);
            //关系表和设备处理完成后批量更新本次使用的pool
            this.deviceIdPoolMapper.updateBatch(deviceIdPoolPoList);
            return new ApiResponse<>(ret);
        } else {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "分配的设备不可为空");
        }
    }

    /**
     * 将设备列表中设备召回，并取消相关关联
     * 2018-08-21
     * sixiaojun
     *
     * @param deviceQueryRequests
     * @return
     */
    public Boolean callBackDeviceFromCustomer(List<DeviceQueryRequest.DeviceQueryList> deviceQueryRequests) {
        List<DeviceCustomerRelationPo> deviceCustomerRelationPoList = new ArrayList<>();
        List<DevicePo> devicePoList = new ArrayList<>();
        List<DeviceIdPoolPo> deviceIdPoolPoList = new ArrayList<>();
        deviceQueryRequests.stream().forEach(device -> {
                    DevicePo devicePo = this.deviceMapper.selectByMac(device.getMac());
                    DeviceCustomerRelationPo deviceCustomerRelationPo = this.deviceCustomerRelationMapper.selectByDeviceMac(device.getMac());
                    //记录需要删除的客户设备关系
                    deviceCustomerRelationPoList.add(deviceCustomerRelationPo);
                    //记录要更新的设备池信息
                    DeviceIdPoolPo deviceIdPoolPo = this.deviceIdPoolMapper.selectByWxDeviceId(devicePo.getWxDeviceId());
                    deviceIdPoolPo.setStatus(DeviceConstant.WXDEVICEID_STATUS_NO);
                    deviceIdPoolPo.setLastUpdateTime(System.currentTimeMillis());
                    deviceIdPoolPoList.add(deviceIdPoolPo);
                    //记录要更新的设备信息
                    devicePo.setModelId(null);
                    devicePo.setWxDeviceId(null);
                    devicePo.setWxDevicelicence(null);
                    devicePo.setWxQrticket(null);
                    devicePo.setProductId(null);
                    devicePo.setLastUpdateTime(System.currentTimeMillis());
                    devicePoList.add(devicePo);
                }
        );
        this.deviceMapper.updateBatch(devicePoList);
        this.deviceIdPoolMapper.updateBatch(deviceIdPoolPoList);
        Boolean ret = this.deviceCustomerRelationMapper.deleteBatch(deviceCustomerRelationPoList) > 0;
        if (ret) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 绑定设备
     *
     * @param deviceBindToUserRequest
     * @return
     */
    public Boolean bindDeviceToUser(DeviceBindToUserRequest deviceBindToUserRequest) {
        List<DeviceTeamItemPo> deviceTeamItemPoList = new ArrayList<>();
        List<DevicePo> devicePoList = new ArrayList<>();
        List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPoList = new ArrayList<>();
        DeviceTeamPo deviceTeamPo = this.deviceTeamMapper.selectById(deviceBindToUserRequest.getTeamId());
        CustomerUserPo customerUserPo = this.customerUserMapper.selectByOpenId(deviceBindToUserRequest.getOpenId());
        List<DeviceQueryRequest.DeviceQueryList> bindDeviceList = deviceBindToUserRequest.getDeviceQueryRequest().getDeviceList();
        bindDeviceList.stream().forEach(bindDevice -> {
            DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = new DeviceCustomerUserRelationPo();
            DeviceTeamItemPo deviceTeamItemPo = new DeviceTeamItemPo();
            DevicePo devicePo = this.deviceMapper.selectByMac(bindDevice.getMac());
            //该设备被添加进入组的同时也被绑定给了当前的终端用户，因此设定此处的绑定状态为已绑定
            devicePo.setBindStatus(DeviceConstant.BIND_STATUS_YES);
            //设定绑定时间
            devicePo.setBindTime(System.currentTimeMillis());
            devicePo.setLastUpdateTime(System.currentTimeMillis());
            deviceTeamItemPo.setDeviceId(devicePo.getId());
            deviceTeamItemPo.setTeamId(deviceTeamPo.getId());
            deviceTeamItemPo.setUserId(customerUserPo.getId());
            deviceTeamItemPo.setStatus(CommonConstant.STATUS_YES);
            deviceTeamItemPo.setCreateTime(System.currentTimeMillis());
            deviceTeamItemPo.setLastUpdateTime(System.currentTimeMillis());
            deviceCustomerUserRelationPo.setDeviceId(devicePo.getId());
            deviceCustomerUserRelationPo.setCustomerId(customerUserPo.getId());
            deviceCustomerUserRelationPo.setOpenId(deviceBindToUserRequest.getOpenId());
            deviceCustomerUserRelationPo.setStatus(CommonConstant.STATUS_YES);
            deviceCustomerUserRelationPo.setCreateTime(System.currentTimeMillis());
            deviceCustomerUserRelationPo.setLastUpdateTime(System.currentTimeMillis());
            devicePoList.add(devicePo);
            deviceTeamItemPoList.add(deviceTeamItemPo);
            deviceCustomerUserRelationPoList.add(deviceCustomerUserRelationPo);
        });
        //进行设备名称的批量更新
        this.deviceMapper.updateBatch(devicePoList);
        //进行设备、客户、用户关系的绑定
        this.deviceCustomerUserRelationMapper.insertBatch(deviceCustomerUserRelationPoList);
        //进行设备的批量绑定
        Boolean ret = this.deviceTeamItemMapper.insertBatch(deviceTeamItemPoList) > 0;
        if (ret) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设备解绑(单个)
     *
     * @return
     */
    public ApiResponse<Boolean> untieOneDeviceToUser(DeviceUnbindRequest.deviceVo deviceVo) {//设备解绑 todo

        try {
            Integer deviceId = deviceVo.deviceId;
            String mac = deviceVo.mac;
            if(null==deviceId||deviceId<=0||StringUtils.isBlank(mac)){
                return new ApiResponse<>(RetCode.PARAM_ERROR, "参数不可为空");
            }

            //查询该设备详情 进行验证
            DevicePo queryDevicePo = deviceMapper.selectById(deviceId);

            if(null==queryDevicePo){
                return new ApiResponse<>(RetCode.PARAM_ERROR, "当前设备不存在");
            }else if(!mac.equals(queryDevicePo.getMac())){
                return new ApiResponse<>(RetCode.PARAM_ERROR, "设备主键与mac地址不匹配");
            }
            //删除 该设备的用户绑定关系
            deviceCustomerUserRelationMapper.deleteRealationByDeviceId(deviceId);

            //删除 该设备的 组关系
            deviceTeamItemMapper.deleteItemsByDeviceId(deviceId);

            //更新 设备的绑定状态 为 未绑定
            DevicePo unbindDevicePo = new DevicePo();
            unbindDevicePo.setId(deviceId);
            unbindDevicePo.setBindStatus(DeviceConstant.BIND_STATUS_NO);
            unbindDevicePo.setBindTime(null);
            unbindDevicePo.setLastUpdateTime(System.currentTimeMillis());

            deviceMapper.updateById(unbindDevicePo);

            return new ApiResponse<>(RetCode.OK, "解绑成功");
        } catch (Exception e) {
            log.error("设备解绑失败-{}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备解绑失败");
        }

    }

    /**
     * 设备解绑(批量)
     *
     * @return
     */
    public ApiResponse<Boolean> untieDeviceToUser(DeviceUnbindRequest unbindRequest) {//设备解绑 todo

        try {
            List<DeviceUnbindRequest.deviceVo> deviceVos = unbindRequest.deviceVos;
            if (deviceVos != null && deviceVos.size() > 0) {
                deviceVos.stream().forEach(deviceVo -> {
                    Integer deviceId = deviceVo.deviceId;
                    String mac = deviceVo.mac;
                    DevicePo queryDevicePo = deviceMapper.selectById(deviceId);
                    //查询已被该设备绑定的用户 ，并进行解绑

                    deviceCustomerUserRelationMapper.deleteRealationByDeviceId(deviceId);
//                    List<DeviceCustomerUserRelationPo> customerUserRelationPos = deviceCustomerUserRelationMapper.selectByDeviceId(deviceId);
//                    if (customerUserRelationPos != null && customerUserRelationPos.size() > 0) {
//                        customerUserRelationPos.stream().forEach(deviceCustomerUserRelationPo -> {
//                            deviceCustomerUserRelationMapper.deleteRelationByJoinId(deviceCustomerUserRelationPo.getOpenId(), deviceId);
//                        });
//                    }

                    //查询 设备组中包含该 设备的 进行解绑

                    deviceTeamItemMapper.deleteItemsByDeviceId(deviceId);
//                    List<DeviceTeamItemPo> deviceTeamItemPos = deviceTeamItemMapper.selectItemsByDeviceId(deviceId);
//                    if(deviceTeamItemPos!=null&&deviceTeamItemPos.size()>0){
//                        deviceTeamItemPos.stream().forEach(deviceTeamItemPo -> {
//                            deviceTeamItemMapper.deleteById(deviceTeamItemPo.getId());
//                        });
//                    }

//
                    //更新 设备的绑定状态 为 未绑定
                    DevicePo unbindDevicePo = new DevicePo();
                    unbindDevicePo.setId(deviceId);
                    unbindDevicePo.setBindStatus(DeviceConstant.BIND_STATUS_NO);
                    unbindDevicePo.setBindTime(null);
                    unbindDevicePo.setLastUpdateTime(System.currentTimeMillis());

                    deviceMapper.updateById(unbindDevicePo);

                });
            }
//            deviceMapper.updateBindStatus(deviceVos);

            return new ApiResponse<>(RetCode.OK, "解绑成功");
        } catch (Exception e) {
            log.error("设备解绑失败-{}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备解绑失败");
        }

    }

    /**
     * 设备禁用
     *
     * @return
     */
    public ApiResponse<Boolean> updateDeivceDisble(DeviceUnbindRequest unbindRequest) {//设备禁用 todo

        try {
            List<DeviceUnbindRequest.deviceVo> deviceVos = unbindRequest.deviceVos;
            if (deviceVos != null && deviceVos.size() > 0) {
                deviceVos.stream().forEach(deviceVo -> {
                    Integer deviceId = deviceVo.deviceId;
                    String mac = deviceVo.mac;
//                    DevicePo queryDevicePo = deviceMapper.selectById(deviceId);

                    //更新 设备的启用状态 为 禁用
                    DevicePo updateDevicePo = new DevicePo();
                    updateDevicePo.setId(deviceId);
                    updateDevicePo.setEnableStatus(DeviceConstant.ENABLE_STATUS_NO);
                    updateDevicePo.setLastUpdateTime(System.currentTimeMillis());

                    deviceMapper.updateById(updateDevicePo);

                });
            }
            return new ApiResponse<>(RetCode.OK, "禁用成功");
        } catch (Exception e) {
            log.error("设备禁用失败-{}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备禁用失败");
        }

    }

    /**
     * 设备启用
     *
     * @return
     */
    public ApiResponse<Boolean> updateDeivceEnable(DeviceUnbindRequest unbindRequest) {//设备启用 todo

        try {
            List<DeviceUnbindRequest.deviceVo> deviceVos = unbindRequest.deviceVos;
            if (deviceVos != null && deviceVos.size() > 0) {
                deviceVos.stream().forEach(deviceVo -> {
                    Integer deviceId = deviceVo.deviceId;
                    String mac = deviceVo.mac;
//                    DevicePo queryDevicePo = deviceMapper.selectById(deviceId);

                    //更新 设备的启用状态 启用
                    DevicePo updateDevicePo = new DevicePo();
                    updateDevicePo.setId(deviceId);
                    updateDevicePo.setEnableStatus(DeviceConstant.ENABLE_STATUS_YES);
                    updateDevicePo.setLastUpdateTime(System.currentTimeMillis());

                    deviceMapper.updateById(updateDevicePo);

                });
            }
            return new ApiResponse<>(RetCode.OK, "启用成功");
        } catch (Exception e) {
            log.error("设备启用失败-{}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备启用失败");
        }

    }

    public List<CustomerUserPo> queryUser(Integer customerId) {
        List<CustomerUserPo> customerUserPoList = this.customerUserMapper.selectByCustomerId(customerId);
        return customerUserPoList;
    }

    /**
     * 2018-08-18
     * sixiaojun
     * 获取设备总数
     *
     * @param
     * @return
     */
    public Integer selectCount() {
        DevicePo queryDevicePo = new DevicePo();
        return deviceMapper.selectCount(queryDevicePo);
    }

    /**
     * 2018-08-20
     * sixiaojun
     * 根据mac地址查询设备表中是否存在相同mac地址的设备，如存在，返回DevicePo，新增失败
     *
     * @param deviceList
     * @return devicePo
     */
    public DevicePo queryDeviceByMac(List<DeviceCreateOrUpdateRequest.DeviceUpdateList> deviceList) {
        DevicePo devicePo = null;
        for (DeviceCreateOrUpdateRequest.DeviceUpdateList device : deviceList) {
            devicePo = deviceMapper.selectByMac(device.getMac());
            if (null != devicePo) {
                return devicePo;
            }
        }
        return devicePo;
    }

    public DevicePo queryDeviceByName(List<DeviceCreateOrUpdateRequest.DeviceUpdateList> deviceList) {
        DevicePo devicePo = null;
        for (DeviceCreateOrUpdateRequest.DeviceUpdateList device : deviceList) {
            devicePo = deviceMapper.selectByMac(device.getName());
            if (null != devicePo) {
                return devicePo;
            }
        }
        return devicePo;
    }

    /**
     * 2018-08-20
     * sixiaojun
     * 根据设备列表中的设备mac查询某个设备是否已被分配
     *
     * @param deviceList
     * @return
     */
    public DevicePo isDeviceHasCustomer(List<DeviceQueryRequest.DeviceQueryList> deviceList) {
        for (DeviceQueryRequest.DeviceQueryList device : deviceList) {
            DevicePo devicePo = deviceMapper.selectDeviceCustomerRelationByMac(device.getMac());
            //如果当前设备已被分配则返回错误
            if (null != devicePo) {
                return devicePo;
            }

        }
        return null;
    }

    public List<DeviceTeamPo> queryTeamInfoByUser(String openId) {
        //首先查询该用户是否有自定义组
        List<DeviceTeamPo> deviceTeamPoList = this.deviceTeamMapper.selectByUserOpenId(openId);
        DeviceTeamPo deviceTeamPo = new DeviceTeamPo();
        CustomerUserPo customerUserPo = this.customerUserMapper.selectByOpenId(openId);
        if (0 == deviceTeamPoList.size()) {
            deviceTeamPoList.clear();
            //若没有自定义组则加载默认组
            WxConfigPo wxConfigPo = this.wxConfigMapper.selectConfigByCustomerId(customerUserPo.getCustomerId());
            deviceTeamPo.setName(wxConfigPo.getDefaultTeamName());
            deviceTeamPo.setId(DeviceConstant.DEFAULT_TEAM_ID);
            deviceTeamPoList.add(deviceTeamPo);
        }
        return deviceTeamPoList;
    }

    public CustomerUserPo isUserExist(String openId) {
        return this.customerUserMapper.selectByOpenId(openId);
    }

    /**
     * 往deviceIdPool 增加多个个配额
     *
     * @param customerId
     * @param productId
     * @param addCount
     * @return
     */
    public ApiResponse<Boolean> createWxDeviceIdPools(Integer customerId, String productId, Integer addCount) {
        Boolean ret = true;
        CustomerPo customerPo = customerMapper.selectById(customerId);
        //获取数据
        if (customerPo != null) {
            String appId = customerPo.getAppid();
            String appSecret = customerPo.getAppsecret();
            int correctCount = 0;
            if (null != addCount && addCount > 0) {
                List<DeviceIdPoolPo> deviceIdPoolPos = new ArrayList<>();

                for (int m = 0; m < addCount; m++) {
                    ApiResponse<JSONObject> result = obtainDeviceInfo(appId, appSecret, customerId.toString(), productId);
                    //当第一个就开始 出现错误时，则直接返回结果
                    if (m == 0 && RetCode.PARAM_ERROR == result.getCode()) {
                        return new ApiResponse<>(RetCode.PARAM_ERROR, result.getMsg());
                    }
                    JSONObject jsonObject = result.getData();
                    if (jsonObject != null) {
                        String wxDeviceId = jsonObject.getString("deviceid");
                        String wxDevicelicence = jsonObject.getString("devicelicence");
                        String wxQrticket = jsonObject.getString("qrticket");

                        DeviceIdPoolPo insertPo = new DeviceIdPoolPo();

                        insertPo.setCustomerId(customerId);
                        insertPo.setProductId(productId);
                        insertPo.setWxDeviceId(wxDeviceId);
                        insertPo.setWxDeviceLicence(wxDevicelicence);
                        insertPo.setWxQrticket(wxQrticket);
                        insertPo.setStatus(DeviceConstant.WXDEVICEID_STATUS_NO);
                        insertPo.setCreateTime(System.currentTimeMillis());
                        insertPo.setLastUpdateTime(System.currentTimeMillis());

                        deviceIdPoolPos.add(insertPo);
                    } else {
                        log.info("createWxDeviceIdPool.jsonObject = {}", false);
                    }
                }

                if (deviceIdPoolPos != null && deviceIdPoolPos.size() > 0) {
                    ret = deviceIdPoolMapper.insertBatch(deviceIdPoolPos) > 0;
                } else {
                    return new ApiResponse<>(RetCode.ERROR, "获取微信DeviceId失败");
                }

            } else {
                return new ApiResponse<>(RetCode.ERROR, "客户不存在");
            }
        }


        return new ApiResponse<>(ret);
    }

    /**
     * 往deviceIdPool 增加一个配额
     *
     * @param customerId
     * @param productId
     * @return
     */
    public Boolean createWxDeviceIdPool(Integer customerId, String productId) {
        CustomerPo customerPo = customerMapper.selectById(customerId);
        //获取数据
        String appId = customerPo.getAppid();
        String appSecret = customerPo.getAppsecret();

        JSONObject jsonObject = obtainDeviceJson(appId, appSecret, customerId.toString(), productId).getData();
        if (jsonObject != null) {
            String wxDeviceId = jsonObject.getString("deviceid");
            String wxDevicelicence = jsonObject.getString("devicelicence");
            String wxQrticket = jsonObject.getString("qrticket");
            DeviceIdPoolPo insertPo = new DeviceIdPoolPo();

            insertPo.setCustomerId(customerId);
            insertPo.setProductId(productId);
            insertPo.setWxDeviceId(wxDeviceId);
            insertPo.setWxDeviceLicence(wxDevicelicence);
            insertPo.setWxQrticket(wxQrticket);
            insertPo.setStatus(DeviceConstant.WXDEVICEID_STATUS_NO);
            insertPo.setCreateTime(System.currentTimeMillis());
            insertPo.setLastUpdateTime(System.currentTimeMillis());

            int insertRet = deviceIdPoolMapper.insert(insertPo);
            log.info("createWxDeviceIdPool.insertRet = {}", insertRet);
            return true;
        } else {
            log.info("createWxDeviceIdPool.jsonObject = {}", false);
            return false;
        }

    }

    private ApiResponse<JSONObject> obtainDeviceJson(String appId, String appSecret, String customerId, String productId) {
        ApiResponse<JSONObject> result = obtainDeviceInfo(appId, appSecret, customerId, productId);
        if (RetCode.PARAM_ERROR == result.getCode()) {
            return result;
        }
        JSONObject deviceInfo = result.getData();
        if (deviceInfo == null) {
            wechartUtil.getAccessToken(appId, appSecret, customerId, true);
            deviceInfo = obtainDeviceInfo(appId, appSecret, customerId, productId).getData();
        }
        if (deviceInfo != null) {
            return new ApiResponse<>(deviceInfo);
        }
        return new ApiResponse<>();
    }

    private ApiResponse<JSONObject> obtainDeviceInfo(String appId, String appSecret, String customerId, String productId) {
        String accessToken = wechartUtil.getAccessToken(appId, appSecret, customerId, false);
        if (StringUtils.isBlank(accessToken)) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该appId与appSecret无法获取accessToken");
        }
        String url = new StringBuilder("https://api.weixin.qq.com/device/getqrcode?access_token=").append(accessToken).append("&product_id=").append(productId).toString();
        HttpGet httpGet = new HttpGet();
        try {
            httpGet.setURI(new URI(url));
            CloseableHttpResponse response = HttpClients.createDefault().execute(httpGet);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            log.info("result = {}", result);
            JSONObject jsonObject = JSON.parseObject(result.toString());
            //获取微信 设备id 失败
            if (jsonObject != null) {

                if (jsonObject.containsKey("errcode") && CommonConstant.ZERO != jsonObject.get("errcode")) {
                    return new ApiResponse<>(RetCode.PARAM_ERROR, result.toString(), jsonObject);
                }
                JSONObject resultObject = jsonObject.getJSONObject("base_resp");
                if (resultObject != null && resultObject.containsKey("errcode")) {
                    Integer retCode = resultObject.getInteger("errcode");
                    if (retCode != null && retCode.equals(0)) {
                        return new ApiResponse<>(RetCode.OK, "", jsonObject);
                    } else {
                        return new ApiResponse<>(RetCode.PARAM_ERROR, result.toString(), jsonObject);
                    }
                }
            }

        } catch (Exception e) {
            log.error("", e);
        }
        return new ApiResponse<>(RetCode.PARAM_ERROR, "获取设备配额失败", null);
    }

    private Integer getCanUseProductId(String customerId) {
        String productKey = "productKey." + customerId;
        String productIdStr = stringRedisTemplate.opsForValue().get(productKey);
        if (StringUtils.isEmpty(productIdStr)) {
            return 0;
        }
        return Integer.valueOf(productIdStr);
    }

}