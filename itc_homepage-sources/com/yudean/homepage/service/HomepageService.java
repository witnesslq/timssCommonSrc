package com.yudean.homepage.service;

import java.util.List;
import java.util.Set;

import com.yudean.homepage.bean.HomepageWorkTask;
import com.yudean.homepage.bean.ProcessFucExtParam;
import com.yudean.homepage.bean.WorktaskBean;
import com.yudean.homepage.vo.WorktaskViewObj;
import com.yudean.itc.dto.sec.SecProcRoute;
import com.yudean.mvc.bean.userinfo.UserInfo;

/**
 * 任务管理模块服务接口，提供给其他模块使用
 * 
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: HomepageService.java
 * @author: kChen
 * @createDate: 2014-7-8
 * @updateUser: kChen
 * @version: 1.0
 */
public interface HomepageService {

    /**
     * 新增通知
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-10-16
     * @param homeworkTask
     * @param userInfo
     * @return:
     */
    String createNotice(HomepageWorkTask homeworkTask, List<String> operUser, UserInfo userInfo);

    /**
     * 删除指定通知
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-10-16
     * @param processInsId
     * @param userInfo
     * @return:
     */
    String deleteNotice(String processInsId, UserInfo userInfo);

    /**
     * 创建任务
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-9-15
     * @param homeworkTask 任务对象{}
     * @param extParam2用于设置辅助参数，例如visibletype：插入到路由表中的可见类型
     * @return:
     */
    String create(HomepageWorkTask homeworkTask, UserInfo userInfo);

    /**
     * 创建任务
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-9-15
     * @param homeworkTask 任务对象{}
     * @param extParam2用于设置辅助参数，例如visibletype：插入到路由表中的可见类型
     * @return:
     */
    String create(HomepageWorkTask homeworkTask, UserInfo userInfo, ProcessFucExtParam extParam2);

    /**
     * 创建一个任务实例，会被直接放置在草稿中 该方法统一实现了消息发送方法
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-8
     * @param flow 流水号
     * @param processInstId 关联ID，关联具体业务的id,有业务模块提供
     * @param typeName 任务类别名称,如采购申请、采购单等
     * @param name 任务名称
     * @param statusName 状态名称,如部门经理审批等
     * @param url 点击时的跳转路径
     * @return
     * @throws RuntimeException :
     */
    String createProcess(String flow, String processInstId, String typeName, String name, String statusName,
            String url, UserInfo userInfo, ProcessFucExtParam extParam) throws RuntimeException;

    /**
     * 创建一个任务实例，会被直接放置在草稿中 该方法统一实现了消息发送方法
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-8
     * @param flow 流水号
     * @param processInstId 关联ID，关联具体业务的id,有业务模块提供
     * @param typeName 任务类别名称,如采购申请、采购单等
     * @param name 任务名称
     * @param statusName 状态名称,如部门经理审批等
     * @param url 点击时的跳转路径
     * @param tasktype 任务类别，主任务WorktaskBean.TaskType.Main\ 子任务
     *            WorktaskBean.TaskType.Sub
     * @return
     * @throws RuntimeException :
     */
    String createProcess(String flow, String processInstId, String typeName, String name, String statusName,
            String url, WorktaskBean.TaskType tasktype, UserInfo userInfo, ProcessFucExtParam extParam2)
            throws RuntimeException;

    /**
     * 扭转任务 该方法统一实现了消息发送方法
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-8
     * @param id 修改参数，可以是主键ID Integer，也可以是流水号flowno String
     * @param statusName 状态名称
     * @param operUser : 下一步办理人列表
     * @param userInfo :
     */
    void Process(String processInstId, String statusName, List<String> operUser, UserInfo userInfo,
            ProcessFucExtParam extParam) throws RuntimeException;

    /**
     * 删除某个在办任务的待办列表,携带子任务信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-28
     * @param taskId 任务节点ID
     * @param ProcessInstId 任务实例ID
     * @param userInfo 当前操作人
     * @throws RuntimeException :
     */
    void DeleteProcess(String taskId, String ProcessInstId, UserInfo userInfo) throws RuntimeException;

    /**
     * 扭转主任务，，如果子任务id存在则不扭转
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-28
     * @param processInstId
     * @param subProcessInstId
     * @param statusName
     * @param operUser
     * @param userInfo
     * @throws RuntimeException :
     */
    void Process(String taskId, String ProcessInstId, String statusName, List<String> operUser, UserInfo userInfo,
            ProcessFucExtParam extParam) throws RuntimeException;

    /**
     * 扭转主任务，如果子任务id不存在则不扭转
     * 
     * @param taskId
     * @param ProcessInstId
     * @param statusName
     * @param operUser 办理用户信息set，可以指定下一步办理人的站点
     * @param userInfo
     * @throws RuntimeException
     */
    void Process(String taskId, String ProcessInstId, String statusName, Set<UserInfo> operUser, UserInfo userInfo,
            ProcessFucExtParam extParam) throws RuntimeException;

    /**
     * 删除任务
     * 
     * @description: 该方法统一实现了消息发送方法
     * @author: kChen
     * @createDate: 2014-7-8
     * @param processInstId 流程实例编号
     * @param userInfo :
     */
    void Delete(String processInstId, UserInfo userInfo) throws RuntimeException;

    /**
     * 完成任务
     * 
     * @description: 该方法统一实现了消息发送方法
     * @author: kChen
     * @createDate: 2014-7-8
     * @param processInstId 流程实例ID
     * @param userInfo 用户信息
     */
    void complete(String processInstId, UserInfo userInfo) throws RuntimeException;

    /**
     * 完成任务
     * 
     * @description: 该方法统一实现了消息发送方法
     * @author: kChen
     * @createDate: 2014-9-10
     * @param processInstId 流程实例ID
     * @param userInfo 用户信息
     * @param statusName 状态名称
     * @throws RuntimeException :
     */
    void complete(String processInstId, UserInfo userInfo, String statusName) throws RuntimeException;

    /**
     * 修改一个任务内容
     * 
     * @description:当某个字段为null时，不会修改
     * @author: kChen
     * @createDate: 2014-7-8
     * @param id
     * @param flow
     * @param typeName
     * @param name
     * @param statusName
     * @param operUser
     * @param url
     * @throws RuntimeException :
     */
    void modify(Object id, String flow, String typeName, String name, String statusName, List<String> operUser,
            String url, UserInfo userInfo) throws RuntimeException;

    /**
     * 获取指定用户的所有待办任务,站点为null时，获取所有站点的待办信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-11-24
     * @param userid 用户编码
     * @param siteid 站点编码，为空则获取所有站点下的待办
     * @return:
     */
    List<WorktaskViewObj> getUserAllTasking(String userid, String siteid) throws RuntimeException;

    /**
     * 根据传入的flowno获取任务信息
     * 
     * @param flowno
     * @param userInfo
     * @return
     */
    WorktaskBean getOneTaskByFlowNo(String flowno, UserInfo userInfo) throws RuntimeException;

    /**
     * 通过参数获取路由表
     * 
     * @param flowNo 流水号 如果为null则不作为查询条件
     * @param userId 用户编号 如果为null则不作为查询条件
     * @param siteId 站点编号 如果为null则不作为查询条件
     * @return
     */
    List<SecProcRoute> getSecRouteInfo(String flowNo, String userId, String siteId);

    /**
     * @description:创建待办或站内信息（不走流程）
     * @author: yuanzh
     * @createDate: 2015-9-8
     * @param task 消息内容
     * @param userIds 接收用户编号
     * @param userInfo 当前用户信息
     * @param type: 信息类型
     */
    void createNoticeWithOutWorkflow(HomepageWorkTask task, List<String> userIds, UserInfo userInfo, String type);

    /**
     * @description:删除待办或站内信息（不走流程）
     * @author: yuanzh
     * @createDate: 2015-9-8
     * @param processInitId 唯一id
     * @param userInfo: 当前用户信息
     */
    void deleteNoticeWithOutWorkflow(String processInitId, UserInfo userInfo);
}
