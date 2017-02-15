package com.yudean.homepage.interfaces;

import java.util.List;
import java.util.Set;

import com.yudean.homepage.bean.WorktaskBean;
import com.yudean.mvc.bean.userinfo.UserInfo;

/**
 * 任务管理模块通知消息回调接口
 * 
 * @title: 接口实现了在创建待办、扭转任务时需要发送消息的回调接口，需要实现它才能发送消息
 * @description: {desc}
 * @company: gdyd
 * @className: HomepageNotifyInterface.java
 * @author: kChen
 * @createDate: 2014-7-24
 * @updateUser: kChen
 * @version: 1.0
 */
public interface HomepageNotifyInterface {
    /**
     * 工作任务通知消息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-24
     * @param flow
     * @param processInstId
     * @param typeName
     * @param name
     * @param statusName
     * @param url
     * @param userList
     * @param userInfo
     * @throws RuntimeException :
     * @throws Throwable
     */
    void notify(String flow, String processInstId, String typeName, String name, String statusName, String url,
            List<String> userList, UserInfo userInfo, WorktaskBean taskBean) throws RuntimeException;

    /**
     * 工作任务通知消息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-24
     * @param flow
     * @param processInstId
     * @param typeName
     * @param name
     * @param statusName
     * @param url
     * @param operUser
     * @param userInfo
     * @throws RuntimeException :
     * @throws Throwable
     */
    void notifyWithSetUser(String flow, String processInstId, String typeName, String name, String statusName,
            String url, Set<UserInfo> operUser, UserInfo userInfo, WorktaskBean taskBean) throws RuntimeException;
}
