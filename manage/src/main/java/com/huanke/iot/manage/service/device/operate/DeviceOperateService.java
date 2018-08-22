package com.huanke.iot.manage.service.device.operate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.base.dao.device.*;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.po.device.DeviceCustomerRelationPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.manage.vo.request.device.operate.DeviceAssignToCustomerRequest;
import com.huanke.iot.manage.vo.request.device.operate.DeviceCreateOrUpdateRequest;
import com.huanke.iot.manage.service.wechart.WechartUtil;
import com.huanke.iot.manage.vo.request.device.operate.DeviceListQueryRequest;
import com.huanke.iot.manage.vo.request.device.operate.DeviceQueryRequest;
import com.huanke.iot.manage.vo.response.device.DeviceListVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
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
    private DeviceModelMapper deviceModelMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private WechartUtil wechartUtil;

    /**2018-08-15
     * sixiaojun
     * 支持批量或单个添加
     * @param deviceLists
     * @return
     */
    public Boolean createDevice(List<DeviceCreateOrUpdateRequest.DeviceList> deviceLists) {
        for (DeviceCreateOrUpdateRequest.DeviceList device:deviceLists) {
            String mac=device.getMac();
            DevicePo devicePo=deviceMapper.selectByMac(mac);
            if(null != devicePo && StringUtils.isNotEmpty(devicePo.getDeviceId())){
                return  false;
            }
            else {
                DevicePo insertPo = new DevicePo();
                insertPo.setName(device.getName());
                insertPo.setTypeId(device.getTypeId());
                insertPo.setMac(device.getMac());
                insertPo.setCreateTime(device.getCreateTime());
                deviceMapper.insert(insertPo);
            }
        }
        return true;
    }

    /**2018-08-15
     * sixiaojun
     * 根据前台请求按页查询设备数据
     * @param deviceListQueryRequest
     * @return list
     */
    public List<DeviceListVo> queryDeviceByPage(DeviceListQueryRequest deviceListQueryRequest){
        //当期要查询的页
        Integer currentPage = deviceListQueryRequest.getPage();
        //每页显示的数量
        Integer limit= deviceListQueryRequest.getLimit();
        //偏移量
        Integer offset = (currentPage - 1) * limit;
        //查询所有数据相关数据，要求DevicePo所有值为null，所以新建一个空的DevicePo
        DevicePo queryPo=new DevicePo();
        List<DevicePo> devicePos=deviceMapper.selectList(queryPo,limit,offset);
        List<DeviceListVo> deviceQueryVos=devicePos.stream().map(devicePo -> {
            DeviceListVo deviceQueryVo=new DeviceListVo();
            deviceQueryVo.setName(devicePo.getName());
            deviceQueryVo.setMac(devicePo.getMac());
            deviceQueryVo.setDeviceTypeId(devicePo.getTypeId());
            if(null != devicePo.getBindStatus()){
                if(3 == devicePo.getBindStatus()){
                    deviceQueryVo.setBindStatus("已解绑");
                }
                if(2 == devicePo.getBindStatus()){
                    deviceQueryVo.setBindStatus("已绑定");
                }
                if(1 == devicePo.getBindStatus()){
                    deviceQueryVo.setBindStatus("未绑定");
                }
            }
            if(null != devicePo.getEnableStatus()){
                if(1 == devicePo.getEnableStatus()){
                    deviceQueryVo.setEnableStatus("已启用");
                }
                if(0 == devicePo.getEnableStatus()){
                    deviceQueryVo.setEnableStatus("已禁用");
                }
            }
            if(null != deviceGroupItemMapper.selectByDeviceId(devicePo.getId())){
                deviceQueryVo.setGroupId(deviceGroupItemMapper.selectByDeviceId(devicePo.getId()).getGroupId());
                deviceQueryVo.setGroupName(deviceGroupMapper.selectById(deviceGroupItemMapper.selectByDeviceId(devicePo.getId()).getGroupId()).getName());
            }
            else {
                deviceQueryVo.setGroupId(-1);
                deviceQueryVo.setGroupName("无");
            }
            if(null !=devicePo.getWorkStatus()){
                if(1 == devicePo.getWorkStatus()){
                    deviceQueryVo.setWorkStatus("租赁中");
                }
                if(0 == devicePo.getWorkStatus()){
                    deviceQueryVo.setWorkStatus("空闲");
                }
            }
            if(null != devicePo.getOnlineStatus()){
                if(1 == devicePo.getOnlineStatus()){
                    deviceQueryVo.setOnlineStatus("在线");
                }
                if(0 == devicePo.getOnlineStatus()){
                    deviceQueryVo.setOnlineStatus("离线");
                }
            }
            return deviceQueryVo;
        }).collect(Collectors.toList());
        return deviceQueryVos;
    }

    /**
     * 删除设备及与设备相关的信息
     * 2018-08-21
     * sixiaojun
     * @param deviceLists
     * @return
     */
    public Integer deleteDevice(List<DeviceCreateOrUpdateRequest.DeviceList> deviceLists){
        int count=0;
        for(DeviceCreateOrUpdateRequest.DeviceList device:deviceLists){
            //先从设备表中删除该mac地址的设备
            DevicePo devicePo=deviceMapper.selectByMac(device.getMac());
            if(deviceMapper.deleteDevice(devicePo)>0){
                //如果当前设备存在集群
                if(null != deviceGroupItemMapper.selectByDeviceId(devicePo.getId())) {
                    //删除成功后再从设备集群列表中删除该设备的集群相关信息
                    deviceGroupItemMapper.deleteDeviceById(devicePo.getId());
                }
                count++;
            }
        }
        //返回本次删除的设备总数
        return count;
    }

    /**
     * 将设备列表中的设备分配给设备型号，并与当前客户关联
     * 2018-08-21
     * sixiaojun
     * @param deviceAssignToCustomerRequest
     * @return
     */
    public Boolean assignDeviceToCustomer(DeviceAssignToCustomerRequest deviceAssignToCustomerRequest){
        //获取设备列表
        List<DeviceQueryRequest.DeviceList> deviceList=deviceAssignToCustomerRequest.getDeviceQueryRequest().getDeviceList();
        if(null != deviceList) {
            for (DeviceQueryRequest.DeviceList device : deviceList) {
                DeviceCustomerRelationPo deviceCustomerRelationPo = new DeviceCustomerRelationPo();
                deviceCustomerRelationPo.setCustomerId(deviceAssignToCustomerRequest.getCustomerId());
                deviceCustomerRelationPo.setDeviceId(deviceMapper.selectByMac(device.getMac()).getId());
                deviceCustomerRelationPo.setCreateTime(System.currentTimeMillis());
                deviceCustomerRelationPo.setLastUpdateTime(System.currentTimeMillis());
                //新增设备和客户关系
                deviceCustomerRelationMapper.insert(deviceCustomerRelationPo);
                //在设备表中更新deviceModelId字段，将设备与设备型号表关联
                DevicePo devicePo=new DevicePo();
                DeviceModelPo deviceModelPo=deviceModelMapper.selectByCustomerId(deviceAssignToCustomerRequest.getCustomerId());
                devicePo.setId(deviceMapper.selectByMac(device.getMac()).getId());
                devicePo.setModelId(deviceModelPo.getId());
                deviceMapper.updateByDeviceId(devicePo);
            }
            return true;
        }
        else {
            return false;
        }
    }
    /**
     * 2018-08-18
     * sixiaojun
     * 获取设备总数
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
     * @param deviceList
     * @return devicePo
     */
    public DevicePo isDeviceExist(List<DeviceCreateOrUpdateRequest.DeviceList> deviceList){
        DevicePo devicePo=null;
        for(DeviceCreateOrUpdateRequest.DeviceList device:deviceList){
            devicePo=deviceMapper.selectByMac(device.getMac());
            if(null != devicePo){
                return devicePo;
            }
        }
        return devicePo;
    }

    /**
     * 2018-08-20
     * sixiaojun
     * 根据设备列表中的设备mac查询某个设备是否已被分配
     * @param deviceList
     * @return
     */
    public Boolean isDeviceHasCustomer(List<DeviceQueryRequest.DeviceList> deviceList){
        for(DeviceQueryRequest.DeviceList device:deviceList){
            DevicePo devicePo=deviceMapper.selectByMac(device.getMac());
            //如果当前设备已被分配则返回错误
            if(null != deviceCustomerRelationMapper.selectByDeviceId(devicePo.getId())){
                return true;
            }
        }
        return false;
    }


    public Boolean updateDevice(DeviceCreateOrUpdateRequest.DeviceList deviceList) {
        DevicePo updatePo = new DevicePo();
        updatePo.setId(deviceMapper.selectByMac(deviceList.getMac()).getId());
        updatePo.setName(deviceList.getName());
        return deviceMapper.updateById(updatePo) > 0;
    }


    private JSONObject obtainDeviceJson(String appId,String appSecret,Integer publicId,String productId) {
        JSONObject deviceInfo = obtainDeviceInfo(appId,appSecret,publicId,productId);
        if (deviceInfo == null) {
            wechartUtil.getAccessToken(appId,appSecret,publicId, true);
            deviceInfo = obtainDeviceInfo(appId, appSecret, publicId,productId);
        }
        if (deviceInfo != null) {
            return deviceInfo;
        }
        return null;
    }

    private JSONObject obtainDeviceInfo(String appId,String appSecret,Integer publicId,String productId) {

        String accessToken = wechartUtil.getAccessToken(appId, appSecret,publicId, false);
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
            if (jsonObject != null) {
                JSONObject resultObject = jsonObject.getJSONObject("base_resp");
                if (resultObject != null && resultObject.containsKey("errcode")) {
                    Integer retCode = resultObject.getInteger("errcode");
                    if (retCode != null && retCode.equals(0)) {
                        return jsonObject;
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    private Integer getCanUseProductId(Integer publicId) {
        String productKey = "productKey." + publicId;
        String productIdStr = stringRedisTemplate.opsForValue().get(productKey);
        if (StringUtils.isEmpty(productIdStr)) {
            return 0;
        }
        return Integer.valueOf(productIdStr);
    }

}
