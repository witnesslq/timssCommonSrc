package com.yudean.itc.ldap;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ApplicationConstants {

	private static final String CONFIG_FILE = "config.properties";
	private static Map<String, Object> configs = new HashMap<String, Object>();
	
	
	private static final Logger log = Logger.getLogger(ApplicationConstants.class);
	static {
		InputStream in = null;
		Properties p = new Properties();
		try{
			in = currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE);		
			p.load(in);
			for(Object k : p.keySet()){
				String key = (String) k;
				configs.put( key, p.getProperty(key));
			}
			log.info("config.properties is loaded!"  );
		} catch (IOException e){
			log.error("Unable to read config.properties");				
		} finally{
			if(in != null)
				try {
					in.close();
				} catch (IOException e) {
					log.error("Unable to close inputstream");		
				}
		}
	}
	
	public static String getConfig(String key){
		return (String) configs.get(key);
	}
	public static  String buildADPath(String userName) {
		String adPathTemplate = getConfig("ad.path.template");
		if (isBlank(adPathTemplate)) {
			log.error("ad.path template do not exist in config.properties please config it");
			return null;
		}
		log.debug("ad.path template is "+adPathTemplate);
		try {
			String adPath = format(adPathTemplate, userName);
			log.debug("adPath is:"+adPath);
			return adPath;
		} catch (Exception e) {
			log.error("ad path template format error");
			return null;
		}
		
	}
	
}
