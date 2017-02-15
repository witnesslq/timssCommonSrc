package com.yudean.mvc.util.helper;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.yudean.mvc.util.FormTokenUtil;

public class TokenSupervisor implements Serializable{
	private static final long serialVersionUID = -7129635198908320697L;
	private Map<Long, Long> TokenList;
	public TokenSupervisor(){
		TokenList = new HashMap<Long, Long>();
	}
	
	public Long build(){
		Long _token;
		if(FormTokenUtil.TOKEN_MAP_LEN > TokenList.size()){
			_token = (new Date().getTime() % 86400000) * 100 + Math.abs((int) (Math.random() * 100));
			TokenList.put(_token, System.currentTimeMillis());
		}else{
			Set<Entry<Long, Long>> set = TokenList.entrySet();
			Object[] dataList = set.toArray();
			int pos = (int)Math.round(Math.random() * (30 - 1));
			@SuppressWarnings("unchecked")
			Entry<Long, Long> entry = (Entry<Long, Long>)dataList[pos];
			_token = entry.getKey();
			TokenList.put(_token, System.currentTimeMillis());
		}
		return _token;
	}
	public boolean validToken(Long _token){
		boolean ret = false;
		Long timestamp = TokenList.get(_token);
		if(null != timestamp){
			if(FormTokenUtil.TOKEN_ACT_TIME > System.currentTimeMillis() - timestamp){
				ret = true;
			}
			TokenList.remove(_token);
		}
		return ret;
	}
}
