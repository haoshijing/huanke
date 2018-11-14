package com.huanke.iot.manage.service.device.operate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.*;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.customer.WxConfigMapper;
import com.huanke.iot.base.dao.device.*;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityOptionMapper;
import com.huanke.iot.base.dao.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.customer.WxConfigPo;
import com.huanke.iot.base.po.device.DeviceCustomerRelationPo;
import com.huanke.iot.base.po.device.DeviceCustomerUserRelationPo;
import com.huanke.iot.base.po.device.DeviceIdPoolPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityOptionPo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import com.huanke.iot.base.po.device.group.DeviceGroupPo;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityOptionPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.util.LocationUtils;
import com.huanke.iot.base.util.UniNoCreateUtils;
import com.huanke.iot.manage.common.util.ExcelUtil;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.gateway.MqttSendService;
import com.huanke.iot.manage.service.user.UserService;
import com.huanke.iot.manage.service.wechart.WechartUtil;
import com.huanke.iot.manage.vo.request.device.group.FuncListMessage;
import com.huanke.iot.manage.vo.request.device.operate.*;
import com.huanke.iot.manage.vo.response.device.BaseListVo;
import com.huanke.iot.manage.vo.response.device.ability.DeviceAbilityVo;
import com.huanke.iot.manage.vo.response.device.operate.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.subject.Subject;

//import com.huanke.iot.user.model.user.User;

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
    private DeviceAbilityMapper deviceAbilityMapper;

    @Autowired
    private DeviceAbilityOptionMapper deviceAbilityOptionMapper;

    @Autowired
    private DeviceModelAbilityOptionMapper deviceModelAbilityOptionMapper;

    @Autowired
    private DeviceTeamItemMapper deviceTeamItemMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private WechartUtil wechartUtil;

    @Autowired
    private WxConfigMapper wxConfigMapper;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;

    @Autowired
    private LocationUtils locationUtils;

    @Autowired
    private MqttSendService mqttSendService;


    private static String[] keys = {"name","manageName","mac", "customerName", "deviceType", "bindStatus", "enableStatus", "groupName",
            "workStatus", "onlineStatus", "modelId", "modelName", "birthTime", "lastOnlineTime", "location"};

    private static String[] texts = {"名称","管理名称", "MAC", "归属", "类型", "绑定状态", "启用状态", "集群名", "工作状态", "在线状态", "设备型号ID", "设备型号名称", "注册时间", "最后上上线时间", "地理位置"};

    /**
     * 2018-08-15
     * sixiaojun
     * 支持批量或单个添加
     *
     * @param deviceLists
     * @return
     */
    public ApiResponse<List<DeviceAddSuccessVo>> createDevice(List<DeviceCreateOrUpdateRequest.DeviceUpdateList> deviceLists) throws Exception {
        User user = userService.getCurrentUser();
        List<DeviceAddSuccessVo> deviceAddSuccessVoList = new ArrayList<>();
        List<DevicePo> devicePoList = deviceLists.stream().map(device -> {
            DevicePo insertPo = new DevicePo();
            insertPo.setName(device.getName());
            //如果管理名称为空，则默认为 设备名称
            insertPo.setManageName(StringUtils.isNotBlank(device.getManageName())?device.getManageName():device.getName());
            insertPo.setTypeId(device.getTypeId());
            insertPo.setMac(device.getMac());
            //设定绑定状态为未绑定
            insertPo.setBindStatus(DeviceConstant.BIND_STATUS_NO);
            //设定分配状态为未分配
            insertPo.setAssignStatus(DeviceConstant.ASSIGN_STATUS_NO);
            //设定工作状态为空闲
            insertPo.setWorkStatus(DeviceConstant.WORKING_STATUS_NO);
            //设定开关状态为关机
            insertPo.setPowerStatus(DeviceConstant.POWER_STATUS__NO);
            insertPo.setStatus(CommonConstant.STATUS_YES);
            //设定在线状态为离线
            insertPo.setOnlineStatus(DeviceConstant.ONLINE_STATUS_NO);
            //设定启用状态为禁用
            insertPo.setEnableStatus(DeviceConstant.ENABLE_STATUS_NO);
            insertPo.setHardVersion(device.getHardVersion());
            insertPo.setBirthTime(device.getBirthTime());

            /*添加人与添加时间*/
            insertPo.setCreateTime(System.currentTimeMillis());
            insertPo.setCreateUser(user.getId());

            insertPo.setSaNo(UniNoCreateUtils.createNo(DeviceConstant.DEVICE_UNI_NO_DEVICE));
            return insertPo;
        }).collect(Collectors.toList());
        //批量插入
        Boolean ret = deviceMapper.insertBatch(devicePoList) > 0;
        if (ret) {
            devicePoList.stream().forEach(devicePo -> {
                DeviceAddSuccessVo deviceAddSuccessVo = new DeviceAddSuccessVo();
                deviceAddSuccessVo.setDeviceId(devicePo.getId());
                deviceAddSuccessVoList.add(deviceAddSuccessVo);
            });
            return new ApiResponse<>(RetCode.OK, "添加成功", deviceAddSuccessVoList);
        } else {
            return new ApiResponse<>(RetCode.OK, "添加失败", null);
        }
    }

    /**
     * 修改设备
     *
     * @param deviceUpdateRequest
     * @return
     */
    public ApiResponse<Boolean> updateDevice(DeviceUpdateRequest deviceUpdateRequest) {
        boolean ret = true;
        User user = userService.getCurrentUser();
        DevicePo devicePo = deviceMapper.selectById(deviceUpdateRequest.getId());
        if (devicePo != null) {
            BeanUtils.copyProperties(deviceUpdateRequest, devicePo);
            /*修改人与修改时间*/
            devicePo.setLastUpdateUser(user.getId());
            devicePo.setLastUpdateTime(System.currentTimeMillis());

            ret = deviceMapper.updateById(devicePo) > 0;
        } else {
            return new ApiResponse<>(RetCode.OK, "该设备不存在！", false);
        }

        return new ApiResponse<>(RetCode.OK, "修改成功", ret);
    }

    /**
     * 2018-08-15
     * sixiaojun
     * 根据主键查询设备详情
     *
     * @param deviceId
     * @return DeviceListVo
     */
    public DeviceListVo queryDeviceById(Integer deviceId) throws Exception {

        //查询所有数据相关数据，要求DevicePo所有值为null，所以新建一个空的DevicePo
        //此处仅仅查询主设备

        DevicePo devicePo = deviceMapper.selectById(deviceId);
        if (devicePo == null) {
            return null;
        }

        DeviceCustomerRelationPo deviceCustomerRelationPo;
        DeviceListVo deviceQueryVo = new DeviceListVo();
        deviceQueryVo.setName(devicePo.getName());
        deviceQueryVo.setManageName(devicePo.getManageName());
        deviceQueryVo.setMac(devicePo.getMac());
        deviceQueryVo.setSaNo(devicePo.getSaNo());
        deviceQueryVo.setWxDeviceId(devicePo.getWxDeviceId());

        deviceQueryVo.setTypeId(devicePo.getTypeId());
        deviceQueryVo.setDeviceType(devicePo.getTypeName());
        deviceQueryVo.setTypeNo(devicePo.getTypeNo());
        deviceQueryVo.setModelId(devicePo.getModelId());
        deviceQueryVo.setModelName(devicePo.getModelName());
        deviceQueryVo.setModelCode(devicePo.getModelCode());
        deviceQueryVo.setModelNo(devicePo.getModelNo());

        /*设置设备客户从属关系*/
        deviceCustomerRelationPo = deviceCustomerRelationMapper.selectByDeviceId(devicePo.getId());
        if (null != deviceCustomerRelationPo) {
            deviceQueryVo.setCustomerId(deviceCustomerRelationPo.getCustomerId());
            deviceQueryVo.setCustomerName(deviceCustomerRelationPo.getCustomerName());
            deviceQueryVo.setSLD(deviceCustomerRelationPo.getSLD());
        }

        deviceQueryVo.setIp(devicePo.getIp());
        deviceQueryVo.setMapGps(devicePo.getMapGps());
        deviceQueryVo.setLocation(devicePo.getLocation());
        deviceQueryVo.setAssignStatus(devicePo.getAssignStatus());
        deviceQueryVo.setBindStatus(devicePo.getBindStatus());
        deviceQueryVo.setEnableStatus(devicePo.getEnableStatus());
        deviceQueryVo.setWorkStatus(devicePo.getWorkStatus());
        deviceQueryVo.setOnlineStatus(devicePo.getOnlineStatus());
        deviceQueryVo.setStatus(devicePo.getStatus());
        //获取主从相关的信息
        deviceQueryVo.setHostStatus(devicePo.getHostStatus());
        Integer childCount = this.deviceMapper.queryChildDeviceCount(devicePo.getId());
        deviceQueryVo.setChildCount(childCount);
        //查询集群信息
        DeviceGroupPo queryDeviceGroup = this.deviceGroupMapper.selectByDeviceId(devicePo.getId());
        if (null != queryDeviceGroup) {
            deviceQueryVo.setGroupId(queryDeviceGroup.getId());
            deviceQueryVo.setGroupName(queryDeviceGroup.getName());
        } else {
            deviceQueryVo.setGroupId(-1);
            deviceQueryVo.setGroupName("无集群");
        }
        deviceQueryVo.setId(devicePo.getId());
        deviceQueryVo.setBirthTime(devicePo.getBirthTime());
        /*创建人、创建时间、修改人，修改时间*/
        deviceQueryVo.setCreateTime(devicePo.getCreateTime());
        deviceQueryVo.setLastUpdateTime(devicePo.getLastUpdateTime());
        deviceQueryVo.setCreateUser(devicePo.getCreateUser());
        deviceQueryVo.setCreateUserName(userService.getUserName(devicePo.getCreateUser()));
        deviceQueryVo.setLastUpdateUser(devicePo.getLastUpdateUser());
        deviceQueryVo.setLastUpdateUserName(userService.getUserName(devicePo.getLastUpdateUser()));
        //查询绑定信息
        DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = this.deviceCustomerUserRelationMapper.selectByDeviceId(devicePo.getId());
        if (null != deviceCustomerUserRelationPo) {
            deviceQueryVo.setUserOpenId(deviceCustomerUserRelationPo.getOpenId());
            deviceQueryVo.setUserName(deviceCustomerUserRelationPo.getNickname());
        }
        String funcValue = deviceOperLogMapper.queryPowerByDeviceId(deviceId);

        if (null != funcValue && StringUtils.isNotEmpty(funcValue)) {
            //0-关机 1-开机
            deviceQueryVo.setPowerStatus(funcValue.equals("0") ? 0 : 1);
        } else {
            deviceQueryVo.setPowerStatus(0);
        }
        deviceQueryVo.setLocation(devicePo.getLocation());

        return deviceQueryVo;
    }


    /**
     * 2018-08-15
     * sixiaojun
     * 根据前台请求按页查询设备数据
     *
     * @param deviceListQueryRequest
     * @return list
     */
    public ApiResponse<List<DeviceListVo>> queryDeviceByPage(DeviceListQueryRequest deviceListQueryRequest) throws Exception {
        //todo 显示从设备
//        Subject subject = SecurityUtils.getSubject();
//        UserPo user = (UserPo) subject.getSession().getAttribute("user");
        Integer offset = (deviceListQueryRequest.getPage() - 1) * deviceListQueryRequest.getLimit();
        Integer limit = deviceListQueryRequest.getLimit();
        Integer customerId = customerService.obtainCustomerId(false);
        log.info("当前的用户id: =",customerId);
        //查询所有数据相关数据，要求DevicePo所有值为null，所以新建一个空的DevicePo
        //此处仅仅查询主设备
        DevicePo queryPo = new DevicePo();
        if (deviceListQueryRequest != null) {
            BeanUtils.copyProperties(deviceListQueryRequest, queryPo);
        }
        if (deviceListQueryRequest.getCustomerId() == null) {
            queryPo.setCustomerId(customerId);
        }
        List<DevicePo> devicePos = deviceMapper.selectList(queryPo, limit, offset);
        if (null == devicePos || 0 == devicePos.size()) {
            return new ApiResponse<>(RetCode.OK, "暂无设备", null);
        }
        List<DeviceOperLogPo> deviceOnlinePos = deviceOperLogMapper.queryAllOnlineByCreateTime();
        Map<String,Long> onlineValues = new HashMap<String,Long>();
        deviceOnlinePos.stream().forEach(temp ->{onlineValues.put(temp.getDeviceId().toString(),temp.getCreateTime());});
        List<DeviceListVo> deviceQueryVos = devicePos.stream().map(devicePo -> {
            DeviceListVo deviceQueryVo = new DeviceListVo();
            deviceQueryVo.setName(devicePo.getName());
            deviceQueryVo.setManageName(devicePo.getManageName());
            deviceQueryVo.setMac(devicePo.getMac());
            deviceQueryVo.setSaNo(devicePo.getSaNo());

            deviceQueryVo.setTypeId(devicePo.getTypeId());
            deviceQueryVo.setModelId(devicePo.getModelId());
            deviceQueryVo.setModelName(devicePo.getModelName());
            //查询管理名称
//            DeviceTeamItemPo deviceTeamItemPo = this.deviceTeamItemMapper.selectByDeviceId(devicePo.getId());
//            if(null != deviceTeamItemPo){
//                deviceQueryVo.setManageName(deviceTeamItemPo.getManageName());
//                log.info("当前设备的管理名称：={}",deviceQueryVo.getModelName());
//            }
            deviceQueryVo.setCustomerId(devicePo.getCustomerId());
            deviceQueryVo.setCustomerName(devicePo.getCustomerName());

            deviceQueryVo.setLocation(devicePo.getLocation());
            deviceQueryVo.setAssignStatus(devicePo.getAssignStatus());
            deviceQueryVo.setBindStatus(devicePo.getBindStatus());
            deviceQueryVo.setEnableStatus(devicePo.getEnableStatus());
            deviceQueryVo.setWorkStatus(devicePo.getWorkStatus());
            deviceQueryVo.setPowerStatus(devicePo.getPowerStatus());
            deviceQueryVo.setOnlineStatus(devicePo.getOnlineStatus());
            deviceQueryVo.setStatus(devicePo.getStatus());
            //最后上线时间
            DeviceOperLogPo deviceOnlinePo = new DeviceOperLogPo();
            deviceOnlinePo.setCreateTime(onlineValues.get(devicePo.getId().toString()));
            if (null != deviceOnlinePo && null != deviceOnlinePo.getCreateTime()) {
                deviceQueryVo.setLastOnlineTime(deviceOnlinePo.getCreateTime());
            } else {
                deviceQueryVo.setLastOnlineTime(null);
            }
            //获取主从相关的信息
            deviceQueryVo.setHostStatus(devicePo.getHostStatus());
            Integer childCount = this.deviceMapper.queryChildDeviceCount(devicePo.getId());
            deviceQueryVo.setChildCount(childCount);
            //查询集群信息
            DeviceGroupPo queryDeviceGroup = this.deviceGroupMapper.selectByDeviceId(devicePo.getId());
//            DeviceGroupItemPo queryDeviceGroupItemPo = this.deviceGroupItemMapper.selectByDeviceId(devicePo.getId());
            if (null != queryDeviceGroup) {
                deviceQueryVo.setGroupId(queryDeviceGroup.getId());
                deviceQueryVo.setGroupName(queryDeviceGroup.getName());
            } else {
                deviceQueryVo.setGroupId(-1);
                deviceQueryVo.setGroupName("无集群");
            }
            deviceQueryVo.setId(devicePo.getId());
            deviceQueryVo.setBirthTime(devicePo.getBirthTime());
            /*创建人、创建时间、修改人，修改时间*/
            deviceQueryVo.setCreateTime(devicePo.getCreateTime());
            deviceQueryVo.setLastUpdateTime(devicePo.getLastUpdateTime());
            deviceQueryVo.setCreateUser(devicePo.getCreateUser());
//            deviceQueryVo.setCreateUserName(userService.getUserName(devicePo.getCreateUser()));
            deviceQueryVo.setLastUpdateUser(devicePo.getLastUpdateUser());
//            deviceQueryVo.setLastUpdateUserName(userService.getUserName(devicePo.getLastUpdateUser()));
            //查询绑定信息
            DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = this.deviceCustomerUserRelationMapper.selectByDeviceId(devicePo.getId());
            if (null != deviceCustomerUserRelationPo) {
                deviceQueryVo.setUserOpenId(deviceCustomerUserRelationPo.getOpenId());
                deviceQueryVo.setUserName(deviceCustomerUserRelationPo.getNickname());
            }
            deviceQueryVo.setLocation(devicePo.getLocation());
            //todo 开关机筛选
            return deviceQueryVo;
        }).collect(Collectors.toList());

        return new ApiResponse<>(RetCode.OK, "查询成功", deviceQueryVos);
    }


    /**
     * 2018-08-15
     * sixiaojun
     * 根据前台请求按页查询设备数据
     *
     * @param deviceListQueryRequest
     * @return list
     */
    public ApiResponse<BaseListVo> queryDeviceList(DeviceListQueryRequest deviceListQueryRequest) throws Exception {

        BaseListVo baseListVo = new BaseListVo();

        Integer customerId = customerService.obtainCustomerId(false);
        //查询所有数据相关数据，要求DevicePo所有值为null，所以新建一个空的DevicePo
        //此处仅仅查询主设备
        DevicePo queryPo = new DevicePo();
        if (deviceListQueryRequest != null) {
            BeanUtils.copyProperties(deviceListQueryRequest, queryPo);
        }

        if (deviceListQueryRequest.getCustomerId() == null) {
            queryPo.setCustomerId(customerId);
        }
        ApiResponse<List<DeviceListVo>> deviceQueryRtn = queryDeviceByPage(deviceListQueryRequest);
        if (deviceQueryRtn != null && deviceQueryRtn.getCode() != RetCode.OK) {
            return new ApiResponse<>(RetCode.ERROR, deviceQueryRtn.getMsg());
        }
        Integer totalCount = selectCount(queryPo);

        baseListVo.setDataList(deviceQueryRtn.getData());
        baseListVo.setTotalCount(totalCount);

        return new ApiResponse<>(RetCode.OK, "查询成功", baseListVo);
    }

    /**
     * 2018-08-18
     * sixiaojun
     * 获取设备总数
     *
     * @param
     * @return
     */
    public Integer selectCount(DevicePo devicePo) throws Exception {

        return deviceMapper.selectCount(devicePo);
    }

    /**
     * 导出设备列表
     *
     * @param response
     * @param deviceListExportRequest
     * @return
     * @throws Exception
     */
    public ApiResponse<Boolean> exportDeviceList(HttpServletResponse response, DeviceListExportRequest deviceListExportRequest) throws Exception {
        //生成列名map
        Map<String, String> titleMap = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            titleMap.put(keys[i], texts[i]);
        }
        //根据条件筛选excel列名
        Class cls = deviceListExportRequest.getClass();
        Field[] fields = cls.getDeclaredFields();
        List<String> titleKeys = new ArrayList<>();
        List<String> titleNames = new ArrayList<>();
        Map<String, String> filterMap = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            String getMethodName = "get" + field.getName().substring(0, 1).toUpperCase()
                    + field.getName().substring(1);
            Method getMethod = cls.getMethod(getMethodName, new Class[]{});
            Object value = getMethod.invoke(deviceListExportRequest, new Object[]{});
            if (value instanceof Boolean) {
                Boolean result = (Boolean) field.get(deviceListExportRequest);
                if (result) {
                    titleKeys.add(field.getName());
                    titleNames.add(titleMap.get(field.getName()));
                    filterMap.put(field.getName(),titleMap.get(field.getName()));
                }
            }
        }
        //生成列后按列条件筛选device数据
        ApiResponse<List<DeviceListVo>> result = this.queryDeviceByPage(deviceListExportRequest.getDeviceListQueryRequest());
//        log.info("当时设备列表的查询代码：= {}",result.getCode());
//        log.info("当时设备列表的查询数据：= {}",result.getData());
        if(null == result.getData() || RetCode.OK != result.getCode()){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"当期条件下设备列表中无设备");
        }
        List<DeviceListVo> deviceListVoList = result.getData();
        //数值类型转换
        List<DeviceExportVo> deviceExportVoList = new ArrayList<>();
        deviceListVoList.stream().forEach(eachPo ->{
            DeviceExportVo deviceExportVo = new DeviceExportVo();
            BeanUtils.copyProperties(eachPo,deviceExportVo);
            deviceExportVo.setAssignStatus( DeviceConstant.ASSIGN_STATUS_YES.equals(eachPo.getAssignStatus())?"已分配":"未分配");
            deviceExportVo.setBindStatus( DeviceConstant.BIND_STATUS_YES.equals(eachPo.getBindStatus())?"已绑定":"未绑定");
            deviceExportVo.setEnableStatus( DeviceConstant.ENABLE_STATUS_YES.equals(eachPo.getEnableStatus())?"启用":"禁用");
            deviceExportVo.setOnlineStatus(DeviceConstant.ONLINE_STATUS_YES.equals(eachPo.getOnlineStatus())?"在线":"离线");
            if(null != eachPo.getLastOnlineTime()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                deviceExportVo.setLastOnlineTime(sdf.format(new Date(eachPo.getLastOnlineTime())));
            }
        });
        String[] titles = new String[titleNames.size()];
        titleNames.toArray(titles);
        ExcelUtil<DeviceListVo> deviceListVoExcelUtil = new ExcelUtil<>();
        deviceListVoExcelUtil.exportExcel(deviceListExportRequest.getFileName(), response, deviceListExportRequest.getSheetTitle(), titles, deviceListVoList, filterMap, deviceListVoExcelUtil.EXCEl_FILE_2007);
        return new ApiResponse<>(RetCode.OK, "ss");
    }

//    /**
//     * 删除设备及与设备相关的信息
//     * 删除设备需 先解除 绑定关系、组关系、群关系、解除客户分配关系（召回）、最后删除
//     * 2018-08-21
//     * sixiaojun
//     *
//     * @param deviceLists
//     * @return
//     */
//    public ApiResponse<Integer> deleteDevice(List<DeviceCreateOrUpdateRequest.DeviceUpdateList> deviceLists) throws Exception{
//        deviceLists.stream().forEach(device -> {
//            //先从设备表中删除该mac地址的设备
//            DevicePo devicePo = deviceMapper.selectByMac(device.getMac());
//            if (deviceMapper.deleteDevice(devicePo) > 0) {
//                deviceGroupItemMapper.deleteDeviceById(devicePo.getId());
//                //如果当前设备已被分配给客户
//                if (null != deviceCustomerRelationMapper.selectByDeviceId(devicePo.getId())) {
//                    //从客户关系表中删除记录
//                    deviceCustomerRelationMapper.deleteDeviceById(devicePo.getId());
//                }
//            }
//        });
//        //返回本次删除的设备总数
//        return new ApiResponse<>(RetCode.OK,"删除成功",deviceLists.size());
//    }


    /**
     * 删除设备及与设备相关的信息
     * 删除设备需 先解除 绑定关系、组关系、群关系、解除客户分配关系（召回）、最后删除
     * 2018-08-21
     * sixiaojun
     *
     * @param deviceVo
     * @return
     */
    public ApiResponse<DevicePo> deleteDevice(DeviceUnbindRequest.deviceVo deviceVo) throws Exception {
        Integer deviceId = deviceVo.deviceId;
        String mac = deviceVo.mac;
        //解除 用户绑定关系、组关系
        ApiResponse<DevicePo> result = untieOneDeviceToUser(deviceVo);
        //解绑成功之后，开始 解除群关系 并 进行解除 客户关系。
        if (RetCode.OK == result.getCode()) {
            DevicePo devicePo = result.getData();
            //当设备已被分配，则进行召回
            if (DeviceConstant.ASSIGN_STATUS_YES.equals(devicePo.getAssignStatus())) {
                DeviceIdPoolPo deviceIdPoolPo = this.deviceIdPoolMapper.selectByWxDeviceId(devicePo.getWxDeviceId());

                if (deviceIdPoolPo != null) {
                    deviceIdPoolPo.setStatus(DeviceConstant.WXDEVICEID_STATUS_NO);
                    deviceIdPoolPo.setLastUpdateTime(System.currentTimeMillis());
                    deviceIdPoolMapper.updateById(deviceIdPoolPo);
                }

            }


            //删除 该设备的群绑定关系
            this.deviceGroupItemMapper.deleteItemsByDeviceId(deviceId);
            //解除客户分配关系
            deviceCustomerRelationMapper.deleteDeviceById(deviceId);

            deviceMapper.deleteDeviceById(deviceId);

        } else {
            return result;
        }
        return new ApiResponse<>(RetCode.OK, "删除成功");
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
    public ApiResponse<DevicePo> deleteOneDevice(DeviceUnbindRequest.deviceVo deviceVo) throws Exception {
        User user = userService.getCurrentUser();
        Integer deviceId = deviceVo.deviceId;
        String mac = deviceVo.mac;
        //解除 用户绑定关系、组关系
        ApiResponse<DevicePo> result = untieOneDeviceToUser(deviceVo);
        //解绑成功之后，开始 解除群关系 并 进行解除 客户关系。
        if (RetCode.OK == result.getCode()) {


            //更改 设备信息
            DevicePo queryDevicePo = deviceMapper.selectById(deviceId);
            if (queryDevicePo != null) {

                //当设备已被分配，则进行召回
                if (DeviceConstant.ASSIGN_STATUS_YES.equals(queryDevicePo.getAssignStatus())) {
                    //记录要更新的设备池信息
                    DeviceIdPoolPo deviceIdPoolPo = this.deviceIdPoolMapper.selectByWxDeviceId(queryDevicePo.getWxDeviceId());

                    if (deviceIdPoolPo != null) {
                        deviceIdPoolPo.setStatus(DeviceConstant.WXDEVICEID_STATUS_NO);
                        deviceIdPoolPo.setLastUpdateTime(System.currentTimeMillis());
                        deviceIdPoolMapper.updateById(deviceIdPoolPo);
                    }
                    //解除客户分配关系
                    deviceCustomerRelationMapper.deleteDeviceById(deviceId);

                }
                //删除 该设备的群绑定关系
                this.deviceGroupItemMapper.deleteItemsByDeviceId(deviceId);


                // 删除该设备的 微信deviceid
                queryDevicePo.setWxDeviceId(null);
                queryDevicePo.setWxDevicelicence(null);
                queryDevicePo.setWxQrticket(null);
                //设定工作状态为空闲
                queryDevicePo.setWorkStatus(DeviceConstant.WORKING_STATUS_NO);
                //设定在线状态为离线
                queryDevicePo.setOnlineStatus(DeviceConstant.ONLINE_STATUS_NO);
                //设定启用状态为禁用
                queryDevicePo.setEnableStatus(DeviceConstant.ENABLE_STATUS_NO);

                queryDevicePo.setAssignStatus(DeviceConstant.ASSIGN_STATUS_NO);

                queryDevicePo.setAssignTime(null);

                queryDevicePo.setStatus(CommonConstant.STATUS_DEL);

                queryDevicePo.setLastUpdateTime(System.currentTimeMillis());

                queryDevicePo.setLastUpdateUser(user.getId());

                deviceMapper.updateById(queryDevicePo);
            }

        } else {
            return result;
        }
        return new ApiResponse<>(RetCode.OK, "删除成功");
    }

    /**
     * 设备恢复（单个）
     *
     * @param deviceVo
     * @return
     */
    public ApiResponse<Boolean> recoverDevice(DeviceUnbindRequest.deviceVo deviceVo) {
        try {

            User user = userService.getCurrentUser();
            Integer deviceId = deviceVo.deviceId;
            String mac = deviceVo.mac;
            if (null == deviceId || deviceId <= 0 || StringUtils.isBlank(mac)) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "参数不可为空");
            }

            //查询该设备详情 进行验证
            DevicePo queryDevicePo = deviceMapper.selectById(deviceId);

            if (null == queryDevicePo) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "当前设备不存在");
            } else if (!mac.equals(queryDevicePo.getMac())) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "设备主键与mac地址不匹配");
            }
            if (CommonConstant.STATUS_YES.equals(queryDevicePo.getStatus()) ) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "非已删除设备不可恢复");
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
//            queryDevicePo.setCreateTime(System.currentTimeMillis());
            queryDevicePo.setLastUpdateTime(System.currentTimeMillis());
            queryDevicePo.setLastUpdateUser(user.getId());

            deviceMapper.updateById(queryDevicePo);
            return new ApiResponse<>(RetCode.OK, "设备恢复成功");

        } catch (Exception e) {
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
    public ApiResponse<Boolean> assignDeviceToCustomer(DeviceAssignToCustomerRequest deviceAssignToCustomerRequest) throws Exception {
        Boolean ret = true;
        User user = userService.getCurrentUser();
        //获取设备列表
        List<DeviceQueryRequest.DeviceQueryList> deviceList = deviceAssignToCustomerRequest.getDeviceQueryRequest().getDeviceList();
        if (deviceList != null && deviceList.size() > 0) {
            //首先查询device_pool表中 该客户的该型号下 是否存在足够数量的device_id和device_license
            DeviceIdPoolPo deviceIdPoolPo = new DeviceIdPoolPo();
            deviceIdPoolPo.setCustomerId(deviceAssignToCustomerRequest.getCustomerId());
            deviceIdPoolPo.setProductId(deviceAssignToCustomerRequest.getProductId());
            deviceIdPoolPo.setStatus(DeviceConstant.WXDEVICEID_STATUS_NO);
            Integer devicePoolCount = deviceIdPoolMapper.selectCount(deviceIdPoolPo);
            log.info("devicePoolCount = {}", devicePoolCount);
            //若当前设备池中当前产品的配额的数量不够，则向微信公众号请求所需要的新的设备证书
            if (deviceList.size() > devicePoolCount) {
                Integer addCount = deviceList.size() - devicePoolCount;
                //获取数据
                ApiResponse<Integer> result = createWxDeviceIdPools(deviceAssignToCustomerRequest.getCustomerId(), deviceAssignToCustomerRequest.getProductId(), addCount);
                if (result == null || RetCode.PARAM_ERROR == result.getCode() || RetCode.ERROR == result.getCode()) {
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
                queryPoolPo.setCustomerId(deviceAssignToCustomerRequest.getCustomerId());
                queryPoolPo.setProductId(deviceAssignToCustomerRequest.getProductId());
                //每次查找当前池中未使用第一条license
                List<DeviceIdPoolPo> poolPoList = deviceIdPoolMapper.selectList(queryPoolPo, 1, offset);
                if (poolPoList != null && poolPoList.size() > 0) {
                    DeviceIdPoolPo resultPo = poolPoList.get(0);

                    //记录本次使用的pool状态为已占用
                    resultPo.setStatus(DeviceConstant.WXDEVICEID_STATUS_YES);
                    deviceIdPoolPoList.add(resultPo);
                    offset++;
                    //在设备表中更新deviceModelId字段，将设备与设备型号表关联
                    DevicePo devicePo = deviceMapper.selectByMac(device.getMac());
                    devicePo.setModelId(deviceAssignToCustomerRequest.getModelId());
                    devicePo.setStatus(CommonConstant.STATUS_YES);
                    devicePo.setAssignStatus(DeviceConstant.ASSIGN_STATUS_YES);
                    devicePo.setAssignTime(System.currentTimeMillis());
                    devicePo.setProductId(deviceAssignToCustomerRequest.getProductId());
                    devicePo.setWxDeviceId(resultPo.getWxDeviceId());
                    devicePo.setWxDevicelicence(resultPo.getWxDeviceLicence());
                    devicePo.setWxQrticket(resultPo.getWxQrticket());
                    //刷新最新更新时间
                    devicePo.setLastUpdateTime(System.currentTimeMillis());
                    devicePo.setLastUpdateUser(user.getId());

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
    public ApiResponse<Boolean> callBackDeviceFromCustomer(List<DeviceQueryRequest.DeviceQueryList> deviceQueryRequests) throws Exception {
        List<DeviceCustomerRelationPo> deviceCustomerRelationPoList = new ArrayList<>();
        List<DevicePo> devicePoList = new ArrayList<>();
        List<DeviceIdPoolPo> deviceIdPoolPoList = new ArrayList<>();

        User user = userService.getCurrentUser();

        if (deviceQueryRequests != null && deviceQueryRequests.size() > 0) {
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
                        DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = this.deviceCustomerUserRelationMapper.selectByDeviceId(devicePo.getId());

                        //若存在绑定设备则先进行解绑
                        if (null != deviceCustomerUserRelationPo) {
                            this.deviceCustomerUserRelationMapper.deleteRelationByDeviceId(devicePo.getId());
                            this.deviceTeamItemMapper.deleteItemsByDeviceId(devicePo.getId());
                            devicePo.setBindStatus(DeviceConstant.BIND_STATUS_NO);
                            devicePo.setBindTime(null);
                        }
                        //记录要更新的设备信息
                        devicePo.setModelId(null);
                        devicePo.setWxDeviceId(null);
                        devicePo.setWxDevicelicence(null);
                        devicePo.setWxQrticket(null);
                        devicePo.setProductId(null);

                        devicePo.setAssignStatus(DeviceConstant.ASSIGN_STATUS_NO);
                        devicePo.setAssignTime(null);
                        devicePo.setLastUpdateTime(System.currentTimeMillis());
                        devicePo.setLastUpdateUser(user.getId());
                        devicePoList.add(devicePo);
                    }
            );
        }

        this.deviceMapper.updateBatch(devicePoList);
        this.deviceIdPoolMapper.updateBatch(deviceIdPoolPoList);
        Boolean ret = this.deviceCustomerRelationMapper.deleteBatch(deviceCustomerRelationPoList) > 0;
        if (ret) {
            return new ApiResponse<>(RetCode.OK, "设备召回成功", true);
        } else {
            return new ApiResponse<>(RetCode.OK, "设备召回失败", false);
        }
    }


    /**
     * 召回 每一台设备的操作。
     *
     * @param devicePos
     * @return
     * @throws Exception
     */
    public ApiResponse<Boolean> callBackDeviceList(List<DevicePo> devicePos) throws Exception {

        User user = userService.getCurrentUser();
        //客户设备关系集合
        List<DeviceCustomerRelationPo> deviceCustomerRelationPoList = new ArrayList<>();
        //设备集合
        List<DevicePo> devicePoList = new ArrayList<>();
        // wxDeviceId集合
        List<DeviceIdPoolPo> deviceIdPoolPoList = new ArrayList<>();

        if (devicePos != null && devicePos.size() > 0) {

            devicePos.stream().forEach(devicePo -> {

                DeviceCustomerRelationPo deviceCustomerRelationPo = this.deviceCustomerRelationMapper.selectByDeviceMac(devicePo.getMac());
                //记录需要删除的客户设备关系
                deviceCustomerRelationPoList.add(deviceCustomerRelationPo);
                //记录要更新的设备池信息
                DeviceIdPoolPo deviceIdPoolPo = this.deviceIdPoolMapper.selectByWxDeviceId(devicePo.getWxDeviceId());
                deviceIdPoolPo.setStatus(DeviceConstant.WXDEVICEID_STATUS_NO);
                deviceIdPoolPo.setLastUpdateTime(System.currentTimeMillis());
                deviceIdPoolPoList.add(deviceIdPoolPo);
                DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = this.deviceCustomerUserRelationMapper.selectByDeviceId(devicePo.getId());

                //若存在绑定设备则先进行解绑
                if (null != deviceCustomerUserRelationPo) {
                    this.deviceCustomerUserRelationMapper.deleteRelationByDeviceId(devicePo.getId());
                    this.deviceTeamItemMapper.deleteItemsByDeviceId(devicePo.getId());
                    devicePo.setBindStatus(DeviceConstant.BIND_STATUS_NO);
                    devicePo.setBindTime(null);
                }
                //记录要更新的设备信息
                devicePo.setModelId(null);
                devicePo.setWxDeviceId(null);
                devicePo.setWxDevicelicence(null);
                devicePo.setWxQrticket(null);
                devicePo.setProductId(null);

                devicePo.setAssignStatus(DeviceConstant.ASSIGN_STATUS_NO);
                devicePo.setAssignTime(null);
                devicePo.setLastUpdateTime(System.currentTimeMillis());
                devicePo.setLastUpdateUser(user.getId());
                devicePoList.add(devicePo);
            });

            this.deviceMapper.updateBatch(devicePoList);
            this.deviceIdPoolMapper.updateBatch(deviceIdPoolPoList);
            Boolean ret = this.deviceCustomerRelationMapper.deleteBatch(deviceCustomerRelationPoList) > 0;
            if (ret) {
                return new ApiResponse<>(RetCode.OK, "设备召回成功", true);
            } else {
                return new ApiResponse<>(RetCode.ERROR, "设备召回失败", false);
            }

        } else {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "不存在可召回的设备", false);
        }
    }

    /**
     * 绑定设备
     *
     * @param deviceBindToUserRequest
     * @return
     */
    public ApiResponse<Boolean> bindDeviceToUser(DeviceBindToUserRequest deviceBindToUserRequest) throws Exception {
        Integer customerId = this.customerService.obtainCustomerId(false);
        User user = userService.getCurrentUser();
        List<DeviceTeamItemPo> deviceTeamItemPoList = new ArrayList<>();
        List<DevicePo> devicePoList = new ArrayList<>();
        List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPoList = new ArrayList<>();
        DeviceTeamPo deviceTeamPo = new DeviceTeamPo();
        CustomerUserPo customerUserPo = customerUserMapper.selectByOpenId(deviceBindToUserRequest.getOpenId());
        if (null == customerUserPo) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该用户不存在");
        }
        //二级客户进行绑定操作则进行用户鉴权
        if(null != customerId && customerUserPo.getCustomerId() != customerId){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"用户不存在或尚未关注公众号");
        }
        //如果该设备组的id为-1 ，认定为 没有该组。则新增组
        if (DeviceConstant.HAS_TEAM_NO.equals(deviceBindToUserRequest.getTeamId())) {

            deviceTeamPo.setName(deviceBindToUserRequest.getTeamName());
            deviceTeamPo.setMasterUserId(customerUserPo.getId());
            deviceTeamPo.setStatus(CommonConstant.STATUS_YES);
            deviceTeamPo.setCreateTime(System.currentTimeMillis());
            //设置组的状态为终端组
            deviceTeamPo.setTeamStatus(DeviceTeamConstants.DEVICE_TEAM_STATUS_TERMINAL);
            deviceTeamPo.setTeamType(DeviceTeamConstants.DEVICE_TEAM_TYPE_USER);
            deviceTeamPo.setCreateTime(System.currentTimeMillis());

            deviceTeamPo.setCreateUserId(customerUserPo.getId());
            deviceTeamPo.setCustomerId(customerUserPo.getCustomerId());
            deviceTeamMapper.insert(deviceTeamPo);
        } else {
            deviceTeamPo = this.deviceTeamMapper.selectById(deviceBindToUserRequest.getTeamId());
        }

        Integer deviceTeamId = deviceTeamPo.getId();
        List<DeviceQueryRequest.DeviceQueryList> bindDeviceList = deviceBindToUserRequest.getDeviceQueryRequest().getDeviceList();

        if (bindDeviceList != null && bindDeviceList.size() > 0) {
            for (int m = 0; m < bindDeviceList.size(); m++) {
                DeviceQueryRequest.DeviceQueryList bindDevice = bindDeviceList.get(m);

                DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = new DeviceCustomerUserRelationPo();
                DeviceTeamItemPo deviceTeamItemPo = new DeviceTeamItemPo();

                //查询 设备是否存在
                DevicePo devicePo = this.deviceMapper.selectByMac(bindDevice.getMac());
                if (devicePo != null) {
                    //查询 该设备是否有客户关系，即 该设备是否被分配。
                    DeviceCustomerRelationPo queryDeviceCustomerRelationPo = deviceCustomerRelationMapper.selectByDeviceId(devicePo.getId());
                    if (queryDeviceCustomerRelationPo != null) {
                        //该设备被添加进入组的同时也被绑定给了当前的终端用户，因此设定此处的绑定状态为已绑定
                        devicePo.setBindStatus(DeviceConstant.BIND_STATUS_YES);
                        //设定绑定时间
                        devicePo.setBindTime(System.currentTimeMillis());

                        devicePo.setLastUpdateTime(System.currentTimeMillis());
                        devicePo.setLastUpdateUser(user.getId());

                        deviceTeamItemPo.setDeviceId(devicePo.getId());
                        deviceTeamItemPo.setTeamId(deviceTeamId);
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
                    } else {
                        return new ApiResponse<>(RetCode.PARAM_ERROR, "MAC：" + bindDevice.getMac() + " 未被分配，绑定失败！", true);
                    }

                } else {
                    return new ApiResponse<>(RetCode.PARAM_ERROR, "MAC：" + bindDevice.getMac() + " 不存在，绑定失败！", true);
                }

            }
        }

        //进行设备名称的批量更新
        this.deviceMapper.updateBatch(devicePoList);
        //进行设备、客户、用户关系的绑定
        this.deviceCustomerUserRelationMapper.insertBatch(deviceCustomerUserRelationPoList);
        //进行设备的批量绑定
        Boolean ret = this.deviceTeamItemMapper.insertBatch(deviceTeamItemPoList) > 0;
        if (ret) {
            return new ApiResponse<>(RetCode.OK, "绑定成功", true);
        } else {
            return new ApiResponse<>(RetCode.ERROR, "绑定失败", false);
        }
    }

    /**
     * 设备解绑(单个)
     *
     * @return
     */
    public ApiResponse<DevicePo> untieOneDeviceToUser(DeviceUnbindRequest.deviceVo deviceVo) {//设备解绑 todo

        try {
            User user = userService.getCurrentUser();
            Integer deviceId = deviceVo.deviceId;
            String mac = deviceVo.mac;
            if (null == deviceId || deviceId <= 0 || StringUtils.isBlank(mac)) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "参数不可为空");
            }

            //查询该设备详情 进行验证
            DevicePo queryDevicePo = deviceMapper.selectById(deviceId);

            if (null == queryDevicePo) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "当前设备不存在");
            } else if (!mac.equals(queryDevicePo.getMac())) {
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
            unbindDevicePo.setLastUpdateUser(user.getId());

            deviceMapper.updateById(unbindDevicePo);

            return new ApiResponse<>(RetCode.OK, "解绑成功", queryDevicePo);
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
            User user = userService.getCurrentUser();
            List<DeviceUnbindRequest.deviceVo> deviceVos = unbindRequest.deviceVos;
            if (deviceVos != null && deviceVos.size() > 0) {
                deviceVos.stream().forEach(deviceVo -> {
                    Integer deviceId = deviceVo.deviceId;
                    String mac = deviceVo.mac;
//                    DevicePo queryDevicePo = deviceMapper.selectById(deviceId);
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
                    unbindDevicePo.setLastUpdateUser(user.getId());

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
            User user = userService.getCurrentUser();
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
                    updateDevicePo.setLastUpdateUser(deviceVo.getLastUpdateUser());

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
            User user = userService.getCurrentUser();
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
                    updateDevicePo.setLastUpdateUser(user.getId());

                    deviceMapper.updateById(updateDevicePo);

                });
            }
            return new ApiResponse<>(RetCode.OK, "启用成功");
        } catch (Exception e) {
            log.error("设备启用失败-{}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备启用失败");
        }

    }

    public ApiResponse<List<DeviceShareListVo>> queryShareList(Integer deviceId) {
        List<DeviceShareListVo> deviceShareListVoList = new ArrayList<>();
        //查询设备的绑定情况
        List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPoList = this.deviceCustomerUserRelationMapper.queryByDeviceId(deviceId);
        //
        if (null != deviceCustomerUserRelationPoList && 0 < deviceCustomerUserRelationPoList.size()) {
            //查询主绑人
            DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = deviceCustomerUserRelationPoList.get(0);
            CustomerUserPo masterUserPo = this.customerUserMapper.selectByOpenId(deviceCustomerUserRelationPo.getOpenId());
            DeviceShareListVo masterPo = new DeviceShareListVo();
            //主绑定人信息
            masterPo.setUserId(masterUserPo.getId());
            masterPo.setOpenId(masterUserPo.getOpenId());
            masterPo.setHeadImg(masterUserPo.getHeadimgurl());
            masterPo.setNickname(masterUserPo.getNickname());
            DeviceTeamItemPo masterTeamItemPo = this.deviceTeamItemMapper.selectByJoinId(deviceId, masterUserPo.getId());
            masterPo.setJoinTime(masterTeamItemPo.getCreateTime());
            deviceShareListVoList.add(masterPo);
            //查询设备的共享人信息,筛选主绑定人
            List<DeviceTeamItemPo> deviceTeamItemPoList = this.deviceTeamItemMapper.selectItemsByDeviceId(deviceId);
            List<DeviceTeamItemPo> subUserTeamItemPoList = deviceTeamItemPoList.stream().filter(eachPo -> {
                List<String> openIdsList = deviceCustomerUserRelationPoList.stream().map(userEachPo -> userEachPo.getOpenId()).collect(Collectors.toList());
                Integer subUserId = eachPo.getUserId();
                CustomerUserPo customerUserPo = this.customerUserMapper.selectById(subUserId);
                if (!openIdsList.contains(customerUserPo.getOpenId())) {
                    return true;
                }
                return false;
            }).sorted(Comparator.comparing(DeviceTeamItemPo::getCreateTime)).collect(Collectors.toList());
            //返回最终结果
            List<DeviceShareListVo> subUserVo = subUserTeamItemPoList.stream().map(eachPo -> {
                Integer subUserId = eachPo.getUserId();
                CustomerUserPo customerUserPo = this.customerUserMapper.selectByUserId(subUserId);
                DeviceShareListVo deviceShareVo = new DeviceShareListVo();
                deviceShareVo.setUserId(customerUserPo.getId());
                deviceShareVo.setNickname(customerUserPo.getNickname());
                deviceShareVo.setJoinTime(eachPo.getCreateTime());
                deviceShareVo.setOpenId(customerUserPo.getOpenId());
                deviceShareVo.setHeadImg(customerUserPo.getHeadimgurl());
                deviceShareVo.setStatus(eachPo.getStatus() == 1 ? true : false);
                return deviceShareVo;
            }).collect(Collectors.toList());
            return new ApiResponse<>(RetCode.OK, "查询授权列表成功", subUserVo);
        } else {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "设备尚未被绑定");
        }
    }

    public ApiResponse<Boolean> updateRelation(UpdateShareRequest updateShareRequest) throws Exception {
        //查询设备状态
        DevicePo devicePo = this.deviceMapper.selectById(updateShareRequest.getDeviceId());
        if (null == devicePo) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "设备不存在或已被删除");
        }
        //查询设备的绑定状态
        DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = this.deviceCustomerUserRelationMapper.selectByDeviceId(updateShareRequest.getDeviceId());
        if (null == deviceCustomerUserRelationPo) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "设备尚未被绑定");
        }
        //查询授权用户信息
        CustomerUserPo customerUserPo = this.customerUserMapper.selectByOpenId(updateShareRequest.getOpenId());
        if (null == customerUserPo) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "授权用户不存在");
        }
        DeviceTeamItemPo queryItemPo = new DeviceTeamItemPo();
        queryItemPo.setUserId(customerUserPo.getId());
        queryItemPo.setDeviceId(updateShareRequest.getDeviceId());
        queryItemPo.setStatus(null);
        List<DeviceTeamItemPo> deviceTeamItemPoList = deviceTeamMapper.queryTeamItems(queryItemPo);
        if (null == deviceTeamItemPoList || 0 == deviceTeamItemPoList.size()) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "授权用户尚未拥有此设备");
        }
        DeviceTeamItemPo deviceTeamItemPo = deviceTeamItemPoList.get(0);
        this.deviceTeamItemMapper.updateStatus(deviceTeamItemPo.getDeviceId(), deviceTeamItemPo.getUserId(), updateShareRequest.getStatus());
        return new ApiResponse<>(RetCode.OK, "授权成功", true);
    }

    public ApiResponse<Boolean> updateAllRelation(UpdateShareRequest updateShareRequest) throws Exception {
        List<DeviceTeamItemPo> deviceTeamItemPoList = deviceTeamItemMapper.selectItemsByDeviceId(updateShareRequest.getDeviceId());
        if (null == deviceTeamItemPoList || 0 == deviceTeamItemPoList.size()) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "授权用户尚未拥有此设备");
        }
        List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPoList = deviceCustomerUserRelationMapper.queryByDeviceId(updateShareRequest.getDeviceId());
        if (null == deviceCustomerUserRelationPoList || 0 == deviceCustomerUserRelationPoList.size()) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "设备尚未被绑定");
        }
        List<DeviceTeamItemPo> subUserTeamItemPoList = deviceTeamItemPoList.stream().filter(eachPo -> {
            List<String> openIdsList = deviceCustomerUserRelationPoList.stream().map(deviceCustomerUserRelationPo -> deviceCustomerUserRelationPo.getOpenId()).collect(Collectors.toList());
            Integer userId = eachPo.getUserId();
            CustomerUserPo customerUserPo = customerUserMapper.selectById(userId);
            if (!openIdsList.contains(customerUserPo.getOpenId())) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        //从设备组关系表中筛选除主绑定人之外的用户
        subUserTeamItemPoList.stream().forEach(eachPo -> {
            eachPo.setStatus(updateShareRequest.getStatus());
        });
        this.deviceTeamItemMapper.updateBatch(subUserTeamItemPoList);
        return new ApiResponse<>(RetCode.OK, "全部授权成功", true);
    }

    public ApiResponse<Boolean> clearRelation(UpdateShareRequest updateShareRequest) throws Exception {
        DeviceTeamItemPo queryItemPo = new DeviceTeamItemPo();
        CustomerUserPo beClearCustomerUserPo = customerUserMapper.selectByOpenId(updateShareRequest.getOpenId());
        queryItemPo.setUserId(beClearCustomerUserPo.getId());
        queryItemPo.setDeviceId(updateShareRequest.getDeviceId());
        queryItemPo.setStatus(null);
        List<DeviceTeamItemPo> deviceTeamItemPoList = deviceTeamMapper.queryTeamItems(queryItemPo);
        if (deviceTeamItemPoList.size() == 0 || null == deviceTeamItemPoList) {
            log.info("被删除用户无此设备，deviceId={}", updateShareRequest.getDeviceId());
            return new ApiResponse<>(RetCode.PARAM_ERROR, "被删除用户无此设备");
        }
        DeviceTeamItemPo deviceTeamItemPo = deviceTeamItemPoList.get(0);
        this.deviceTeamItemMapper.deleteByJoinId(deviceTeamItemPo.getDeviceId(), deviceTeamItemPo.getUserId());
        return new ApiResponse<>(RetCode.OK, "清除分享成功", true);
    }

    public ApiResponse<Boolean> clearAllRelation(UpdateShareRequest updateShareRequest) throws Exception {
        DeviceTeamItemPo queryItemPo = new DeviceTeamItemPo();
        //查询设备的绑定状态
        List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPoList = deviceCustomerUserRelationMapper.queryByDeviceId(updateShareRequest.getDeviceId());
        if (null == deviceCustomerUserRelationPoList || 0 == deviceCustomerUserRelationPoList.size()) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "设备尚未被绑定");
        }
        queryItemPo.setDeviceId(updateShareRequest.getDeviceId());
        queryItemPo.setStatus(null);
        List<DeviceTeamItemPo> deviceTeamItemPoList = deviceTeamMapper.queryTeamItems(queryItemPo);
        if (null != deviceTeamItemPoList && 0 < deviceTeamItemPoList.size()) {
            //筛选除主绑人之外的用户
            List<DeviceTeamItemPo> subUserTeamItemPoList = deviceTeamItemPoList.stream().filter(eachPo -> {
                List<String> openIdsList = deviceCustomerUserRelationPoList.stream().map(deviceCustomerUserRelationPo -> deviceCustomerUserRelationPo.getOpenId()).collect(Collectors.toList());
                Integer userId = eachPo.getUserId();
                CustomerUserPo customerUserPo = customerUserMapper.selectById(userId);
                if (!openIdsList.contains(customerUserPo.getOpenId())) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            this.deviceTeamItemMapper.deleteBatch(subUserTeamItemPoList);
        } else {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "当前设备下无已分享设备");
        }
        return new ApiResponse<>(RetCode.OK, "删除分享（全部）成功", true);
    }

    /**
     * 查询主设备下的从设备
     *
     * @param deviceId
     * @return
     * @throws Exception
     */
    public ApiResponse<List<DevicePo>> queryChildDevice(Integer deviceId) throws Exception {
        List<DevicePo> devicePoList = this.deviceMapper.selectChildDeviceListByHostDeviceId(deviceId);
        if (null != devicePoList && 0 < devicePoList.size()) {
            return new ApiResponse<>(RetCode.OK, "查询从设备成功", devicePoList);
        } else {
            return new ApiResponse<>(RetCode.OK, "当前设备无从设备", null);
        }
    }

    /**
     * 查询用户
     *
     * @param customerId
     * @return
     */
    public List<CustomerUserPo> queryUser(Integer customerId) {
        List<CustomerUserPo> customerUserPoList = this.customerUserMapper.selectByCustomerId(customerId);
        return customerUserPoList;
    }


    /**
     * 查询设备位置
     *
     * @param deviceId
     * @return
     * @throws Exception
     */
    public ApiResponse<DeviceLocationVo> queryDeviceLocation(Integer deviceId) throws Exception {
        DeviceLocationVo locationVo = new DeviceLocationVo();
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        if (devicePo != null) {
            if (StringUtils.isEmpty(devicePo.getLocation())) {
                JSONObject locationJson = locationUtils.getLocation(devicePo.getIp(), true);
                if (locationJson != null) {
                    if (locationJson.containsKey("content")) {
                        JSONObject content = locationJson.getJSONObject("content");
                        if (content != null) {
                            if (content.containsKey("address_detail")) {
                                JSONObject addressDetail = content.getJSONObject("address_detail");
                                if (addressDetail != null) {
                                    locationVo.setProvince(addressDetail.getString("province"));
                                    locationVo.setCity(addressDetail.getString("city"));
                                    locationVo.setArea(locationVo.getCity());
                                    locationVo.setLocation(locationVo.getProvince() + "," + locationVo.getCity());
                                }
                            }
                            if (content.containsKey("point")) {
                                JSONObject point = content.getJSONObject("point");
                                if (point != null) {
                                    locationVo.setPointX(point.getString("x"));
                                    locationVo.setPointY(point.getString("y"));
                                }
                            }

                        }
                    }
                }
            } else {
                String[] locationArray = devicePo.getLocation().split(",");
                locationVo.setArea(Joiner.on(" ").join(locationArray));
                locationVo.setLocation(devicePo.getLocation());
            }
            return new ApiResponse<>(RetCode.OK, "设备位置查询成功", locationVo);
        } else {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该设备不存在", null);
        }

    }

    public ApiResponse<List<DeviceLocationVo>> queryDeviceLocationInGroup(List<Integer> deviceIdList) throws Exception {
        List<DeviceLocationVo> deviceLocationVoList = new ArrayList<>();
        for(Integer deviceId : deviceIdList){
            DeviceLocationVo deviceLocationVo = new DeviceLocationVo();
            ApiResponse<DeviceLocationVo> deviceLocationVoRtn = queryDeviceLocation(deviceId);
            if(RetCode.OK == deviceLocationVoRtn.getCode()){
                deviceLocationVo = deviceLocationVoRtn.getData();
            }else {
                deviceLocationVo.setArea("无此设备区域数据");
                deviceLocationVo.setLocation("无此设备位置数据");
            }
            deviceLocationVoList.add(deviceLocationVo);
        }
        return new ApiResponse<>(RetCode.OK,"查询集群中设备位置成功",deviceLocationVoList);
    }

    /**
     * 查询天气
     *
     * @param deviceId
     * @return
     */
    public ApiResponse<DeviceWeatherVo> queryDeviceWeather(Integer deviceId) {
        DeviceWeatherVo deviceWeatherVo = new DeviceWeatherVo();
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        JSONObject weatherJson = locationUtils.getWeather(devicePo.getIp(), true);
        if (weatherJson != null) {
            if (weatherJson.containsKey("result")) {
                JSONObject result = weatherJson.getJSONObject("result");
                if (result != null) {
                    deviceWeatherVo.setOuterHum(result.getString("humidity"));
                    deviceWeatherVo.setOuterPm(result.getString("aqi"));
                    deviceWeatherVo.setOuterTem(result.getString("temperature_curr"));
                    deviceWeatherVo.setWeather(result.getString("weather_curr"));
                }
            }
        }
        return new ApiResponse<>(RetCode.OK, "设备天气查询成功", deviceWeatherVo);
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
            if (null != devicePo && CommonConstant.STATUS_DEL != devicePo.getStatus()) {
                return devicePo;
            } else {
                devicePo = null;
            }
        }
        return devicePo;
    }

    public DevicePo queryDeviceByName(List<DeviceCreateOrUpdateRequest.DeviceUpdateList> deviceList) {
        DevicePo devicePo = null;
        for (DeviceCreateOrUpdateRequest.DeviceUpdateList device : deviceList) {
            devicePo = deviceMapper.selectByName(device.getName());
            if (null != devicePo && CommonConstant.STATUS_DEL != devicePo.getStatus()) {
                return devicePo;
            } else {
                devicePo = null;
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
    public DevicePo isDeviceHasCustomer(List<DeviceQueryRequest.DeviceQueryList> deviceList) throws Exception {
        for (DeviceQueryRequest.DeviceQueryList device : deviceList) {
            DevicePo devicePo = deviceMapper.selectDeviceCustomerRelationByMac(device.getMac());
            //如果当前设备已被分配则返回错误
            if (null != devicePo) {
                return devicePo;
            }
        }
        return null;
    }

    /**
     * 查询当前用户下租的信息，若无组则加载自定义组
     *
     * @param openId
     * @return
     * @throws Exception
     */
    public ApiResponse<List<DeviceTeamPo>> queryTeamInfoByUser(String openId) throws Exception {
        //加载该用户名下未删除的自定义组
        //只查询属于当前客户下的用户的组
        Integer customerId = this.customerService.obtainCustomerId(false);
        List<DeviceTeamPo> deviceTeamPoList = this.deviceTeamMapper.selectByUserOpenId(openId,customerId);
        DeviceTeamPo deviceTeamPo = new DeviceTeamPo();
        CustomerUserPo customerUserPo = this.customerUserMapper.selectByOpenId(openId);
        if (null == deviceTeamPoList || 0 == deviceTeamPoList.size()) {
            deviceTeamPoList.clear();
            //若没有自定义组则加载默认组
            WxConfigPo wxConfigPo = this.wxConfigMapper.selectConfigByCustomerId(customerUserPo.getCustomerId());
            if (wxConfigPo != null) {
                deviceTeamPo.setName(wxConfigPo.getDefaultTeamName());
                deviceTeamPo.setId(DeviceConstant.DEFAULT_TEAM_ID);
                deviceTeamPoList.add(deviceTeamPo);
            }

        }
        return new ApiResponse<>(RetCode.OK, "查询用户组成功", deviceTeamPoList);
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
    public ApiResponse<Integer> createWxDeviceIdPools(Integer customerId, String productId, Integer addCount) {
        Boolean ret = true;
        CustomerPo customerPo = customerMapper.selectById(customerId);
        int correctCount = 0;
        //获取数据
        if (customerPo != null) {
            String appId = customerPo.getAppid();
            String appSecret = customerPo.getAppsecret();
            if (null != addCount && addCount > 0) {
                List<DeviceIdPoolPo> deviceIdPoolPos = new ArrayList<>();

                for (int m = 0; m < addCount; m++) {
                    ApiResponse<JSONObject> result = obtainDeviceInfo(appId, appSecret, customerId.toString(), productId);
                    //当第一个就开始 出现错误时，则直接返回结果
                    if (m == 0 && RetCode.OK != result.getCode()) {
                        return new ApiResponse<>(RetCode.ERROR, result.getMsg(), correctCount);
                    }
                    JSONObject jsonObject = result.getData();
                    if (jsonObject != null) {
                        String wxDeviceId = jsonObject.getString("deviceid");
                        String wxDevicelicence = jsonObject.getString("devicelicence");
                        String wxQrticket = jsonObject.getString("qrticket");

                        if (wxDeviceId != null) {
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

                            correctCount++;
                        } else {
                            log.error("wxDeviceId 或 wxDevicelicence为空 = {}", jsonObject);
                        }

                    } else {
                        log.error("createWxDeviceIdPool.jsonObject = {}", false);
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


        return new ApiResponse<>(correctCount);
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
                //如果 因为 access_token过期，则重新获取access_token，并回调自己。
                if (jsonObject.containsKey("errcode") && CommonConstant.ZERO != jsonObject.get("errcode")) {
                    if (RetCode.WX_RROR_40001.equals(jsonObject.get("errcode")) || RetCode.WX_RROR_40001 == jsonObject.get("errcode")) {
                        accessToken = wechartUtil.getAccessToken(appId, appSecret, customerId, true);
                        obtainDeviceInfo(appId, appSecret, customerId, productId);
                    }
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


    /**
     * 查询设备功能项值
     *
     * @param abilityIds
     * @return
     */
    public List<DeviceAbilityVo.DeviceAbilitysVo> queryDetailAbilitysValue(Integer deviceId, List<Integer> abilityIds) {
        Integer modelId = deviceMapper.selectById(deviceId).getModelId();
        List<DeviceAbilityVo.DeviceAbilitysVo> deviceAbilitysVoList = new ArrayList<>();
        Map<Object, Object> datas = stringRedisTemplate.opsForHash().entries("sensor2." + deviceId);
        Map<Object, Object> controlDatas = stringRedisTemplate.opsForHash().entries("control2." + deviceId);
        //缓存功能项集
//        List<DeviceAbilityPo> deviceAbilityPoCaches = deviceAbilityMapper.selectList(new DeviceAbilityPo(), 10000, 0);
//        Map<Integer,DeviceAbilityPo> deviceAbilityPoMap = deviceAbilityPoCaches.stream().collect(Collectors.toMap(DeviceAbilityPo::getId, a -> a,(k1, k2)->k1));
        for (Integer abilityId : abilityIds) {
            List<DeviceModelAbilityOptionPo> deviceModelAbilityOptionPoCaches = new ArrayList<>();
            Map<Integer, DeviceModelAbilityOptionPo> deviceModelAbilityOptionPoMap = new HashMap<>();
            DeviceAbilityPo deviceabilityPo = deviceAbilityMapper.selectById(abilityId);
            String dirValue = deviceabilityPo.getDirValue();
            Integer abilityType = deviceabilityPo.getAbilityType();

            DeviceAbilityVo.DeviceAbilitysVo deviceAbilitysVo = new DeviceAbilityVo.DeviceAbilitysVo();
            deviceAbilitysVo.setAbilityName(deviceabilityPo.getAbilityName());
            deviceAbilitysVo.setId(abilityId);
            deviceAbilitysVo.setAbilityType(abilityType);
            deviceAbilitysVo.setDirValue(dirValue);
            switch (abilityType) {
                case DeviceAbilityTypeContants.ability_type_text:
                    deviceAbilitysVo.setCurrValue(getData(datas, dirValue));
                    deviceAbilitysVo.setUnit(deviceabilityPo.getRemark());
                    break;
                case DeviceAbilityTypeContants.ability_type_single:
                    List<DeviceAbilityOptionPo> deviceabilityOptionPos = deviceAbilityOptionMapper.selectActiveOptionsByAbilityId(abilityId);

                    //缓存
                    deviceModelAbilityOptionPoCaches = deviceModelAbilityOptionMapper.queryByModelIdAbilityId(modelId ,abilityId );
                    deviceModelAbilityOptionPoMap = deviceModelAbilityOptionPoCaches.stream().collect(Collectors.toMap(DeviceModelAbilityOptionPo::getAbilityOptionId, a -> a, (k1, k2) -> k1));
                    String optionValue = getData(controlDatas, dirValue);
                    List<DeviceAbilityVo.abilityOption> abilityOptionList = new ArrayList<>();
                    for (DeviceAbilityOptionPo deviceabilityOptionPo : deviceabilityOptionPos) {
                        DeviceModelAbilityOptionPo deviceModelAbilityOptionPo = deviceModelAbilityOptionPoMap.get(deviceabilityOptionPo.getId());
                        if(deviceModelAbilityOptionPo == null){
                            continue;
                        }
                        DeviceAbilityVo.abilityOption abilityOption = new DeviceAbilityVo.abilityOption();
                        abilityOption.setDirValue(deviceabilityOptionPo.getOptionValue());
                        if (optionValue.equals(deviceabilityOptionPo.getOptionValue())) {
                            deviceAbilitysVo.setCurrValue(deviceabilityOptionPo.getOptionValue());
                            abilityOption.setIsSelect(CommonConstant.STATUS_IS_YES);
                        } else {
                            abilityOption.setIsSelect(CommonConstant.STATUS_IS_NO);
                        }
                        abilityOptionList.add(abilityOption);
                    }
                    deviceAbilitysVo.setAbilityOptionList(abilityOptionList);
                    break;
                case DeviceAbilityTypeContants.ability_type_checkbox:
                    List<DeviceAbilityOptionPo> deviceabilityOptionPos1 = deviceAbilityOptionMapper.selectOptionsByAbilityId(abilityId);
                    //缓存
                    deviceModelAbilityOptionPoCaches = deviceModelAbilityOptionMapper.queryByModelIdAbilityId(modelId ,abilityId );
                    deviceModelAbilityOptionPoMap = deviceModelAbilityOptionPoCaches.stream().collect(Collectors.toMap(DeviceModelAbilityOptionPo::getAbilityOptionId, a -> a, (k1, k2) -> k1));
                    List<DeviceAbilityVo.abilityOption> abilityOptionList1 = new ArrayList<>();
                    for (DeviceAbilityOptionPo deviceabilityOptionPo : deviceabilityOptionPos1) {
                        DeviceModelAbilityOptionPo deviceModelAbilityOptionPo = deviceModelAbilityOptionPoMap.get(deviceabilityOptionPo.getId());
                        if(deviceModelAbilityOptionPo == null){
                            continue;
                        }
                        String targetOptionValue = deviceabilityOptionPo.getOptionValue();
                        String finalOptionValue = getData(controlDatas, targetOptionValue);
                        DeviceAbilityVo.abilityOption abilityOption = new DeviceAbilityVo.abilityOption();
                        abilityOption.setDirValue(deviceabilityOptionPo.getOptionValue());
                        if (Integer.valueOf(finalOptionValue) == 1) {
                            abilityOption.setIsSelect(CommonConstant.STATUS_IS_YES);
                        } else {
                            abilityOption.setIsSelect(CommonConstant.STATUS_IS_NO);
                        }
                        abilityOptionList1.add(abilityOption);
                    }
                    deviceAbilitysVo.setAbilityOptionList(abilityOptionList1);
                    break;
                case DeviceAbilityTypeContants.ability_type_threshhold:
                    deviceAbilitysVo.setCurrValue(getData(controlDatas, dirValue));
                    deviceAbilitysVo.setUnit(deviceabilityPo.getRemark());
                    break;
                case DeviceAbilityTypeContants.ability_type_threshholdselect:
                    DeviceAbilityPo deviceAbilityPo = deviceAbilityMapper.selectById(abilityId);
                    if (deviceAbilityPo.getDirValue().equals("-1")) {//滤网临时妥协办法，后期再想更优方式
                        List<DeviceAbilityOptionPo> deviceabilityOptionPos5 = deviceAbilityOptionMapper.selectOptionsByAbilityId(abilityId);
                        List<DeviceAbilityVo.abilityOption> abilityOptionList5 = new ArrayList<>();
                        for (DeviceAbilityOptionPo deviceabilityOptionPo : deviceabilityOptionPos5) {
                            String optionValue5 = getData(controlDatas, deviceabilityOptionPo.getOptionValue());
                            DeviceAbilityVo.abilityOption abilityOption = new DeviceAbilityVo.abilityOption();
                            abilityOption.setDirValue(deviceabilityOptionPo.getOptionValue());
                            abilityOption.setCurrValue(optionValue5);
                            abilityOptionList5.add(abilityOption);
                        }
                        deviceAbilitysVo.setAbilityOptionList(abilityOptionList5);
                    } else {
                        List<DeviceAbilityOptionPo> deviceabilityOptionPos5 = deviceAbilityOptionMapper.selectOptionsByAbilityId(abilityId);
                        //缓存
                        deviceModelAbilityOptionPoCaches = deviceModelAbilityOptionMapper.queryByModelIdAbilityId(modelId ,abilityId );
                        deviceModelAbilityOptionPoMap = deviceModelAbilityOptionPoCaches.stream().collect(Collectors.toMap(DeviceModelAbilityOptionPo::getAbilityOptionId, a -> a, (k1, k2) -> k1));
                        String optionValue5 = getData(controlDatas, dirValue);
                        List<DeviceAbilityVo.abilityOption> abilityOptionList5 = new ArrayList<>();
                        for (DeviceAbilityOptionPo deviceabilityOptionPo : deviceabilityOptionPos5) {
                            DeviceModelAbilityOptionPo deviceModelAbilityOptionPo = deviceModelAbilityOptionPoMap.get(deviceabilityOptionPo.getId());
                            if(deviceModelAbilityOptionPo == null){
                                continue;
                            }
                            DeviceAbilityVo.abilityOption abilityOption = new DeviceAbilityVo.abilityOption();
                            abilityOption.setDirValue(deviceabilityOptionPo.getOptionValue());
                            if (optionValue5.equals(deviceabilityOptionPo.getOptionValue())) {
                                abilityOption.setIsSelect(CommonConstant.STATUS_IS_YES);
                            } else {
                                abilityOption.setIsSelect(CommonConstant.STATUS_IS_NO);
                            }
                            abilityOptionList5.add(abilityOption);
                        }
                        deviceAbilitysVo.setAbilityOptionList(abilityOptionList5);
                    }
                    break;
                default:
                    break;

            }
            deviceAbilitysVoList.add(deviceAbilitysVo);
        }
        //添加空气质量判定
        if (datas.containsKey(SensorTypeEnums.PM25_IN.getCode())) {
            DeviceAbilityVo.DeviceAbilitysVo deviceAbilitysVo = new DeviceAbilityVo.DeviceAbilitysVo();
            deviceAbilitysVo.setDirValue("0");
            deviceAbilitysVo.setAbilityName("空气质量");

            String data = getData(datas, SensorTypeEnums.PM25_IN.getCode());
            if (StringUtils.isNotEmpty(data)) {
                Integer diData = Integer.valueOf(data);
                if (diData >= 0 && diData <= 35) {
                    deviceAbilitysVo.setCurrValue("优");
                } else if (diData > 35 && diData <= 75) {
                    deviceAbilitysVo.setCurrValue("良");
                } else if (diData > 75 && diData <= 150) {
                    deviceAbilitysVo.setCurrValue("中");
                } else {
                    deviceAbilitysVo.setCurrValue("差");
                }
            } else {
                deviceAbilitysVo.setCurrValue("优");
            }
            deviceAbilitysVoList.add(deviceAbilitysVo);
        }
        return deviceAbilitysVoList;
    }

    private String getData(Map<Object, Object> map, String key) {
        if (map.containsKey(key)) {
            return (String) map.get(key);
        }
        return "0";
    }


    /**
     * 设备操作指令
     * @param deviceFuncVo
     * @param operType
     * @return
     */
    public ApiResponse<String> sendFunc (DeviceFuncRequest deviceFuncVo, Integer operType) {

        //获取当前登录的用户
        User user = this.userService.getCurrentUser();

        DevicePo devicePo = deviceMapper.selectById(deviceFuncVo.getDeviceId());
        if (devicePo != null) {
            Integer deviceId = devicePo.getId();
            String topic = "/down2/control/" + deviceId;
            String requestId = UUID.randomUUID().toString().replace("-", "");
            /*操作日志*/
            DeviceOperLogPo deviceOperLogPo = new DeviceOperLogPo();
            deviceOperLogPo.setFuncId(deviceFuncVo.getFuncId());
            deviceOperLogPo.setDeviceId(deviceId);
            deviceOperLogPo.setOperType(operType);
            deviceOperLogPo.setOperUserId(user.getId());
            deviceOperLogPo.setFuncValue(deviceFuncVo.getValue());
            deviceOperLogPo.setRequestId(requestId);
            deviceOperLogPo.setCreateTime(System.currentTimeMillis());
            deviceOperLogMapper.insert(deviceOperLogPo);

            FuncListMessage funcListMessage = new FuncListMessage();
            funcListMessage.setMsg_type("control");
            funcListMessage.setMsg_id(requestId);
            FuncListMessage.FuncItemMessage funcItemMessage = new FuncListMessage.FuncItemMessage();
            funcItemMessage.setType(deviceFuncVo.getFuncId());
            funcItemMessage.setValue(deviceFuncVo.getValue());
            funcListMessage.setDatas(Lists.newArrayList(funcItemMessage));
            mqttSendService.sendMessage(topic, JSON.toJSONString(funcListMessage));
            stringRedisTemplate.opsForHash().put("control2." + deviceId, funcItemMessage.getType(), String.valueOf(funcItemMessage.getValue()));
            return new ApiResponse<>(RetCode.OK,"设备开/设备关成功",requestId);
        }else{
            return new ApiResponse<>(RetCode.PARAM_ERROR,"该设备不存在");
        }


    }
}