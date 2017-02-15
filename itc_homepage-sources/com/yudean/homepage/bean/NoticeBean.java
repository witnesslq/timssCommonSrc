package com.yudean.homepage.bean;

import java.util.Date;

import com.yudean.itc.code.StatusCode;
import com.yudean.itc.dto.DataSecuredDTO;

/**
 * 门户最新动态、通知Bean
 * 
 * @author kchen
 */
public class NoticeBean extends DataSecuredDTO {

    private static final long serialVersionUID = 7479007317912769417L;

    /**
     * 通知、完成
     * 
     * @author kchen
     */
    public enum Status {
        Notice, Complete, Info, Warning
    }

    private String code;
    private String content;
    private StatusCode active;
    private Status status;
    private Date statusdate;
    private String userid;
    private String operUrl;
    private String statusName;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getStatusdate() {
        return statusdate;
    }

    public void setStatusdate(Date statusdate) {
        this.statusdate = statusdate;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getOperUrl() {
        return operUrl;
    }

    public void setOperUrl(String operUrl) {
        this.operUrl = operUrl;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StatusCode getActive() {
        return active;
    }

    public void setActive(StatusCode active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "NoticeBean [code=" + code + ", content=" + content + ", status=" + status + ", statusdate="
                + statusdate + ", userid=" + userid + ", operUrl=" + operUrl + ", statusName=" + statusName + "]";
    }
}
