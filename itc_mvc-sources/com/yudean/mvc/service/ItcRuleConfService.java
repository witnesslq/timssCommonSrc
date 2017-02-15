package com.yudean.mvc.service;

import java.util.List;

import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.SecureExclusiveRule;
import com.yudean.mvc.bean.userinfo.UserInfoScope;

/**
 * @title: 权限例外功能接口
 * @description: 权限例外功能接口
 * @company: gdyd
 * @className: ItcRuleConfService.java
 * @author: yuanzh
 * @createDate: 2016-3-11
 * @updateUser: yuanzh
 * @version: 1.0
 */
public interface ItcRuleConfService {

    /**
     * @description:查询权限例外列表
     * @author: yuanzh
     * @createDate: 2016-3-11
     * @param userInfo
     * @param secExclusiveRule
     * @return
     * @throws Throwable:
     */
    Page<SecureExclusiveRule> querySecExclusiveRuleList(UserInfoScope userInfo, SecureExclusiveRule secExclusiveRule)
            throws Throwable;

    /**
     * @description: 保存权限例外列表信息
     * @author: yuanzh
     * @createDate: 2016-3-14
     * @param userInfo
     * @param serList:
     */
    void saveSecExclusiveRule(UserInfoScope userInfo, List<SecureExclusiveRule> serList) throws Throwable;
}
