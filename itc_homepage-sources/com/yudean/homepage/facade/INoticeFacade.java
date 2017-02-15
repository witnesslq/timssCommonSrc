package com.yudean.homepage.facade;

import java.util.List;

import com.yudean.homepage.bean.HomepageWorkTask;
import com.yudean.mvc.bean.userinfo.UserInfo;

public interface INoticeFacade {

	 /**
     * 新增通知
     * 
     * @description:
     * @createDate: 2016-2-26
     * @param name         通知名称
     * @param url          跳转路径
     * @param operUser     通知人员列表，String为用户id
     * @param userInfo     当前用户信息
     * @return: 
     */
    void createNotice(String name,String url, String modelName,List<String> operUser, UserInfo userInfo);

}
