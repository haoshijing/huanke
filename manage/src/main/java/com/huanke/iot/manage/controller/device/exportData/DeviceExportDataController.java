package com.huanke.iot.manage.controller.device.exportData;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.request.exportData.DeviceExportDataRequest;
import com.huanke.iot.base.resp.exportData.DeviceExportDataRsp;
import com.huanke.iot.manage.service.device.exportData.DeviceExportDataService;
import com.huanke.iot.manage.vo.request.BaseListRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author caikun
 * @version 2018年08月15日 22:11
 **/
@RestController
@RequestMapping("/api/deviceExport")
@Slf4j
public class DeviceExportDataController {
    @Autowired
    private DeviceExportDataService deviceExportDataService;

    /**
     * 查询客户列表
     * @param request
     * @return
     * @throws Exception
     */
    @ApiOperation("查询列表")
    @PostMapping(value = "/selectList")
    public ApiResponse<DeviceExportDataRsp> selectList(@RequestBody DeviceExportDataRequest request) throws Exception {
        DeviceExportDataRsp rsp = deviceExportDataService.selectList(request);
        return new ApiResponse<>(rsp);
    }

    @ApiOperation("批量添加设备mac信息")
    @PostMapping(value = "/add")
    public ApiResponse<Boolean> add(@RequestBody BaseListRequest<String> request) {
        Boolean result = deviceExportDataService.add(request);
        return new ApiResponse<>(result);
    }

    @ApiOperation("删除设备mac配置")
    @PostMapping(value = "/delete")
    public ApiResponse<Boolean> delete(@RequestBody BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        if(valueList.size() == 0){
            throw new BusinessException("没有要的信息");
        }
        Boolean result = deviceExportDataService.delete(valueList);
        return new ApiResponse<>(result);
    }
}
