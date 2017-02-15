package com.yudean.mvc.util.jsonprocess;

import java.text.SimpleDateFormat;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class JsonDateValueProcessor implements JsonValueProcessor {
	private String format ="yyyy-MM-dd HH:mm:ss";
	
	public JsonDateValueProcessor() {
		super();
	}
	
	public JsonDateValueProcessor(String format) {
		super();
		this.format = format;
	}

	@Override
	public Object processArrayValue(Object paramObject,JsonConfig paramJsonConfig) {
		return process(paramObject);
	}

	@Override
	public Object processObjectValue(String paramString, Object paramObject,JsonConfig paramJsonConfig) {
		return process(paramObject);
	}
	
	
	private Object process(Object value){
		if (value instanceof java.sql.Date || value instanceof java.sql.Timestamp || value instanceof java.util.Date) {
			SimpleDateFormat df = new SimpleDateFormat(format);
			Long time = df.getCalendar().getTimeInMillis();
	        return time;
		}
        return value == null ? "" : value.toString();  
    }

}

