package com.yudean.interfaces.service.impl.esb;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.gdyd.esb.services.v2.employee.EmployeeDAO;
import com.gdyd.esb.services.v2.employee.EmployeeDAOService;
import com.gdyd.esb.services.v2.org.OrgDAO;
import com.gdyd.esb.services.v2.org.OrgDAOService;
import com.gdyd.esb.services.erp.put.ERPPutService;
import com.gdyd.esb.services.erp.put.ERPPutServiceService;
import com.gdyd.esb.services.supplier.SupplierDAO;
import com.gdyd.esb.services.supplier.SupplierDAOService;
import com.yudean.interfaces.config.ESBInterfaecConfig;
import com.yudean.itc.dto.support.Configuration;
import com.yudean.itc.manager.support.IConfigurationManager;

/**
 * 统一数据交换平台(ESB)接口配置参数
 * 
 * @company: gdyd
 * @className: EsbConfig.java
 * @author: kChen
 * @createDate: 2014-9-26
 * @updateUser: kChen
 * @version: 1.0
 */
@Component
@Lazy(false)
public class EsbWebService implements ApplicationContextAware {
	static final private Logger log = Logger.getLogger(EsbWebService.class);
	private String confUrl;// 服务地址
	private String confOrgServiceName;// 部门、公司信息服务
	private String confEmployServiceName;// 人事信息服务
	private String confOrgSupplierServiceName;// 商务网公司信息服务
	private String confOrgERPServiceName;// Erp接口服务

	private OrgDAO orgDao;
	private EmployeeDAO employeeDao;
	private SupplierDAO supplierDao;
	private ERPPutService erpPutService;

	private String erpControlIdUrl;// ERP服务编码获取接口

	private String erpServiceId;// ERP服务编码获取接口

	private String erpServiceTOKEN;// ERP传输接口

	/**
	 * 获取部门信息接口
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-26
	 * @return:
	 */
	public OrgDAO getOrgDao() {
		return orgDao;
	}

	/**
	 * 获取人事信息接口
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-26
	 * @return:
	 */
	public EmployeeDAO getEmployeeDao() {
		return employeeDao;
	}

	/**
	 * 获取商务网供应商信息接口
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-26
	 * @return:
	 */
	public SupplierDAO getSupplierDao() {
		return supplierDao;
	}

	/**
	 * 获取ERP服务接口
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-12-5
	 * @return:
	 */
	public ERPPutService getERPPutService() {
		return erpPutService;
	}

	/**
	 * 获取ERP服务编码URL
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-12-8
	 * @return:
	 */
	public String getErpControlIdUrl() {
		return erpControlIdUrl;
	}

	/**
	 * 获取ERP接口TOKEN
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-12-8
	 * @return:
	 */
	public String getErpServiceTOKEN() {
		return erpServiceTOKEN;
	}

	/**
	 * 获取ErpServiceID
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-12-8
	 * @return:
	 */
	public String getErpServiceId() {
		return erpServiceId;
	}

	/**
	 * 初始化webservice接口
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-26
	 * @param configurationManager
	 *            :
	 */
	public void init(IConfigurationManager configurationManager) {

		List<Configuration> confList = configurationManager.retrieveAll();
		for (Configuration config : confList) {
			ESBInterfaecConfig confType = ESBInterfaecConfig.none;
			try {
				confType = ESBInterfaecConfig.valueOf(config.getConf());
			} catch (IllegalArgumentException e) {
				continue;
			}
			switch (confType) {
			case interface_ESB_DNS: {
				confUrl = config.getVal();
				break;
			}
			case interface_ESB_EmployService: {
				confEmployServiceName = config.getVal();
				break;
			}
			case interface_ESB_OrgService: {
				confOrgServiceName = config.getVal();
				break;
			}
			case interface_ESB_SupplierService: {
				confOrgSupplierServiceName = config.getVal();
				break;
			}
			case interface_ESB_ERPPutService: {
				confOrgERPServiceName = config.getVal();
				break;
			}
			case interface_ESB_ErpControlIdURL: {
				erpControlIdUrl = config.getVal();
				break;
			}
			case interface_ESB_ErpServiceTOKEN: {
				erpServiceTOKEN = config.getVal();
				break;
			}
			case interface_ESB_ErpServiceID: {
				erpServiceId = config.getVal();
				break;
			}
			default:
				break;
			}
		}
		if (checkNull()) {
			initOrgDao();
			initEmployeeDAO();
			initSupplierDAO();
			initERPPutService();
		}
	}

	private boolean checkNull() {
		boolean isRight = false;
		if (null == confUrl || "".equals(confUrl)) {
			log.error("初始化服务器地址失败，停用ESB相关接口");
		} else if (null == confOrgServiceName || "".equals(confOrgServiceName)) {
			log.error("初始化部门、公司信息服名称失败，停用ESB相关接口");
		} else if (null == confEmployServiceName || "".equals(confEmployServiceName)) {
			log.error("初始化人事信息服务服务名称失败，停用ESB相关接口");
		} else if (null == confOrgSupplierServiceName || "".equals(confOrgSupplierServiceName)) {
			log.error("初始化商务网公司信息服务名称失败，停用ESB相关接口");
		} else {
			isRight = true;
		}
		return isRight;
	}

	/**
	 * 初始化公司部门信息webservice接口
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-26:
	 */
	private void initOrgDao() {
		if (null != confOrgServiceName && !"NaN".equals(confOrgServiceName)) {
			try {
				URL wsdlURL = OrgDAOService.WSDL_LOCATION;
				QName SERVICE_NAME = new QName(confUrl, confOrgServiceName);
				OrgDAOService orgDaoSer = new OrgDAOService(wsdlURL, SERVICE_NAME);
				orgDao = orgDaoSer.getOrgDAOPort();
			} catch (Exception e) {
				log.error("链接orgDao失败", e);
			}
		}
	}

	/**
	 * 初始化人事信息webservice接口
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-26:
	 */
	private void initEmployeeDAO() {
		if (null != confEmployServiceName && !"NaN".equals(confEmployServiceName)) {
			try {
				URL wsdlURL = EmployeeDAOService.WSDL_LOCATION;
				QName SERVICE_NAME = new QName(confUrl, confEmployServiceName);
				EmployeeDAOService employDaoSer = new EmployeeDAOService(wsdlURL, SERVICE_NAME);
				employeeDao = employDaoSer.getEmployeeDAOPort();
			} catch (Exception e) {
				log.error("链接employeeDao失败", e);
			}
		}
	}

	/**
	 * 初始化商务网信息webservice接口
	 * 
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-26:
	 */
	private void initSupplierDAO() {
		if (null != confOrgSupplierServiceName && !"NaN".equals(confOrgSupplierServiceName)) {
			try {
				URL wsdlURL = SupplierDAOService.WSDL_LOCATION;
				QName SERVICE_NAME = new QName(confUrl, confOrgSupplierServiceName);
				SupplierDAOService supplierDAOService = new SupplierDAOService(wsdlURL, SERVICE_NAME);
				supplierDao = supplierDAOService.getSupplierDAOPort();
			} catch (Exception e) {
				log.error("链接supplierDao失败", e);
			}
		}
	}

	private void initERPPutService() {
		if (null != confOrgERPServiceName && !"NaN".equals(confOrgERPServiceName)) {
			try {
				URL wsdlURL = ERPPutServiceService.WSDL_LOCATION;
				QName SERVICE_NAME = new QName(confUrl, confOrgERPServiceName);
				ERPPutServiceService erpPutServiceService = new ERPPutServiceService(wsdlURL, SERVICE_NAME);
				erpPutService = erpPutServiceService.getERPPutServicePort();
			} catch (Exception e) {
				log.error("链接erpPutService失败", e);
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		IConfigurationManager configurationManager = applicationContext.getBean(IConfigurationManager.class);
		init(configurationManager);
	}
}
