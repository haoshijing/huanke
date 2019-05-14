package com.huanke.iot.manage.controller.test;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.manage.common.util.ExcelUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 任务controller
 *
 * @author onlymark
 * @create 2018-11-16 上午9:33
 */
@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String username;


    @ApiOperation("测试poi")
    @PostMapping(value = "/test1")
    public ApiResponse<Boolean> test1(HttpServletResponse response) throws Exception {
        ExcelUtil<TestBean> deviceListVoExcelUtil = new ExcelUtil<>();
        Map<String, String> filterMap = new HashMap<>();
        filterMap.put("v1", "标题1");
        filterMap.put("v2", "标题2");
        filterMap.put("v3", "标题3");
        filterMap.put("v4", "标题4");
        filterMap.put("v5", "标题5");
        String[] titles = new String[5];
        titles[0] = "标题1";
        titles[1] = "标题2";
        titles[2] = "标题3";
        titles[3] = "标题4";
        titles[4] = "标题5";
        List<TestBean> deviceExportVoList = new ArrayList<>();
        deviceExportVoList.add(new TestBean("aa1", "bb1", "cc1", "dd1", "ee1"));
        deviceExportVoList.add(new TestBean("aa2", "bb2", "cc2", "dd2", "ee2"));
        deviceExportVoList.add(new TestBean("aa3", "bb3", "cc3", "dd3", "ee3"));
        deviceExportVoList.add(new TestBean("aa4", "bb4", "cc4", "dd4", "ee4"));
        deviceExportVoList.add(new TestBean("aa6", "bb6", "cc6", "dd6", "ee6"));
        InputStreamSource test1 = deviceListVoExcelUtil.createOutPutStream("test1", titles, deviceExportVoList, filterMap, "2007");
        //发送邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(username);
        helper.setTo("997008397@qq.com");
        helper.setSubject("爱你的一天");
        helper.setText("每日数据文件");
        helper.addAttachment("1111.xls", test1);
        mailSender.send(mimeMessage);
        return new ApiResponse<>(true);
    }
}
