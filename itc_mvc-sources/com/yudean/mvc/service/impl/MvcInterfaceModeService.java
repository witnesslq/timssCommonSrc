package com.yudean.mvc.service.impl;

import org.springframework.stereotype.Service;

import com.yudean.itc.dto.interfaces.eip.TransferData;
import com.yudean.itc.util.EncryptUtil;
import com.yudean.itc.util.json.JsonHelper;
import com.yudean.mvc.service.IMvcInterfaceModeService;

@Service
public class MvcInterfaceModeService implements IMvcInterfaceModeService {

	@Override
	public String processInterfaceMode(String mode, String data) throws Exception {
		String retUrl = "";
		String stext = EncryptUtil.UrlBase64Decode(data);
		TransferData transData = JsonHelper.fromJsonStringToBean(stext, TransferData.class);
		retUrl = transData.getUrl();
		return retUrl;
	}
}
