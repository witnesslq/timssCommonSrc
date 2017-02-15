package com.yudean.interfaces.interfaces;

import com.yudean.itc.dto.interfaces.eip.mobile.ParamDetailBean;
import com.yudean.itc.dto.interfaces.eip.mobile.ParamProcessBean;
import com.yudean.itc.dto.interfaces.eip.mobile.RetContentBean;
import com.yudean.itc.dto.interfaces.eip.mobile.RetProcessBean;

/**
 * EIP移动端接口
 * @company: gdyd
 * @className: EipMobileInterface.java
 * @author: kChen
 * @createDate: 2014-9-21
 * @updateUser: kChen
 * @version: 1.0
 */
public interface EipMobileInterface {
	/**
	 * 根据参数获取流程表单详情
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-21
	 * @param bean
	 * @return:
	 */
	RetContentBean retrieveWorkflowFormDetails(ParamDetailBean bean);
	
	/**
	 * 处理流程
	 * @description:
	 * @author: kChen
	 * @createDate: 2014-9-21
	 * @param dto
	 * @return:
	 */
	RetProcessBean processWorkflow(ParamProcessBean bean);
}
