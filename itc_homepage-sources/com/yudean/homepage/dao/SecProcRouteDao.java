package com.yudean.homepage.dao;

import java.util.List;

import com.yudean.itc.dto.sec.SecProcRoute;

public interface SecProcRouteDao {

    /**
     * 查詢路由列表
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-10-23
     * @param secProcRoute
     * @return:
     */
    List<SecProcRoute> selectSecProcRoute(SecProcRoute secProcRoute);

    /**
     * 添加查詢類表
     * 
     * @description:
     * @author: kChen
     * @createDate: 2014-10-23
     * @param secProcRoute
     * @return:
     */
    int insertSecProcRoute(SecProcRoute secProcRoute);

    /**
     * 批量添加路由表信息
     * 
     * @param procRoutList
     * @return
     */
    int insertSecProcRouteBatch(List<SecProcRoute> procRoutList);
}
