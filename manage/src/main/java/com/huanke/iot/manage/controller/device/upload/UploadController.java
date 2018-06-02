package com.huanke.iot.manage.controller.device.upload;


import com.aliyun.oss.OSSClient;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.po.device.DeviceUpgradePo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * @author haoshijing
 * @version 2018年06月02日 15:15
 **/
@RestController
@Slf4j
@RequestMapping("/api")
public class UploadController {
    @Value("${accessKeyId}")
    private String accessKeyId;

    @Value("${accessKeySecret}")
    private String accessKeySecret;

    @Value("${bucketUrl}")
    private String bucketUrl;

    @Value("${bucketName}")
    private String bucketName;

    @RequestMapping("/upload")
    public ApiResponse<String> uploadBinFile(MultipartFile file){
        String fileName = file.getOriginalFilename();
        String newFileName = UUID.randomUUID().toString().replace("-","");
        int idx = fileName.lastIndexOf(".");
        String fileExt = fileName.substring(idx+1);
        newFileName = newFileName + "."+fileExt;

        try {
            uploadToOss(newFileName,file.getBytes());
        }catch (Exception e){
            return ApiResponse.responseError(e);
        }

        return new ApiResponse<>(fileName);
    }
    private void uploadToOss(String fileKey,byte[] content){
        OSSClient ossClient = new OSSClient(bucketUrl, accessKeyId,accessKeySecret);
        try {
            ossClient.putObject(bucketName, fileKey, new ByteArrayInputStream(content));
        }catch (Exception e){
            log.error("",e);
        }finally {
            if(ossClient != null){
                ossClient.shutdown();
            }
        }
    }
}
