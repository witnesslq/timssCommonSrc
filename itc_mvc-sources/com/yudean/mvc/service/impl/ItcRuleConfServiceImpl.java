package com.yudean.mvc.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yudean.itc.dao.sec.SecureExclusiveRuleMapper;
import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.sec.SecureExclusiveRule;
import com.yudean.itc.helper.RowFilterHelper;
import com.yudean.mvc.bean.userinfo.UserInfoScope;
import com.yudean.mvc.service.ItcRuleConfService;

/**
 * @title: 权限例外功能接口
 * @description: 权限例外功能接口
 * @company: gdyd
 * @className: ItcRuleConfServiceImpl.java
 * @author: yuanzh
 * @createDate: 2016-3-11
 * @updateUser: yuanzh
 * @version: 1.0
 */
@Service
public class ItcRuleConfServiceImpl implements ItcRuleConfService {

    @Autowired
    private SecureExclusiveRuleMapper secureExclusiveRuleMapper;

    @Override
    public Page<SecureExclusiveRule> querySecExclusiveRuleList(UserInfoScope userInfo,
            SecureExclusiveRule secExclusiveRule) throws Throwable {

        UserInfoScope scope = userInfo;
        Page<SecureExclusiveRule> page = scope.getPage();
        try {
            String sort = String.valueOf( scope.getParam( "sort" ) == null ? "" : scope.getParam( "sort" ) );
            String order = String.valueOf( scope.getParam( "order" ) == null ? "" : scope.getParam( "order" ) );
            if ( !"".equals( sort ) && !"".equals( order ) ) {
                page.setSortKey( sort );
                page.setSortOrder( order );
            } else {
                page.setSortKey( "RULE_ID" );
                page.setSortOrder( "ASC" );
            }

            if ( null != secExclusiveRule ) {
                page.setParameter( "ruleId", secExclusiveRule.getRuleId() );
                page.setParameter( "roles", secExclusiveRule.getRoles() );
            }

            List<SecureExclusiveRule> ret = secureExclusiveRuleMapper.querySecExclusiveRuleList( page );
            page.setResults( ret );
        } catch (Exception e) {
            throw new Exception(
                    "----------------- ItcRuleConfServiceImpl 的  querySecExclusiveRuleList 方法抛出异常 ----------------- ",
                    e );
        }

        return page;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveSecExclusiveRule(UserInfoScope userInfo, List<SecureExclusiveRule> serList) throws Throwable {
        List<SecureExclusiveRule> serDbList = new ArrayList<SecureExclusiveRule>();
        SecureExclusiveRule serExChange = new SecureExclusiveRule();
        for ( SecureExclusiveRule ser : serList ) {
            serExChange.setRuleId( ser.getRuleId() );
            serDbList = querySecExclusiveRuleList( userInfo, serExChange ).getResults();
            if ( null != serDbList && !serDbList.isEmpty() ) {
                secureExclusiveRuleMapper.updateSecExclusiveRule( ser );
            } else {
                secureExclusiveRuleMapper.insertSecExclusiveRule( ser );
            }
        }
        // 数据更新完毕之后刷新一下内存
        RowFilterHelper.getFreshExclusiveRules();
    }
}
