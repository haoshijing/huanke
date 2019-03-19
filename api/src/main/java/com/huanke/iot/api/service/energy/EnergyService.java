package com.huanke.iot.api.service.energy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.controller.h5.req.EnergySetDataReq;
import com.huanke.iot.api.controller.h5.response.EnergyPageVo;
import com.huanke.iot.api.util.HttpUtil;
import com.huanke.iot.base.dao.energy.EnergyIdMapper;
import com.huanke.iot.base.po.energy.EnergyIdPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
@Slf4j
public class EnergyService {
    @Autowired
    private EnergyIdMapper energyIdMapper;

    public List<EnergyPageVo> selectPageDatasByPageId(String pageId) {
        List<EnergyPageVo> energyPageVoList = new ArrayList<>();
        List<EnergyIdPo> energyIdPos = energyIdMapper.selectEnergyIdsByPageId(pageId);
        Map<String, EnergyPageVo> energyIdPoMap = new HashMap<>();
        StringBuilder paramSb = new StringBuilder();
        paramSb.append("ver:\"3.0\"").append("\r\n").append("id").append("\r\n");
        energyIdPos.stream().forEach(e -> {
            paramSb.append("@").append(e.getEnergyId()).append("\r\n");
            energyIdPoMap.put(e.getEnergyId(), new EnergyPageVo(e.getEnergyId(), e.getPageId(), e.getName(), e.getPageMatchId(), e.getType()));

        });
        System.out.println(paramSb.toString());//查看请求数据点

        JSONObject jsonObject = HttpUtil.sendText(paramSb.toString(), "http://120.92.153.51:13301/read");
        System.out.println(jsonObject.toString());
        JSONArray rows = jsonObject.getJSONArray("rows");
        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = rows.getJSONObject(i); // 遍历 jsonarray 数组，把每一个对象转成 json 对象
            String curVal = row.getString("curVal");
            String energyId = row.getString("id");
            EnergyPageVo energyPageVo = energyIdPoMap.get(energyId.substring(2,energyId.length()));
            curVal = curVal.substring(2,curVal.length());
            energyPageVo.setPageId(pageId);
            energyPageVo.setData(Double.parseDouble(curVal));
            energyPageVoList.add(energyPageVo);
        }
        return energyPageVoList;
    }

    public Boolean setDoubleData(EnergySetDataReq request, Integer userId) {
        StringBuilder paramSb = new StringBuilder();
        paramSb.append("ver:\"3.0\"").append("\r\n").append("id, level, val, who, duration").append("\r\n");
        paramSb.append("@").append(request.getEnergyId()).append(",8,").append(request.getData()).append(",").append("\"" + userId + "\"").append(",5");
        System.out.println(paramSb.toString());//查看请求数据点

        JSONObject jsonObject = HttpUtil.sendText(paramSb.toString(), "http://120.92.153.51:13301/pointWrite");
        System.out.println(jsonObject.toString());
        JSONObject meta = jsonObject.getJSONObject("meta");
        String ver = meta.getString("ver");
        if(ver.equals("2.0")){
            return true;
        }
        return false;
    }
}
