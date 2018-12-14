package com.huanke.iot.job.controller;

import com.huanke.iot.base.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/job/flushCache")
@Slf4j
public class FlushCacheController {
    @RequestMapping("/flushCache")
    public ApiResponse<Boolean> flushCache() {
        log.info("刷新缓存");
        //deviceDataService.flushCache();
        return new ApiResponse<>(true);
    }
}
