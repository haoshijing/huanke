package com.huanke.iot.api.web;

import com.alibaba.fastjson.JSON;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.WxConfigMapper;
import com.huanke.iot.base.po.customer.WxConfigPo;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 描述:
 * 设备高级设置拦截器
 *
 * @author onlymark
 * @create 2018-09-11 下午3:05
 */
@Repository
public class DeviceHighSetInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private WxConfigMapper wxConfigMapper;

    private static final String HIGHTOKEN = "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String token = request.getHeader(HIGHTOKEN);
        if(token == null){
            ApiResponse apiResponse = new ApiResponse(RetCode.TICKET_ERROR,"请传入token");
            response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.getWriter().print(JSON.toJSONString(apiResponse));
            return false;
        }
        String hightoken = stringRedisTemplate.opsForValue().get(token);
        if(hightoken!=null) {
            String[] split = hightoken.split("-");
            WxConfigPo wxConfigPo = wxConfigMapper.getByJoinId(Integer.valueOf(split[0]), split[1]);
            if (wxConfigPo != null) {
                return true;
            }
        }

        ApiResponse apiResponse = new ApiResponse(RetCode.TICKET_ERROR,"token无效或已经过期");
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.getWriter().print(JSON.toJSONString(apiResponse));
        return false;
    }
}
