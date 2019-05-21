package com.huanke.iot.manage.vo.response.device.typeModel;

import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.manage.vo.response.format.ModelFormatVo;
import lombok.Data;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceModelVo {

    private Integer id;
    private String name;
    private String modelNo;
    private String modelCode;
    private Integer typeId; //类型id
    private String typeName;
    private Integer customerId;
    private String customerName;
    private String productQrCode;
    private String productId;
    private Integer formatId;
    private Integer androidFormatId;
    private String[] icons;
    private String version;
    private String childModelIds;
    private Integer status = CommonConstant.STATUS_YES;
    private String remark;
    private List<String> helpFileUrlList;

    private Long createTime;
    private Integer createUser;
    private String createUserName;
    private Long lastUpdateTime;
    private Integer lastUpdateUser;
    private String lastUpdateUserName;


    private List<DeviceModelAbilityVo> deviceModelAbilitys;

    private ModelFormatVo deviceModelFormat;

    private Integer devicePoolCount;
    @Data
    public static class DeviceModelPercent{
        private Integer modelId;
        private String modelName;
        private Long deviceCount;
        private String modelPercent;
    }

    private Integer listShowModelAbilityId;

}
