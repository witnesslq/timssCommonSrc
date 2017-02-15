package com.yudean.homepage.facade;

import java.util.List;
import java.util.Set;

import com.yudean.homepage.bean.HomepageWorkTask;
import com.yudean.homepage.bean.ProcessFucExtParam;
import com.yudean.homepage.bean.WorktaskBean;
import com.yudean.homepage.vo.WorktaskViewObj;
import com.yudean.itc.dto.sec.SecProcRoute;
import com.yudean.mvc.bean.userinfo.UserInfo;

public interface ITaskFacade {

    /**
     * 创建任务,存入草稿
     * 
     * @description:        
     * @createDate: 2016-2-26
     * @param flow          流水号
     * @param extcode       关联具体业务的id,由业务模块提供，一般情况下为业务模块的主键，如：MC20160302011
     * @param typeName      任务类别名称,如采购申请、采购单等
     * @param name          任务名称，如：请假申请
     * @param url           点击时的跳转路径
     * @param userInfo      当前操作用户信息
     * @param extParam      用于设置辅助参数，例如visibletype：插入到路由表中的可见类型
     * @return:  任务的主键
     */
    String create(String flowNo, String extCode, String typeName, String name,String statusName,
            String url, UserInfo userInfo, ProcessFucExtParam extParam);

    /**
     * 创建一个任务实例
     * 
     * @description:          不区分是否使用流程，使用流程的话，需要设置任务类别，不使用流程则设置任务类别为Main
     * @createDate: 2016-2-26
     * @param flow            流水号
     * @param extcode         关联具体业务的id,由业务模块提供，可以是流程实例id，也可以是业务模块自定义的id
     * @param typeName        任务类别名称,如采购申请、采购单等
     * @param name            任务名称
     * @param statusName      状态名称,如部门经理审批等
     * @param url             点击时的跳转路径
     * @param tasktype        任务类别，主任务WorktaskBean.TaskType.Main\ 子任务WorktaskBean.TaskType.Sub
     * @param userInfo        当前操作用户信息
     * @param extParam        辅助参数
     * @return
     * @throws RuntimeException :
     */
    String createProcess(String flow, String extCode, String typeName, String name, String statusName,
            String url, WorktaskBean.TaskType tasktype, UserInfo userInfo, ProcessFucExtParam extParam)
            throws RuntimeException;

    /**
     * 执行任务
     *   
     * @description:            不使用工作流，手动执行任务跳转到下一步。
     * @createDate: 2016-2-26
     * @param extcode          关联具体业务的id,由业务模块提供
     * @param statusName        状态名称
     * @param operUser          下一步办理人列表
     * @param userInfo          当前操作用户信息
     * @param extParam          辅助参数
     */
    void execTask(String extCode, String statusName, String name,List<String> operUser, UserInfo userInfo,
            ProcessFucExtParam extParam) throws RuntimeException;

    /**
     * 执行任务
     * 
     * @description:            使用工作流，自动执行任务跳转到下一步。
     * @createDate: 2016-2-26
     * @param taskId            任务id
     * @param processInstId     流程实例id
     * @param statusName        状态名称,如部门经理审批等
     * @param operUser          下一步办理人列表
     * @param userInfo          当前用户信息
     * @param extParam          辅助参数
     * @throws RuntimeException :
     */
    void execTaskWithProcess(String taskId, String processInstId, String statusName, List<String> operUser, UserInfo userInfo,
            ProcessFucExtParam extParam) throws RuntimeException;

    /**
     * 执行任务
     * @description:          使用工作流，自动执行任务跳转到下一步。
     * @createDate: 2016-2-26
     * @param taskId          任务id
     * @param ProcessInstId   流程实例id
     * @param statusName      状态名称
     * @param operUser        办理用户信息set，可以指定下一步办理人的站点
     * @param userInfo        当前用户信息
     * @throws RuntimeException
     */
    void execTaskWithProcess(String taskId, String ProcessInstId, String statusName, Set<UserInfo> operUser, UserInfo userInfo,
            ProcessFucExtParam extParam) throws RuntimeException;

    /**
     * 删除某个在办任务的待办列表
     * 
     * @description:          使用流程的。
     * @createDate: 2016-2-26
     * @param taskId          任务节点ID
     * @param ProcessInstId   任务实例ID
     * @param userInfo        当前操作用户信息
     * @throws RuntimeException :
     */
    void deleteProcess(String taskId, String ProcessInstId, UserInfo userInfo) throws RuntimeException;
    
    /**
     * 删除任务
     * 
     * @description:          
     * @createDate: 2016-2-26
     * @param extCode         关联具体业务的id/流水号
     * @param userInfo :      当前用户信息
     */
    void delete(String extCode, UserInfo userInfo) throws RuntimeException;

    /**
     * 完成任务
     * 
     * @description: 
     * @createDate: 2016-2-26
     * @param extCode          关联具体业务的id,有业务模块提供，可以是流程实例id，也可以是业务模块自定义的id
     * @param userInfo         用户信息
     * @param statusName       状态名称
     * @throws RuntimeException :
     */
    void complete(String extCode, UserInfo userInfo, String statusName) throws RuntimeException;

    /**
     * 修改一个任务内容
     * 
     * @description:
     * @createDate: 2016-2-26
     * @param id               待办任务主键
     * @param name             任务名称
     * @param userInfo         用户信息
     * @throws RuntimeException
     * 
     * @return                 无
     */
    void modify(String id, String name, UserInfo userInfo) throws RuntimeException;
    
    /**
     * 查询待办
     * 
     * @description:          供手动执行任务使用
     * @createDate: 2016-2-26
     * param extCode          关联具体业务的id
     * @param userInfo        用户信息
     * 
     * @return                待办任务明细
     */
    WorktaskBean queryTask(String extCode,UserInfo userInfo);
    
    /**
     * 手动创建一个任务
     * 
     * @description:          
     * @createDate: 2016-2-26
     * @param flow            流水号
     * @param extcode         关联具体业务的id,由业务模块提供,需要能保证唯一。
     * @param typeName        任务类别名称,如采购申请、采购单等
     * @param name            任务名称
     * @param statusName      状态名称,如部门经理审批等
     * @param url             点击时的跳转路径
     * @param List<UserInfo>  下一步执行人
     * @param userInfo        当前操作用户信息
     * @param extParam        辅助参数
     * @return
     * @throws RuntimeException :
     */
    String createTask(String flowNo, String extCode, String typeName, String name, String statusName,
            String url, List<UserInfo> nextUserList, UserInfo userInfo, ProcessFucExtParam extParam)
            throws RuntimeException;
    
    /**
     * 删除待办
     * 
     * @description:         供手动执行任务使用
     * @createDate: 2016-2-26
     * @param extCode        关联具体业务的id
     * 
     * @return               无
     */
    void deleteTask(String extCode,UserInfo userInfo);
}
