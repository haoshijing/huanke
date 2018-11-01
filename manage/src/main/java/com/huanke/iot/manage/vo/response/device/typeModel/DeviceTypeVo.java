package com.huanke.iot.manage.vo.response.device.typeModel;

import com.huanke.iot.manage.vo.response.device.ability.DeviceTypeAbilitysVo;
import lombok.Data;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceTypeVo {

    private Integer id;
    private String name;
    private String typeNo;
    private String icon;
    private String stopWatch;
    private String remark;
    private String source;
    private Long createTime;
    private Integer createUser;
    private String createUserName;
    private Long lastUpdateTime;
    private Integer lastUpdateUser;
    private String lastUpdateUserName;


    private List<DeviceTypeAbilitysVo> DeviceTypeAbilitys;

    @Data
    public static class DeviceTypePercent{
        private Integer typeId;
        private String typeName;
        private Long deviceCount;
        private String typePercent;

    }
}

