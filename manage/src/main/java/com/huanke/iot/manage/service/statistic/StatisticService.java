package com.huanke.iot.manage.service.statistic;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.dao.statstic.StatsticCustomerUserLiveMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.statstic.StatsticCustomerUserLivePo;
import com.huanke.iot.base.util.CommonUtil;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.vo.request.device.operate.DeviceHomePageStatisticVo;
import com.huanke.iot.manage.vo.request.device.operate.DeviceLocationCountRequest;
import com.huanke.iot.manage.vo.response.device.customer.CustomerUserVo;
import com.huanke.iot.manage.vo.response.device.operate.DeviceLocationCountVo;
import com.huanke.iot.manage.vo.response.device.operate.DeviceOnlineStatVo;
import com.huanke.iot.manage.vo.response.device.operate.DeviceStatisticsVo;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceModelVo;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceTypeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;


@Repository
@Slf4j
public class StatisticService {


    @Autowired
    private CustomerUserMapper customerUserMapper;

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Autowired
    private DeviceModelMapper deviceModelMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private StatsticCustomerUserLiveMapper statsticCustomerUserLiveMapper;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CommonUtil commonUtil;


    /**
     * 首页面板-统计用户
     *
     * @param
     * @return
     */

    public List<CustomerUserVo> selectUserCountPerMonth() {
        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        int preYear = nowYear-1;
        List rtnList = new ArrayList();
        Integer customerId = customerService.obtainCustomerId(false);
        //今年的用户数据
        List nowUserConutList = customerUserMapper.selectCustomerUserCount(nowYear,customerId);
        //去年的用户数据
        List preYearUserList = customerUserMapper.selectCustomerUserCount(preYear,customerId);
        //上个月的用户数量
        Long prevMonthCount = new Long(0);
        //去年的上个月的用户数量
        Long prevYearMonthCount = new Long(0);

        String addPercent = "0.00%";
        for(int i=1;i<=12;i++){
            CustomerUserVo.CustomerUserCountVo customerUserCountVo = new CustomerUserVo.CustomerUserCountVo();
            String nowMonth = i +"月";
            if(i<10){
                nowMonth = "0"+i+"月";
            }
            //今年某月的用户量
            Long userCount = new Long(0);
            //今年某月用户增长量
            Long addCount = new Long(0);
            //去年某月用户量
            Long preYearUserCount =  new Long(0);
            //去年某月用户增长量
            Long preYearAddCount =  new Long(0);

            if(nowUserConutList!=null&&nowUserConutList.size()>0){
                for(int m=0;m<nowUserConutList.size();m++){
                    Map tempMap = (Map)nowUserConutList.get(m);
                    String tempMonth = (String)tempMap.get("userMonth");
                    Long tempUserCount = (Long)tempMap.get("userCount");
                    if(tempMonth.equals(nowMonth)){
                        userCount = tempUserCount;
                        addCount = userCount-prevMonthCount;
                        prevMonthCount = userCount;
                        break;
                    }
                }
            }

            if(preYearUserList!=null&&preYearUserList.size()>0){
                for(int m=0;m<preYearUserList.size();m++){
                    Map preMap = (Map)preYearUserList.get(m);
                    String tempMonth = (String)preMap.get("userMonth");
                    Long tempUserCount = (Long)preMap.get("userCount");
                    if(tempMonth.equals(nowMonth)){
                        preYearUserCount = tempUserCount;
                        preYearAddCount = preYearUserCount-prevYearMonthCount;

                        prevYearMonthCount = preYearUserCount;
                        break;
                    }
                }
            }

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setMinimumFractionDigits(2);
            //如果除数为0
            if(preYearAddCount==0){
                //被除数为0
                if(addCount==0){
                    addPercent = "--";
                }else{
                    addPercent = df.format(addCount * 100.00 / 1) + "%";
                }
            }else {
                addPercent = df.format((addCount-preYearAddCount) * 100.00 / preYearAddCount) + "%";
            }

            customerUserCountVo.setMonth(nowMonth);
            customerUserCountVo.setAddCount(addCount);
            customerUserCountVo.setUserCount(userCount);
            customerUserCountVo.setAddPercent(addPercent);
            rtnList.add(customerUserCountVo);
        }

        return rtnList;
    }

    /**
     * 首页大数据面板-设备类型统计
     *
     * @return
     */
    public List<DeviceTypeVo.DeviceTypePercent> selectTypePercentPerMonth() {

        List<DeviceTypeVo.DeviceTypePercent> deviceTypePercents = new ArrayList<DeviceTypeVo.DeviceTypePercent>();

        Integer customerId = customerService.obtainCustomerId(false);
        /*查询设备总量*/
        DevicePo queryDevicePo = new DevicePo();
        queryDevicePo.setStatus(CommonConstant.STATUS_YES);
        queryDevicePo.setCustomerId(customerId);
        Integer deviceTotal = deviceMapper.selectCount(queryDevicePo);

        if(deviceTotal!=null&&deviceTotal!=0){
            List deviceTypePercentList = deviceTypeMapper.selectTypePercent(customerId);

            if (deviceTypePercentList != null && deviceTypePercentList.size() > 0) {
                for(int i=0;i<deviceTypePercentList.size();i++){
                    DeviceTypeVo.DeviceTypePercent deviceTypePercent = new DeviceTypeVo.DeviceTypePercent();
                    Map deviceTypePercentMap = (Map)deviceTypePercentList.get(i);
                    Integer typeId = (Integer)deviceTypePercentMap.get("typeId");
                    String typeName = (String)deviceTypePercentMap.get("typeName");
                    Long deviceCount = (Long)deviceTypePercentMap.get("deviceCount");

                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    df.setMinimumFractionDigits(2);
                    String typePercent = df.format(deviceCount * 100.00 / deviceTotal) + "%";

                    deviceTypePercent.setTypeId(typeId);
                    deviceTypePercent.setTypeName(typeName);
                    deviceTypePercent.setDeviceCount(deviceCount);
                    deviceTypePercent.setTypePercent(typePercent);

                    deviceTypePercents.add(deviceTypePercent);
                }
            }
        }

        return deviceTypePercents;

    }

    /**
     * 首页大数据面板-设备型号统计
     *
     * @return
     */
    public List<DeviceModelVo.DeviceModelPercent> selectModelPercent() {

        List<DeviceModelVo.DeviceModelPercent> deviceTypePercents = new ArrayList<DeviceModelVo.DeviceModelPercent>();

        Integer customerId = customerService.obtainCustomerId(false);
        /*查询设备总量*/
        DevicePo queryDevicePo = new DevicePo();
        queryDevicePo.setStatus(CommonConstant.STATUS_YES);
        queryDevicePo.setCustomerId(customerId);
        Integer deviceTotal = deviceMapper.selectCount(queryDevicePo);

        if(deviceTotal!=null&&deviceTotal!=0){
            List deviceModelPercentList = deviceModelMapper.selectModelPercent(customerId);

            if (deviceModelPercentList != null && deviceModelPercentList.size() > 0) {
                for(int i=0;i<deviceModelPercentList.size();i++){
                    DeviceModelVo.DeviceModelPercent deviceTypePercent = new DeviceModelVo.DeviceModelPercent();
                    Map deviceTypePercentMap = (Map)deviceModelPercentList.get(i);
                    Integer modelId = (Integer)deviceTypePercentMap.get("modelId");
                    String modelName = (String)deviceTypePercentMap.get("modelName");
                    Long deviceCount = (Long)deviceTypePercentMap.get("deviceCount");

                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    df.setMinimumFractionDigits(2);
                    String modelPercent = df.format(deviceCount * 100.00 / deviceTotal) + "%";

                    deviceTypePercent.setModelId(modelId);
                    deviceTypePercent.setModelName(modelName);
                    deviceTypePercent.setDeviceCount(deviceCount);
                    deviceTypePercent.setModelPercent(modelPercent);

                    deviceTypePercents.add(deviceTypePercent);
                }
            }
            int needNum = 4;
            if(deviceTypePercents.size()>needNum) {
                deviceTypePercents.sort((x, y) -> Long.compare(y.getDeviceCount(), x.getDeviceCount()));
                deviceTypePercents = deviceTypePercents.subList(0, needNum-1);
                DeviceModelVo.DeviceModelPercent other = new DeviceModelVo.DeviceModelPercent();
                other.setModelName("其他");
                long dc = 0;
                for (DeviceModelVo.DeviceModelPercent temp :deviceTypePercents) {
                    dc+=temp.getDeviceCount();
                }
                other.setDeviceCount(deviceTotal - dc);
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                df.setMinimumFractionDigits(2);
                String modelPercent = df.format(other.getDeviceCount() * 100.00 / deviceTotal) + "%";
                other.setModelPercent(modelPercent);
                deviceTypePercents.add(other);
            }
        }
        return deviceTypePercents;

    }



    /**
     * 首页面板-统计设备
     *
     * @param
     * @return
     */

    public List<DeviceStatisticsVo> selectDeviceCountPerMonth() {
        List rtnList = new ArrayList();
        List nowDeviceConutList = new ArrayList();
        List preYearDeviceList = new ArrayList();
        Integer customerId = customerService.obtainCustomerId(false);

        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        int preYear = nowYear-1;

        if(customerId==null){
            //今年的设备数据
            nowDeviceConutList = deviceMapper.selectDeviceCount(nowYear,CommonConstant.STATUS_YES);
            //去年的设备数据
            preYearDeviceList = deviceMapper.selectDeviceCount(preYear,CommonConstant.STATUS_YES);
        }else{
            //今年的设备数据
            nowDeviceConutList = deviceMapper.selectDeviceCountByCustomer(nowYear,CommonConstant.STATUS_YES,customerId);
            //去年的设备数据
            preYearDeviceList = deviceMapper.selectDeviceCountByCustomer(preYear,CommonConstant.STATUS_YES,customerId);
        }

        //上个月的设备数量
        Long prevMonthCount = new Long(0);
        //去年的上个月的设备数量
        Long prevYearMonthCount = new Long(0);

        String addPercent = "0.00%";
        for(int i=1;i<=12;i++){
            DeviceStatisticsVo deviceStatisticsVo = new DeviceStatisticsVo();
            String nowMonth = i +"月";
            if(i<10){
                nowMonth = "0"+i+"月";
            }
            //今年某月的用户量
            Long deviceCount = new Long(0);
            //今年某月用户增长量
            Long addCount = new Long(0);
            //去年某月用户量
            Long preYearDeviceCount =  new Long(0);
            //去年某月用户增长量
            Long preYearAddCount =  new Long(0);

            if(nowDeviceConutList!=null&&nowDeviceConutList.size()>0){
                for(int m=0;m<nowDeviceConutList.size();m++){
                    Map tempMap = (Map)nowDeviceConutList.get(m);
                    String tempMonth = (String)tempMap.get("deviceMonth");
                    Long tempDeviceCount = (Long)tempMap.get("deviceCount");
                    if(tempMonth.equals(nowMonth)){
                        deviceCount = tempDeviceCount;
                        addCount = deviceCount-prevMonthCount;
                        prevMonthCount = deviceCount;
                        break;
                    }
                }
            }

            if(preYearDeviceList!=null&&preYearDeviceList.size()>0){
                for(int m=0;m<preYearDeviceList.size();m++){
                    Map preMap = (Map)preYearDeviceList.get(m);
                    String tempMonth = (String)preMap.get("deviceMonth");
                    Long tempDeviceCount = (Long)preMap.get("deviceCount");
                    if(tempMonth.equals(nowMonth)){
                        preYearDeviceCount = tempDeviceCount;
                        preYearAddCount = preYearDeviceCount-prevYearMonthCount;

                        prevYearMonthCount = preYearDeviceCount;
                        break;
                    }
                }
            }

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setMinimumFractionDigits(2);
            //如果除数为0
            if(preYearAddCount==0){
                //被除数为0
                if(addCount==0){
                    addPercent = "--";
                }else{
                    addPercent = df.format(addCount * 100.00 / 1) + "%";
                }
            }else {
                addPercent = df.format((addCount-preYearAddCount) * 100.00 / preYearAddCount) + "%";
            }

            deviceStatisticsVo.setMonth(nowMonth);
            deviceStatisticsVo.setAddCount(addCount);
            deviceStatisticsVo.setDeviceCount(deviceCount);
            deviceStatisticsVo.setAddPercent(addPercent);
            rtnList.add(deviceStatisticsVo);
        }

        return rtnList;
    }

    /**
     * 设备今日增长数
     * @return
     */
    public ApiResponse<Integer> selectDeviceByDay(){
        Integer customerId = this.customerService.obtainCustomerId(false);
        //获取当前系统时间戳
        Long endTime = System.currentTimeMillis();
        //获取当前日期的0点时间戳
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH));
        calendar.set(Calendar.DATE,calendar.get(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        Long startTime = calendar.getTimeInMillis();

        //查询当日0点时间戳至当前时间戳中的设备数据
        Integer newDeviceOfToday;
        //二级客户设备新增
        if(null!=customerId){
            newDeviceOfToday = this.deviceMapper.selectCustomerDataByTime(startTime,endTime,customerId);
        }
        //超管设备新增
        else{
            newDeviceOfToday = this.deviceMapper.selectDataByTime(startTime,endTime);
        }
        return new ApiResponse<>(RetCode.OK,"查询今日新增成功",newDeviceOfToday);
    }

    /**
     * 首页统计分析
     * @return
     */
    public ApiResponse<DeviceHomePageStatisticVo> queryHomePageStatistic(){

        DeviceHomePageStatisticVo deviceHomePageStatisticVo = new DeviceHomePageStatisticVo();
        /*当前域名*/
        String userHost = commonUtil.obtainSecondHost();
        /*当前域名的 客户主键*/
        Integer customerId = customerService.obtainCustomerId(false);
        /*统计设备数量*/
        DevicePo queryDevicePo = new DevicePo();
        queryDevicePo.setStatus(CommonConstant.STATUS_YES);
        queryDevicePo.setCustomerId(customerId);
        int totalDeviceCount = deviceMapper.selectCount(queryDevicePo);
        deviceHomePageStatisticVo.setDeviceTotalCount(totalDeviceCount);

        /*统计设备今日增长数量*/
        ApiResponse<Integer> todayDeviceAddCountRtn = selectDeviceByDay();
        deviceHomePageStatisticVo.setTodayDeviceAddCount(todayDeviceAddCountRtn.getData());

        DeviceOnlineStatVo deviceOnlineStatVo = queryCurrentOnline();
        deviceHomePageStatisticVo.setDeviceOnlineCount(deviceOnlineStatVo.getOnlineDevice());
        deviceHomePageStatisticVo.setDeviceOfflineCount(totalDeviceCount - deviceOnlineStatVo.getOnlineDevice());
        deviceHomePageStatisticVo.setDeviceOnlinePercent(deviceOnlineStatVo.getOnlinePercent());

        /*统计当前用户总人数*/
        int totalUserCount = customerUserMapper.selectUserCount(customerId);
        /*统计昨日用户增长数*/
        int preDayUserAddCount = selectUserCountByTime(customerId);
        /*统计今日活跃用户数*/
        int liveUserCount = selectLiveUserCountByTime(customerId);

        deviceHomePageStatisticVo.setTotalUserCount(totalUserCount);
        deviceHomePageStatisticVo.setPreDayUserAddCount(preDayUserAddCount);
        deviceHomePageStatisticVo.setTodayUserLiveCount(liveUserCount);


        return new ApiResponse<>(RetCode.OK,"首页统计成功",deviceHomePageStatisticVo);
    }


    /**
     * 获取 昨日用户增长数
     * @return
     */
    public Integer selectUserCountByTime(Integer customerId){

        //获取昨天的0点时间戳
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH));
        calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        Long startTime = calendar.getTimeInMillis();

        //获取昨日 23:59:59的时间戳
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        Long endTime = calendar.getTimeInMillis();

        int preDayUserAddCount = customerUserMapper.selectUserCountByTime(startTime,endTime,customerId);
        return preDayUserAddCount;
    }

    /**
     * 获取 今日活跃用户增长数
     * @return
     */
    public Integer selectLiveUserCountByTime(Integer customerId){

        //获取当前系统时间戳
        Long endTime = System.currentTimeMillis();
        //获取当前日期的0点时间戳
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH));
        calendar.set(Calendar.DATE,calendar.get(Calendar.DATE));
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        Long startTime = calendar.getTimeInMillis();

        int nowLiveUserCount = customerUserMapper.selectLiveUserCountByTime(startTime,endTime,customerId);
        return nowLiveUserCount;
    }

    /**
     * 查询 当前在线设备
     * @return
     */
    public DeviceOnlineStatVo queryCurrentOnline(){
        Integer customerId = this.customerService.obtainCustomerId(false);
        DevicePo queryPo =new DevicePo();
        //二级客户
        if(null != customerId){
            queryPo.setCustomerId(customerId);
        }
        //查询设备总数
        Integer totalDevice = this.deviceMapper.selectCount(queryPo);
        //查询在线设备总数
        queryPo.setOnlineStatus(DeviceConstant.ONLINE_STATUS_YES);
        Integer onlineDevice = this.deviceMapper.selectCount(queryPo);
        DecimalFormat df = new DecimalFormat("0.00");
        String onLinePercent = df.format((float)onlineDevice/totalDevice);
        DeviceOnlineStatVo deviceOnlineStatVo = new DeviceOnlineStatVo();
        deviceOnlineStatVo.setTotalDevice(totalDevice);
        deviceOnlineStatVo.setOnlineDevice(onlineDevice);
        deviceOnlineStatVo.setOfflineDevice(totalDevice-onlineDevice);
        deviceOnlineStatVo.setOnlinePercent(onLinePercent);
        return deviceOnlineStatVo;
    }

    /**
     * 查询 地址数量
     * @return
     */
    public ApiResponse<DeviceLocationCountVo> queryLocationCount(DeviceLocationCountRequest daeviceLocationCountRequest){
        Integer customerId = customerService.obtainCustomerId(false);
        DevicePo queryDevicePo = new DevicePo();
        queryDevicePo.setCustomerId(customerId);
        int totalDeviceCount = deviceMapper.selectCount(queryDevicePo);
        List<DevicePo> lists = deviceMapper.selectList(queryDevicePo,totalDeviceCount,0);
        Map<String ,Map<String,Map<String,Integer>>> locationCountMap = new HashMap<>();
        lists.stream().forEach(temp->{
            String[] temps = new String[4];
            if(temp.getLocation()!=null){
                temps = temp.getLocation().split(",");
            }
            List<String> locations = new ArrayList<>();
            for(int i =0 ;i< 3;i++){
                if(i>=temps.length||StringUtils.isEmpty(temps[i])){
                    locations.add("其他");
                    locations.add("其他");
                    locations.add("其他");
                    break;
                }else if("上海市".equals(temps[0])||"北京市".equals(temps[0])||"重庆市".equals(temps[0])||"天津市".equals(temps[0])) {
                    locations.add(temps[0]);
                    locations.add("市辖区");
                    locations.add(temps[2]);
                    break;
                }else{
                    locations.add(temps[i]);
                }
            }
            boolean flag = true;
            if(StringUtils.isNotEmpty(daeviceLocationCountRequest.getProvince())){
                flag = locations.get(0).contains(daeviceLocationCountRequest.getProvince());
                if(flag&&StringUtils.isNotEmpty(daeviceLocationCountRequest.getCity())){
                    if("上海市".equals(locations.get(0))||"北京市".equals(locations.get(0))||"重庆市".equals(locations.get(0))||"天津市".equals(locations.get(0))){
                        flag = locations.get(2).contains(daeviceLocationCountRequest.getCity());
                    }else{
                        flag = locations.get(1).contains(daeviceLocationCountRequest.getCity());
                    }
                }
            }
            if(flag) {
                if (locationCountMap.get(locations.get(0)) == null)
                    locationCountMap.put(locations.get(0), new HashMap<>());
                if (locationCountMap.get(locations.get(0)).get(locations.get(1)) == null)
                    locationCountMap.get(locations.get(0)).put(locations.get(1), new HashMap<>());
                if (locationCountMap.get(locations.get(0)).get(locations.get(1)).get(locations.get(2)) == null)
                    locationCountMap.get(locations.get(0)).get(locations.get(1)).put(locations.get(2), 0);
                locationCountMap.get(locations.get(0)).get(locations.get(1)).put(locations.get(2), locationCountMap.get(locations.get(0)).get(locations.get(1)).get(locations.get(2)) + 1);
            }
        });
        DeviceLocationCountVo deviceLocationCountVo = new DeviceLocationCountVo();
        deviceLocationCountVo.setTotal(0);
        List<DeviceLocationCountVo.Province> provinces = new ArrayList<>();
        for(String provinceTemp : locationCountMap.keySet()){
            DeviceLocationCountVo.Province province = new DeviceLocationCountVo.Province();
            province.setProvince(provinceTemp);
            province.setCount(0);
            List<DeviceLocationCountVo.City> citys = new ArrayList<>();
            for(String cityTemp : locationCountMap.get(provinceTemp).keySet()){
                DeviceLocationCountVo.City city = new DeviceLocationCountVo.City();
                city.setCity(cityTemp);
                city.setCount(0);
                List<DeviceLocationCountVo.District> districts = new ArrayList<>();
                for (String districtTemp : locationCountMap.get(provinceTemp).get(cityTemp).keySet()){
                    DeviceLocationCountVo.District district = new DeviceLocationCountVo.District();
                    district.setDistrict(districtTemp);
                    district.setCount(locationCountMap.get(provinceTemp).get(cityTemp).get(districtTemp));
                    city.setCount(city.getCount()+district.getCount());
                    districts.add(district);
                }
                city.setDistancts(districts);
                province.setCount(province.getCount()+city.getCount());
                citys.add(city);
            }
            province.setCitys(citys);
            deviceLocationCountVo.setTotal(deviceLocationCountVo.getTotal()+province.getCount());
            provinces.add(province);
        }
        deviceLocationCountVo.setProvinces(provinces);
        return new ApiResponse<>(deviceLocationCountVo);
    }

    /**
     * 按 月统计 用户活跃度
     * @return
     */
    public List<CustomerUserVo.CustomerUserMonthLiveCountVo> selectLiveCustomerUserCountPerMonth() {
        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        List rtnList = new ArrayList();
        Integer customerId = customerService.obtainCustomerId(false);
        //今年的用户数据
        StatsticCustomerUserLivePo queryPo = new StatsticCustomerUserLivePo();
        queryPo.setCustomerId(customerId);
        queryPo.setStatisticYear(nowYear);
        List monthUserConutList = statsticCustomerUserLiveMapper.selectLiveUserCountByMonth(queryPo);


        for(int i=1;i<=12;i++){
            CustomerUserVo.CustomerUserMonthLiveCountVo customerUserMonthLiveCountVo = new CustomerUserVo.CustomerUserMonthLiveCountVo();
            String nowMonth = i +"月";
            if(i<10){
                nowMonth = "0"+i+"月";
            }
            //今年某月的用户量
            Integer liveUserCount = new Integer(0);

            if(monthUserConutList!=null&&monthUserConutList.size()>0){
                for(int m=0;m<monthUserConutList.size();m++){
                    Map tempMap = (Map)monthUserConutList.get(m);
                    int tempMonth = (Integer) tempMap.get("statisticMonth");
                    Integer tempUserLiveCount = (Integer)tempMap.get("userLiveCount");
                    if(tempMonth==(i)){
                        liveUserCount = tempUserLiveCount;
                        break;
                    }
                }
            }


            customerUserMonthLiveCountVo.setMonth(nowMonth);
            customerUserMonthLiveCountVo.setUserLiveCount(liveUserCount);

            rtnList.add(customerUserMonthLiveCountVo);
        }

        return rtnList;
    }

    /**
     * 按 小时统计 用户活跃度
     * @return
     */
    public List<CustomerUserVo.CustomerUserHourLiveCountVo> selectLiveCustomerUserCountPerHour() {
        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        // 获得月份
        int month = cal.get(Calendar.MONTH) + 1;
        // 获得日期
        int date = cal.get(Calendar.DATE);

        List rtnList = new ArrayList();
        Integer customerId = customerService.obtainCustomerId(false);
        //今年的用户数据
        StatsticCustomerUserLivePo queryPo = new StatsticCustomerUserLivePo();
        queryPo.setCustomerId(customerId);
        queryPo.setStatisticYear(nowYear);
        queryPo.setStatisticMonth(month);
        queryPo.setStatisticDay(date);
        List monthUserConutList = statsticCustomerUserLiveMapper.selectLiveUserCountByHour(queryPo);


        for(int i=1;i<=24;i++){
            CustomerUserVo.CustomerUserHourLiveCountVo customerUserHourLiveCountVo = new CustomerUserVo.CustomerUserHourLiveCountVo();
            String nowHour = i +"时";
            //今年某月的用户量
            Integer liveUserCount = new Integer(0);

            if(monthUserConutList!=null&&monthUserConutList.size()>0){
                for(int m=0;m<monthUserConutList.size();m++){
                    Map tempMap = (Map)monthUserConutList.get(m);
                    int tempHour = (Integer) tempMap.get("statisticHour");
                    Integer tempUserLiveCount = (Integer)tempMap.get("userLiveCount");
                    if(tempHour==i){
                        liveUserCount = tempUserLiveCount;
                        break;
                    }
                }
            }


            customerUserHourLiveCountVo.setHour(nowHour);
            customerUserHourLiveCountVo.setUserLiveCount(liveUserCount);

            rtnList.add(customerUserHourLiveCountVo);
        }

        return rtnList;
    }
}
