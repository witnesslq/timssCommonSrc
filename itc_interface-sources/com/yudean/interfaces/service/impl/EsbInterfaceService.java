package com.yudean.interfaces.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yudean.interfaces.service.IEsbInterfaceService;
import com.yudean.interfaces.service.impl.esb.EsbWebService;
import com.yudean.itc.code.ParamConfig;
import com.yudean.itc.dto.interfaces.esb.CompBean;
import com.yudean.itc.dto.interfaces.esb.DeptBean;
import com.yudean.itc.dto.interfaces.esb.EmpBean;
import com.yudean.itc.dto.interfaces.esb.ErpPutParam;
import com.yudean.itc.dto.interfaces.esb.SupBean;
import com.yudean.itc.util.map.MapHelper;

@Service
public class EsbInterfaceService implements IEsbInterfaceService {

	@Autowired
	EsbWebService esbWebSercice;

	@Override
	public List<CompBean> getAllComp() throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return parse(esbWebSercice.getOrgDao().getAllComp(), CompBean.class);
	}

	@Override
	public CompBean getCompByCode(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		List<CompBean> retList = parse(esbWebSercice.getOrgDao().getByCompCode(code), CompBean.class);
		if (null != retList && !retList.isEmpty()) {
			return retList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public CompBean getCompByShortCode(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		List<CompBean> retList = parse(esbWebSercice.getOrgDao().getTargetName(code), CompBean.class);
		if (null != retList && !retList.isEmpty()) {
			return retList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<DeptBean> getChildDeptByComp(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return parse(esbWebSercice.getOrgDao().getChildrenCompCode(code), DeptBean.class);
	}

	@Override
	public List<DeptBean> getChildDeptByDept(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return parse(esbWebSercice.getOrgDao().getChildrendeptCodeByDept(code), DeptBean.class);
	}

	@Override
	public DeptBean getDeptByCode(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		List<DeptBean> retList = parse(esbWebSercice.getOrgDao().getByDeptcode(code), DeptBean.class);
		if (null != retList && !retList.isEmpty()) {
			return retList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<EmpBean> getEmpByCode(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return parse(esbWebSercice.getEmployeeDao().getEmpByNumCode(code), EmpBean.class);
	}

	@Override
	public List<EmpBean> getEmpByDepCode(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return parse(esbWebSercice.getEmployeeDao().getEmpByDepCode(code), EmpBean.class);
	}

	@Override
	public List<SupBean> getAllSupplier(String code) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return parse(esbWebSercice.getSupplierDao().getSupByCompCode(code), SupBean.class);
	}

	@Override
	public String getErpControlID() throws Exception {
		HttpRequest httpRequest = HttpRequest.get(esbWebSercice.getErpControlIdUrl());
		HttpResponse response = httpRequest.send();
		return response.body();
	}

	@SuppressWarnings("deprecation")
	@Override
	public String purErpData(ErpPutParam erpPutParam) throws Exception {
		if (null == erpPutParam.getServiceID()) {
			erpPutParam.setServiceID(esbWebSercice.getErpServiceId());
		}
		if (null == erpPutParam.getToken()) {
			erpPutParam.setToken(esbWebSercice.getErpServiceTOKEN());
		}
		return esbWebSercice.getERPPutService().putErpData(erpPutParam.getServiceID(), erpPutParam.getToken(), erpPutParam.getTransactionID(), erpPutParam.getPackage(),
				erpPutParam.getTotalPackages(), erpPutParam.getRollback(), erpPutParam.getForceReplace(), erpPutParam.getData());
	}

	private <T> List<T> parse(String StrXML, Class<T> t) throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		List<T> retList = new ArrayList<T>();
		Document doc = DocumentHelper.parseText(StrXML);
		Element root = doc.getRootElement();
		Element datalist = root.element(ParamConfig.ESB_PARSE_XML_NODENAME);
		for (@SuppressWarnings("rawtypes")
		Iterator it = datalist.elementIterator(); it.hasNext();) {
			Element data = (Element) it.next();
			Map<String, Object> curMap = new HashMap<String, Object>();
			for (@SuppressWarnings("rawtypes")
			Iterator p = data.elementIterator(); p.hasNext();) {
				Element curP = (Element) p.next();
				curMap.put(curP.getName(), curP.getText());
			}
			T object = MapHelper.map2Bean(curMap, t);
			retList.add(object);
		}
		return retList;
	}
}
