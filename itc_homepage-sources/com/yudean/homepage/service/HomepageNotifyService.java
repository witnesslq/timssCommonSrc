package com.yudean.homepage.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface HomepageNotifyService {
    /**
     * 删除草稿、同步
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-11-13:
     */
    void notifySync(Method method, Object obj, Object... params) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException;

    /**
     * 删除草稿、异步
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-11-13:
     */
    void notifyAsync(Method method, Object obj, Object... params) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException;
}
