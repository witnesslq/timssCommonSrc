package com.yudean.homepage.bean;

import java.util.Date;

import com.yudean.itc.annotation.AutoGen;
import com.yudean.itc.annotation.AutoGen.GenerationType;
import com.yudean.mvc.bean.ItcMvcBean;

/**
 * 首页(工作任务模块的Bean),对应DB中工作任务数据对象
 * 
 * @title: {title}
 * @description: {desc}
 * @company: gdyd
 * @className: homePageMessionBean.java
 * @author: kChen
 * @createDate: 2014-6-30
 * @updateUser: kChen
 * @version: 1.0
 */
public class WorktaskBean extends ItcMvcBean {
    /**
     * 任务列别，对应枚举变量的hop_classtype
     * 
     * @title: {title}
     * @description: {desc}
     * @company: gdyd
     * @className: WorkTaskBean.java
     * @author: kChen
     * @createDate: 2014-6-30
     * @updateUser: kChen
     * @version: 1.0
     */
    static public enum WorkTaskClass {
        /**
         * 草稿
         */
        Draft,
        /**
         * 在办
         */
        Processed,
        /**
         * 办毕
         */
        Complete,
        /**
         * 删除
         */
        Delete
    }

    static public enum ACTIVEFLAG {
        /**
         * 启用
         */
        ACTIVE,
        /**
         * 停用
         */
        NO
    }

    /**
     * URL类别
     * 
     * @title: {title}
     * @description: {desc}
     * @company: gdyd
     * @className: WorktaskBean.java
     * @author: kChen
     * @createDate: 2014-7-8
     * @updateUser: kChen
     * @version: 1.0
     */
    static public enum WorkTaskURLType {
        Tab
    }

    /**
     * 任务类别 主任务、子任务
     * 
     * @author kchen
     */
    static public enum TaskType {
        Main, Sub
    }

    /**
     * 自动生成的序列号
     */
    private static final long serialVersionUID = -8582598826588843449L;
    private String id;
    @AutoGen(value = "HOP_SEQ", requireType = GenerationType.REQUIRED_NULL)
    private String flowno;
    private String name;
    private String typename;
    private String statusname;
    private Date statusdate;
    private String createusername;
    private String modifyusername;
    private String siteid;
    private String deptid;
    private String deptname;
    private WorkTaskClass classtype;
    private String url;
    private WorkTaskURLType urltype;
    @AutoGen(value = "HOP_EXT_SEQ", requireType = GenerationType.REQUIRED_NULL)
    private String extCode;
    private String parentExtCode;
    private String groupid;
    private ACTIVEFLAG active;
    private TaskType taskType;

    /**
     * 获取任务逻辑主键
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @return:
     */
    public String getId() {
        return id;
    }

    /**
     * 设置任务逻辑主键
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @param id :
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取流水号
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @return:
     */
    public String getFlowno() {
        return flowno;
    }

    /**
     * 设置流水号
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @param flowno :
     */
    public void setFlowno(String flowno) {
        this.flowno = flowno;
    }

    /**
     * 获取任务名称
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @return:
     */
    public String getName() {
        return name;
    }

    /**
     * 设置任务名称
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @param name :
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取任务类别名称
     * 
     * @description:类别名称在枚举变量中定义，枚举类为hop_classtype
     * @author: kChen
     * @createDate: 2014-6-30
     * @return:
     */
    public String getTypename() {
        return typename;
    }

    /**
     * 设置任务类别名称
     * 
     * @description:类别名称在枚举变量中定义，枚举类为hop_classtype
     * @author: kChen
     * @createDate: 2014-6-30
     * @param typename :
     */
    public void setTypename(String typename) {
        this.typename = typename;
    }

    /**
     * 获取状态名称
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @return:
     */
    public String getStatusname() {
        return statusname;
    }

    /**
     * 设置状态名称
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @param statusname :
     */
    public void setStatusname(String statusname) {
        this.statusname = statusname;
    }

    /**
     * 获取状态日期
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @return:
     */
    public Date getStatusdate() {
        return statusdate;
    }

    /**
     * 设置状态日期
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @param statusdate :
     */
    public void setStatusdate(Date statusdate) {
        this.statusdate = statusdate;
    }

    /**
     * 获取创建用户名称
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @return:
     */
    public String getCreateusername() {
        return createusername;
    }

    /**
     * 获取创建用户名称
     * 
     * @description:获取创建用户
     * @author: kChen
     * @createDate: 2014-6-30
     * @return:
     */
    public void setCreateusername(String createusername) {
        this.createusername = createusername;
    }

    /**
     * 获取修改人名称
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @return:
     */
    public String getModifyusername() {
        return modifyusername;
    }

    /**
     * 设置修改人名称
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @param modifyusername :
     */
    public void setModifyusername(String modifyusername) {
        this.modifyusername = modifyusername;
    }

    /**
     * 获取站点信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @return:
     */
    public String getSiteid() {
        return siteid;
    }

    /**
     * 设置站点信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @param siteid :
     */
    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }

    /**
     * 获取部门信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @return:
     */
    public String getDeptid() {
        return deptid;
    }

    /**
     * 设置部门信息
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @param deptid :
     */
    public void setDeptid(String deptid) {
        this.deptid = deptid;
    }

    public String getDeptname() {
        return deptname;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    /**
     * 任务类别（待办、已办、草稿等）
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @return:
     */
    public WorkTaskClass getClasstype() {
        return classtype;
    }

    /**
     * 任务类别（待办、已办、草稿等）
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-6-30
     * @return:
     */
    public void setClasstype(WorkTaskClass classtype) {
        this.classtype = classtype;
    }

    /**
     * 获取URL路径
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-8
     * @return:
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置URL路径
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-8
     * @param url :
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 设置URL类型
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-8
     * @param url :
     */
    public WorkTaskURLType getUrltype() {
        return urltype;
    }

    /**
     * 获取URL类型
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-8
     * @param url :
     */
    public void setUrltype(WorkTaskURLType urltype) {
        this.urltype = urltype;
    }

    /**
     * 获取扩展编码
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-21
     * @return:
     */
    public String getExtCode() {
        return extCode;
    }

    /**
     * 设置扩展编码
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-21
     * @param extCode :
     */
    public void setExtCode(String extCode) {
        this.extCode = extCode;
    }

    /**
     * 获取子实例ID
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-28
     * @return:
     */
    public String getParentExtCode() {
        return parentExtCode;
    }

    /**
     * 设置子实例ID
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-28
     * @param subExtCode :
     */
    public void setParentExtCode(String parentExtCode) {
        this.parentExtCode = parentExtCode;
    }

    /**
     * 获取分组ID
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-28
     * @return:
     */
    public String getGroupid() {
        return groupid;
    }

    /**
     * 设置分组ID
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-28
     * @param groupid :
     */
    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    /**
     * 启用停用标志
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-28
     * @return:
     */
    public ACTIVEFLAG getActive() {
        return active;
    }

    /**
     * 启用停用标志
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-7-28
     * @param active :
     */
    public void setActive(ACTIVEFLAG active) {
        this.active = active;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    @Override
    public String toString() {
        return "WorktaskBean [id=" + id + ", flowno=" + flowno + ", name=" + name + ", typename=" + typename
                + ", statusname=" + statusname + ", statusdate=" + statusdate + ", createusername=" + createusername
                + ", modifyusername=" + modifyusername + ", siteid=" + siteid + ", deptid=" + deptid + ", deptname="
                + deptname + ", classtype=" + classtype + ", url=" + url + ", urltype=" + urltype + ", extCode="
                + extCode + ", parentExtCode=" + parentExtCode + ", groupid=" + groupid + ", active=" + active
                + ", taskType=" + taskType + "]";
    }

}
