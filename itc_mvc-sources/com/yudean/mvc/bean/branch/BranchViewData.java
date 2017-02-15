package com.yudean.mvc.bean.branch;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 探查分支页面内部数据POJO，只在框架层内部使用，非接口
 * @author kChen
 *
 */

@Component("core_service_framework_ToolBranchViewData")
@Scope("prototype")
public class BranchViewData {
	/**
	 * 是否存在分支版本
	 */
	public boolean isHasBranchView;
	/**
	 * 分支版本路径
	 */
	public String BranchViewPath;
}
