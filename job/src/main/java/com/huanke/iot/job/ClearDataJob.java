package com.huanke.iot.job;

import com.huanke.iot.base.dao.data.DeviceExportDataMapper;
import com.huanke.iot.base.dao.device.data.DeviceControlMapper;
import com.huanke.iot.base.dao.device.data.DeviceSensorDataMapper;
import com.huanke.iot.base.dto.DeviceIdMacDto;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.device.data.DeviceSensorPo;
import com.huanke.iot.base.util.ExcelUtil;
import com.huanke.iot.job.bean.DeviceExportDataBean;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import javax.mail.internet.MimeMessage;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 描述:
 * 清除设备多余数据任务
 *
 * @author onlymark
 * @create 2018-11-15 上午9:41
 */
@Repository
@Slf4j
public class ClearDataJob {
    @Autowired
    private DeviceSensorDataMapper deviceSensorDataMapper;
    @Autowired
    private DeviceControlMapper deviceControlMapper;
    @Autowired
    private DeviceExportDataMapper deviceExportDataMapper;
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.tomail}")
    private String tomail;

    @Scheduled(cron = "0 5 2 * * ?")
    public void clearControlData() {
        long lastFiveTime = System.currentTimeMillis() - (120 * 60 * 1000);
        deviceControlMapper.clearData(lastFiveTime);
    }

    @Scheduled(cron = "0 5 3 * * ?")
    public void clearSensorData() {
        long lastFiveTime = System.currentTimeMillis() - (180 * 60 * 1000);
        deviceSensorDataMapper.clearData(lastFiveTime);
    }

    @Scheduled(cron = "0 5 1 * * ?")
    public void exportDeviceData() throws Exception {
        log.info("export device data...");
        List<DeviceIdMacDto> deviceIdMacDtoList = deviceExportDataMapper.queryExportDataDevice();
        long startTime = System.currentTimeMillis() - (65 * 60 * 1000) - (60 * 24 * 60 * 1000) ;
        long endTime = System.currentTimeMillis() - (65 * 60 * 1000);
//        long startTime = System.currentTimeMillis() - (1 * 60 * 1000) - (60 * 6 * 60 * 1000) ;
//        long endTime = System.currentTimeMillis() - (1 * 60 * 1000) - (60 * 5 * 60 * 1000) ;
        for (DeviceIdMacDto deviceIdMacDto : deviceIdMacDtoList) {
            Integer deviceId = deviceIdMacDto.getDeviceId();
            List<DeviceSensorPo> deviceSensorPos = deviceSensorDataMapper.queryExportDataByDeviceId(deviceId, startTime, endTime);
            List<DeviceExportDataBean> deviceExportVoList = new ArrayList<>();
            for (DeviceSensorPo deviceSensorPo : deviceSensorPos) {
                String sensorType = deviceSensorPo.getSensorType();
                SensorTypeEnums sensorTypeEnum = SensorTypeEnums.getByCode(sensorType);
                deviceExportVoList.add(new DeviceExportDataBean(deviceIdMacDto.getMac(), deviceId, sensorTypeEnum.getMark(),
                        deviceSensorPo.getSensorValue(), sensorTypeEnum.getUnit(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(deviceSensorPo.getCreateTime()))));
            }
            ExcelUtil<DeviceExportDataBean> deviceListVoExcelUtil = new ExcelUtil<>();
            Map<String, String> filterMap = new HashMap<>();
            filterMap.put("mac", "设备mac");
            filterMap.put("deviceId", "设备id");
            filterMap.put("funcName", "功能项名称");
            filterMap.put("sensorValue", "传感器上报数值");
            filterMap.put("unit", "单位");
            filterMap.put("time", "时间");
            String[] titles = new String[6];
            titles[0] = "设备mac";
            titles[1] = "设备id";
            titles[2] = "功能项名称";
            titles[3] = "传感器上报数值";
            titles[4] = "单位";
            titles[5] = "时间";
            String dateFormat = new DateTime().minusDays(1).toString("yyyy-MM-dd");
            deviceListVoExcelUtil.exportExcel(new FileOutputStream("/usr/local/dev/daydatas/" + "设备传感器数据-" + deviceIdMacDto.getMac() + "-" + dateFormat + ".xls") , "test1", titles, deviceExportVoList, filterMap, "2007");
            //deviceListVoExcelUtil.exportExcel(new FileOutputStream("/Users/liuxiaoyu/Downloads/" + "设备传感器数据12点～13点-" + deviceIdMacDto.getMac() + "-" + dateFormat + ".xls") , "test1", titles, deviceExportVoList, filterMap, "2007");

            try {
                InputStreamSource test1 = deviceListVoExcelUtil.createOutPutStream("test1", titles, deviceExportVoList, filterMap, "2007");
                //发送邮件
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom(username);
                helper.setTo(tomail);
                helper.setSubject("设备传感器数据：" + dateFormat);
                helper.setText("每日数据文件,详情见附件");
                helper.addAttachment("设备传感器数据-" + deviceIdMacDto.getMac() + "-" + dateFormat + ".xls", test1);
                mailSender.send(mimeMessage);
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
    }
}
