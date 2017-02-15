package com.yudean.mvc.listener;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import com.yudean.mvc.configs.init.MvcWebConfigInit;
import com.yudean.mvc.interfaces.InitClassAfterContextBuildInterface;
import com.yudean.mvc.service.ItcMvcService;
import com.yudean.mvc.util.UtilInitProcess;
import com.yudean.itc.OrgTreeUtil;
import com.yudean.itc.dto.support.AppEnum;
import com.yudean.itc.manager.support.IEnumerationManager;


public class InitItcFramework implements InitClassAfterContextBuildInterface {

	private static final Logger log = Logger.getLogger(InitItcFramework.class);
	
	@Override
	public void initClass(ApplicationContext context) throws Exception {
		ItcMvcService itcMvcService = context.getBean(ItcMvcService.class);
		initItcClass(itcMvcService);
	}
	
	public void initItcClass(ItcMvcService itcMvcService) throws Exception {
		OrgTreeUtil.buildOrgTree();
		//初始化枚举类
		try{
			IEnumerationManager enummerationManager = itcMvcService.getBeans(IEnumerationManager.class);
			List<AppEnum> enums = enummerationManager.retriveAllEnumerations();	
			UtilInitProcess.InitTimssEnumUtil(enums);
			log.debug("初始化枚举映射完毕");
		} catch (Exception e) {
			log.error("初始化枚举变量异常", e);
		}finally{
			
		}
		/**
		 * 初始化前端配置
		 */
		MvcWebConfigInit.init();
	}
}