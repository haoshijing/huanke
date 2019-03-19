package com.huanke.iot.api.controller.h5;

import com.huanke.iot.api.controller.h5.req.EnergySetDataReq;
import com.huanke.iot.api.controller.h5.response.EnergyPageVo;
import com.huanke.iot.api.service.energy.EnergyService;
import com.huanke.iot.base.api.ApiResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述:
 * 能源管理controller
 *
 * @author onlymark
 * @create 2019-03-18 下午2:27
 */
@RestController
@RequestMapping("/h5/api/energy")
@Slf4j
public class EnergyController extends BaseController {
    @Autowired
    private EnergyService energyService;


    @ApiOperation("查询指定页面匹配的数据点数据")
    @PostMapping("/getPageDatas/{pageId}")
    public ApiResponse<List<EnergyPageVo>> timerType(@PathVariable("pageId") String pageId){
        List<EnergyPageVo> energyPageVoList = energyService.selectPageDatasByPageId(pageId);
        return new ApiResponse<>(energyPageVoList);
    }

    @ApiOperation("设置指定数据点数据")
    @PostMapping("/setData")
    public ApiResponse<Boolean> setData(@RequestBody EnergySetDataReq request){
        Integer userId = getCurrentUserId();
        Boolean result = energyService.setDoubleData(request, userId);
        return new ApiResponse<>(result);
    }
}
