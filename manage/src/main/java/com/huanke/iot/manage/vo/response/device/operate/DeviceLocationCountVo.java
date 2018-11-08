package com.huanke.iot.manage.vo.response.device.operate;

import lombok.Data;

import java.util.List;

@Data
public class DeviceLocationCountVo {
    private Integer total;
    private List<Province> provinces;

    @Data
    public static class Province {
        private String province;
        private List<City> citys;
        private Integer count;
    }
    @Data
    public static class City{
        private String city;
        private List<District> distancts;
        private Integer count;
    }

    @Data
    public static class District{
        private String district;
        private Integer count;
    }
}
