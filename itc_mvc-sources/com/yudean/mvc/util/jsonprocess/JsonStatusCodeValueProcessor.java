package com.yudean.mvc.util.jsonprocess;

import com.yudean.itc.code.StatusCode;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class JsonStatusCodeValueProcessor implements JsonValueProcessor {
	public JsonStatusCodeValueProcessor() {
		super();
	}
	
	@Override
	public Object processArrayValue(Object arg0, JsonConfig arg1) {
		// TODO Auto-generated method stub
		if(null == arg0){
			return "";
		}else{
			return arg0;
		}
	}

	@Override
	public Object processObjectValue(String arg0, Object arg1, JsonConfig arg2) {
		// TODO Auto-generated method stub
		if(null == arg1){
			return "";
		}else{
			StatusCode sCode = (StatusCode)arg1;
			return sCode.toString();
		}
	}

}
