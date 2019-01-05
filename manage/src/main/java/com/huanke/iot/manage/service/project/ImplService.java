package com.huanke.iot.manage.service.project;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.base.dao.DictMapper;
import com.huanke.iot.base.dao.project.ImplMapper;
import com.huanke.iot.base.po.project.ProjectImplementLog;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.project.ImplementRequest;
import com.huanke.iot.base.resp.project.ImplementRsp;
import com.huanke.iot.manage.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        if (projectImplementLog.getId() == null) {
            //处理图册{
            List<String> imgList = request.getImgList();
            String imgListStr = String.join(",", imgList);
            projectImplementLog.setImgList(imgListStr);
            projectImplementLog.setFileList(JSONObject.toJSONString(request.getFileMap()));
            projectImplementLog.setCreateUser(user.getId());
            projectImplementLog.setCreateTime(new Date());
            return implMapper.insert(projectImplementLog) > 0;
        } else {
            //处理图册{
            List<String> imgList = request.getImgList();
            String imgListStr = String.join(",", imgList);
            projectImplementLog.setImgList(imgListStr);

            projectImplementLog.setFileList(JSONObject.toJSONString(request.getFileMap()));
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

    public ImplementRsp select(Integer implId) {
        ImplementRsp implementRsp = new ImplementRsp();
        ProjectImplementLog projectImplementLog = implMapper.selectById(implId);
        BeanUtils.copyProperties(projectImplementLog, implementRsp);
        String fileList = implementRsp.getFileList();
        implementRsp.setFileMap((Map<String, List<String>>) JSONObject.parse(fileList));

        String imgListStr = projectImplementLog.getImgList();
        if(imgListStr != null){
            implementRsp.setImgList(Arrays.asList(imgListStr.split(",")));
        }
        implementRsp.setTypeName(dictMapper.selectById(projectImplementLog.getTypeId()).getLabel());
        implementRsp.setFileList(null);
        implementRsp.setImgListStr(null);
        return implementRsp;
    }
}
