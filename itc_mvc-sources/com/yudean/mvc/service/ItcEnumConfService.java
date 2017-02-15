package com.yudean.mvc.service;

import java.util.List;

import com.yudean.itc.dto.Page;
import com.yudean.itc.dto.support.AppEnum;
import com.yudean.itc.dto.support.AppEnumCat;
import com.yudean.itc.dto.support.AppEnumCatVO;
import com.yudean.mvc.bean.userinfo.UserInfoScope;

/**
 * @title: 枚举子类Service
 * @description: 枚举子类Service
 * @company: gdyd
 * @className: BEnumService.java
 * @author: yuanzh
 * @createDate: 2015-8-27
 * @updateUser: yuanzh
 * @version: 1.0
 */
public interface ItcEnumConfService {

    /**
     * @description: 查询枚举类型列表
     * @author: yuanzh
     * @createDate: 2015-8-27
     * @param userInfo
     * @param becv
     * @return:
     * @throws Exception
     * @throws Throwable
     */
    Page<AppEnumCatVO> queryBEnumCatList(UserInfoScope userInfo, AppEnumCatVO becv) throws Throwable;

    /**
     * @description: 枚举类型表单查询
     * @author: yuanzh
     * @createDate: 2015-8-27
     * @param userInfo
     * @param ecatCode
     * @return:
     * @throws Throwable
     */
    AppEnumCat queryBEnumCatByCode(UserInfoScope userInfo, String ecatCode) throws Throwable;

    /**
     * @description: 删除枚举变量
     * @author: yuanzh
     * @createDate: 2015-8-28
     * @param userInfo
     * @param ecatCode
     * @return
     * @throws Throwable:
     */
    boolean deleteEnum(UserInfoScope userInfo, String ecatCode) throws Exception;

    /**
     * @description: 保存枚举变量
     * @author: yuanzh
     * @createDate: 2015-8-28
     * @param userInfo
     * @param bec
     * @param beList
     * @return
     * @throws Throwable:
     */
    boolean saveEnumInfo(UserInfoScope userInfo, AppEnumCat bec, List<AppEnum> beList) throws Exception;

    /**
     * @description: 查询枚举子类列表
     * @author: yuanzh
     * @createDate: 2015-8-27
     * @param userInfo
     * @param ecatCode
     * @return:
     * @throws Throwable
     */
    Page<AppEnum> queryBEnumList(UserInfoScope userInfo, String ecatCode, String enumType) throws Throwable;

}
