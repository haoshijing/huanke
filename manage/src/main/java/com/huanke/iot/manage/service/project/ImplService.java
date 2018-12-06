package com.huanke.iot.manage.service.project;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.base.dao.DictMapper;
import com.huanke.iot.base.dao.project.ImplMapper;
import com.huanke.iot.base.po.config.DictPo;
import com.huanke.iot.base.po.project.ProjectImplementLog;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.project.ImplementRequest;
import com.huanke.iot.base.resp.project.ImplementRsp;
import com.huanke.iot.manage.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * 描述:
 * 执行工程service
 *
 * @author onlymark
 * @create 2018-11-16 上午9:36
 */
@Slf4j
@Repository
public class ImplService {
    @Autowired
    private UserService userService;
    @Autowired
    private ImplMapper implMapper;
    @Autowired
    private DictMapper dictMapper;

    public Boolean addImplement(ImplementRequest request) {
        User user = userService.getCurrentUser();
        ProjectImplementLog projectImplementLog = new ProjectImplementLog();
        BeanUtils.copyProperties(request, projectImplementLog);
        if(projectImplementLog.getId() == null){
            //处理图册{
            List<String> imgList = request.getImgList();
            String imgListStr = String.join(",", imgList);
            projectImplementLog.setImgList(imgListStr);
            projectImplementLog.setFileList(JSONObject.toJSONString(request.getFileMap()));
            projectImplementLog.setCreateUser(user.getId());
            projectImplementLog.setCreateTime(new Date());
            return implMapper.insert(projectImplementLog) > 0;
        }else{
            //处理图册{
            List<String> imgList = request.getImgList();
            String imgListStr = String.join(",", imgList);
            projectImplementLog.setImgList(imgListStr);
            List<ImplementRequest.updateFileMap> fileMapUpdate = request.getFileMapUpdate();
            Map<String, List<String>> fileMap = new HashMap<>();
            Set<String> keySet = fileMap.keySet();
            for (ImplementRequest.updateFileMap fileUpdate : fileMapUpdate) {
                Integer id = fileUpdate.getId();
                DictPo dictPo = dictMapper.selectById(id);
                String label = dictPo.getLabel();
                if(keySet.contains(label)){
                    List<String> stringList = fileMap.get(label);
                    stringList.add(fileUpdate.getUrl());
                }else{
                    List<String> fileMapUp = new ArrayList<>();
                    fileMapUp.add(fileUpdate.getUrl());
                    fileMap.put(label, fileMapUp);
                }
            }
            projectImplementLog.setFileList(JSONObject.toJSONString(fileMap));
            projectImplementLog.setUpdateUser(user.getId());
            projectImplementLog.setUpdateTime(new Date());
            return implMapper.updateById(projectImplementLog) > 0;
        }

    }

    public List<ImplementRsp> selectImplements(Integer projectId) {
        List<ImplementRsp> implementRspList = implMapper.selectByProjectId(projectId);
        for (ImplementRsp implementRsp : implementRspList) {

            String fileList = implementRsp.getFileList();
            implementRsp.setFileMap((Map<String, List<String>>) JSONObject.parse(fileList));

            String imgListStr = implementRsp.getImgListStr();
            implementRsp.setImgList(Arrays.asList(imgListStr.split(",")));
            implementRsp.setFileList(null);
            implementRsp.setImgListStr(null);
        }
        return implementRspList;
    }
}
