package com.yudean.homepage.service;

import java.util.List;

import com.yudean.homepage.bean.WorktaskBean.WorkTaskClass;
import com.yudean.homepage.bean.WorktaskUserBean.WorkTaskUserFlag;
import com.yudean.homepage.vo.WorktaskFlowViewObj;
import com.yudean.homepage.vo.WorktaskViewObj;
import com.yudean.mvc.bean.userinfo.UserInfo;
import com.yudean.itc.dto.Page;

/**
 * 任务管理前端服务
 * 
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: HomepageFrontService.java
 * @author: kChen
 * @createDate: 2014-7-8
 * @updateUser: kChen
 * @version: 1.0
 */
public interface HomepageFrontService {

    /**
     * 获取待办任务
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-28
     * @param page
     * @param userInfo
     * @return
     * @throws RuntimeException:
     */
    Page<WorktaskViewObj> getDoingTaskList(Page<WorktaskViewObj> page, UserInfo userInfo) throws RuntimeException;

    /**
     * 获取办理信息列表
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-8
     * @param userCode
     * @param userInfo
     * @param page
     * @return
     * @throws RuntimeException:
     */
    Page<WorktaskViewObj> getTaskList(Page<WorktaskViewObj> page, UserInfo userInfo) throws RuntimeException;
    
    /**
     * 获取办毕信息列表
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-8
     * @param userCode
     * @param userInfo
     * @param page
     * @return
     * @throws RuntimeException:
     */
    Page<WorktaskViewObj> getCompleteTaskList(Page<WorktaskViewObj> page, UserInfo userInfo) throws RuntimeException;

    /**
     * 前端操作删除某个任务
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-31
     * @param taskVoList
     * @param userInfo
     * @return
     * @throws RuntimeException:
     */
    Integer deleteTaskList(List<WorktaskViewObj> taskVoList, UserInfo userInfo) throws RuntimeException;

    /**
     * 删除业务数据
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-9-10
     * @param taskVoList:
     */
    @Deprecated
    void deleteBusinessTask(List<WorktaskViewObj> taskVoList);

    /**
     * 根据流水号号获取某条表单详情
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-9-23
     * @param flowNo
     * @return
     * @throws RuntimeException:
     */
    WorktaskFlowViewObj getWorkTaskInfoByFlowNo(String flowNo) throws RuntimeException;

    /**
     * 获取站点用户待办、在办任务信息
     * 
     * @return
     * @throws RuntimeException
     */
    int getUserProcessTaskCount(String userId, String siteId, UserInfo userInfo) throws RuntimeException;
    /**
     * 获取站点用户工作任务某一类任务信息
     * 
     * @return
     * @throws RuntimeException
     */
    int getUserTaskStaticCount(String userId, String siteId, WorkTaskClass type, WorkTaskUserFlag flag) throws RuntimeException;
}
