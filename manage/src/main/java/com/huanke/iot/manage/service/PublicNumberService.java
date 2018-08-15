package com.huanke.iot.manage.service;

import com.alibaba.fastjson.JSONObject;
//import com.huanke.iot.base.dao.publicnumber.PublicNumberMapper;
//import com.huanke.iot.base.po.publicnumber.PublicNumberPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicNumberService {

//    @Autowired
//    private PublicNumberMapper publicNumberMapper;
//
//    public List<JSONObject> obtainList() {
//       List<PublicNumberPo> publicNumberPos =  publicNumberMapper.selectList(new PublicNumberPo(),100,0);
//       return publicNumberPos.stream().map(publicNumberPo -> {
//           JSONObject jsonObject = new JSONObject();
//           jsonObject.put("id",publicNumberPo.getId());
//           jsonObject.put("name",publicNumberPo.getName());
//           return jsonObject;
//       }).collect(Collectors.toList());
//    }
}
