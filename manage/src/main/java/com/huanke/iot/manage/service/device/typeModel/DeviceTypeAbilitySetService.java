package com.huanke.iot.manage.service.device.typeModel;

import com.huanke.iot.base.dao.device.typeModel.DeviceTypeAbilitySetMapper;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeCreateOrUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class DeviceTypeAbilitySetService {

    @Autowired
    private DeviceTypeAbilitySetMapper deviceTypeAbilitySetMapper;

    @Value("${accessKeyId}")
    private String accessKeyId;

    @Value("${accessKeySecret}")
    private String accessKeySecret;

    @Value("${bucketUrl}")
    private String bucketUrl;

    @Value("${bucketName}")
    private String bucketName;


    /**
     * 删除 类型的功能集
     * @param typeAbilitySetId
     * @return
     */
    public Boolean deleteById(Integer typeAbilitySetId) {

        Boolean ret  =false;
        //判断当 类型id不为空时
        if( typeAbilitySetId!=null){
            //先删除 该 功能
            ret = deviceTypeAbilitySetMapper.deleteById(typeAbilitySetId) > 0;
        }else{
            log.error("类型主键不可为空");
            return false;
        }
        return ret;
    }

}
