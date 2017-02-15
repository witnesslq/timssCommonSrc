package com.yudean.itc.servlet;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.yudean.itc.util.ApplicationConfig;
import com.yudean.itc.util.Constant;

public class ApplicationStartListener implements ServletContextListener {

	private static final Logger log = Logger.getLogger(ApplicationStartListener.class);

	@Override
    public void contextInitialized(ServletContextEvent arg0) {
		long ts1 = System.currentTimeMillis();
		// 初始化枚举类
		ServletContext servletContext = arg0.getServletContext();  		
		try {
			String uploadDir = ApplicationConfig.getConfig("upload.dir");
			if(uploadDir.equals("@")){
				//合成文件上传的目录 或者直接使用配置文件中的
				Constant.basePath = servletContext.getRealPath("/");
				if(Constant.basePath.endsWith("/") || Constant.basePath.endsWith("\\")){
					
				}
				else{
					Constant.basePath += File.separator;
				}
				Constant.basePath = Constant.basePath + "upload" + File.separator + "file";
			}
			else{
				Constant.basePath = uploadDir;
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		long ts2 = System.currentTimeMillis();
		log.warn("\r\n========================================" + "\r\n权限模块初始化完成（耗时：" + (ts2 - ts1) + "毫秒）"
				+ "\r\n========================================");
	}

	@Override
    public void contextDestroyed(ServletContextEvent arg0) {
		log.warn("\r\n========================================" + "\r\nWEB CONTEXT DESTORYED !" + "\r\n========================================");
	}

}