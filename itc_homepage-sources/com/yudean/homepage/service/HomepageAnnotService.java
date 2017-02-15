package com.yudean.homepage.service;

import java.lang.reflect.InvocationTargetException;

import com.yudean.homepage.bean.DeleteDraftParam;

/**
 * 首页注解处理
 * 
 * @author kchen
 */
public interface HomepageAnnotService {

    /**
     * 删除草稿的注解处理
     * 
     * @param deleteParam
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    void deleteDraftNotify(DeleteDraftParam deleteParam) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException;
}
