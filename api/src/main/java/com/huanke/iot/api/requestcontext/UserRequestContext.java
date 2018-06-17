package com.huanke.iot.api.requestcontext;


import com.huanke.iot.base.innercache.PublicNumberCacheVo;
import lombok.Data;

@Data
public class UserRequestContext {

    private PublicNumberCacheVo cacheVo;
    private Integer currentId;
}
