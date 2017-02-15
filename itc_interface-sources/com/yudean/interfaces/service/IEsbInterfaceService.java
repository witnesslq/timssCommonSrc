package com.yudean.interfaces.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.dom4j.DocumentException;

import com.yudean.itc.dto.interfaces.esb.CompBean;
import com.yudean.itc.dto.interfaces.esb.DeptBean;
import com.yudean.itc.dto.interfaces.esb.EmpBean;
import com.yudean.itc.dto.interfaces.esb.ErpPutParam;
import com.yudean.itc.dto.interfaces.esb.SupBean;

/**
 * 统一数据交换平台相关接口
 * 
 * @company: gdyd
 * @className: IEsbInterfaceService.java
 * @author: kChen
 * @createDate: 2014-9-29
 * @updateUser: kChen
 * @version: 1.0
 */
public interface IEsbInterfaceService {
	/**
	 * 获取所有公司信息列表
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-29
	 * @return
	 * @throws DocumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 *             :
	 */
	List<CompBean> getAllComp() throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException;

	/**
	 * 根据公司编号获取指定的公司信息
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-29
	 * @param code
	 * @return
	 * @throws DocumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 *             :
	 */
	CompBean getCompByCode(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException;

	/**
	 * 根据公司简写编码获取公司名称,如ITC、HYC等
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-29
	 * @param code
	 * @return
	 * @throws DocumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 *             :
	 */
	CompBean getCompByShortCode(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException;

	/**
	 * 根据公司编码获取子部门信息
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-29
	 * @param code
	 * @return
	 * @throws DocumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 *             :
	 */
	List<DeptBean> getChildDeptByComp(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException;

	/**
	 * 根据部门编码获取子部门信息
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-29
	 * @param code
	 * @return
	 * @throws DocumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 *             :
	 */
	List<DeptBean> getChildDeptByDept(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException;

	/**
	 * 根据编码获取部门信息
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-29
	 * @param code
	 * @return
	 * @throws DocumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 *             :
	 */
	DeptBean getDeptByCode(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException;

	/**
	 * 根据人员工号获取人员信息
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-29
	 * @param code
	 * @return 当某个人属于多个站点时，返回的列表包含1条以上信息
	 * @throws DocumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 *             :
	 */
	List<EmpBean> getEmpByCode(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException;

	/**
	 * 根据部门编码获取用户信息
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-29
	 * @param code
	 * @return
	 * @throws DocumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 *             :
	 */
	List<EmpBean> getEmpByDepCode(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException;

	/**
	 * 根据公司简称获取供应商信息(ITC,HYC)
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-29
	 * @throws DocumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 *             :
	 */
	List<SupBean> getAllSupplier(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException;

	/**
	 * 获取Erp ControlID 编码
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-12-8
	 * @return:
	 */
	String getErpControlID() throws Exception;

	/**
	 * ERP数据推送接口，相关参数参看ErpPutParam
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-12-8
	 * @param erpPutParam
	 * @return:
	 */
	String purErpData(ErpPutParam erpPutParam) throws Exception;
}
