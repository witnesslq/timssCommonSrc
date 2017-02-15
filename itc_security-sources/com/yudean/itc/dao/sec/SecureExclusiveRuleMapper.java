package com.yudean.itc.dao.sec;

import java.util.List;

import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.SecureExclusiveRule;

/**
 * @title: 权限例外功能Dao
 * @description: 权限例外功能Dao
 * @company: gdyd
 * @className: SecureExclusiveRuleMapper.java
 * @author: yuanzh
 * @createDate: 2016-3-11
 * @updateUser: yuanzh
 * @version: 1.0
 */
public interface SecureExclusiveRuleMapper {

    /**
     * @description:查询权限例外列表
     * @author: yuanzh
     * @createDate: 2016-3-11
     * @param page
     * @return:
     */
    List<SecureExclusiveRule> querySecExclusiveRuleList(Page<?> page);

    /**
     * @description:更新权限例外
     * @author: yuanzh
     * @createDate: 2016-3-14
     * @param secExclusiveRule:
     */
    void updateSecExclusiveRule(SecureExclusiveRule secExclusiveRule);

    /**
     * @description:插入权限例外
     * @author: yuanzh
     * @createDate: 2016-3-14
     * @param secExclusiveRule:
     */
    void insertSecExclusiveRule(SecureExclusiveRule secExclusiveRule);
}
