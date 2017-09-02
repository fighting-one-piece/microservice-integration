package org.cisiondata.modules.transfer.utils;

import java.util.HashMap;
import java.util.Map;

import org.cisiondata.utils.http.HttpUtils;
import org.cisiondata.utils.param.ParamsUtils;

import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

public class RemoteInvokeUtils {
	
	private static final String PREFIX = "https://api.cisiondata.cn/devplat/ext/api/v1/";
	
	private static final String LOGISTICS_URL = PREFIX + "labels/indices/financial/types/logistics?query=%s&scrollId=%s&rowNumPerPage=%s&accessId=%s&token=%s";
	
	public void readLogisticsDataList() {
		
		
	}
	
	public static void main(String[] args) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("query", "12345678");
		params.put("scrollId", "1");
		params.put("rowNumPerPage", "10");
		String token = ParamsUtils.genToken(params);
		String url = String.format(LOGISTICS_URL, "12345678", "1", "10", ParamsUtils.APP_ID, token);
		System.err.println(url);
		String json = HttpUtils.sendGet(url);
		System.err.println(json);
		XMLSerializer xmlSerializer = new XMLSerializer();
        String xml = xmlSerializer.write(JSONSerializer.toJSON(json));
        System.err.println(xml);
	}
	
	
	
}
