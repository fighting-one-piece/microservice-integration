package org.platform.modules.kyfw.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.platform.utils.http.HttpClientUtils;
import org.platform.utils.image.ImageUtils;
import org.platform.utils.json.GsonUtils;

public class CaptchaImageUtils {
	
	public static void main(String[] args) {
		recognizeImage("F:\\result\\imgs\\eb496be04ef8422b82e708eee1ef07ab.jpg");
	}
	
	public static void recognizeImage(String srcImg) {
		List<String> results = new ArrayList<String>();
		String destImgDir = System.getProperty("user.home");
		int width = 74, height = 76;
		for (int i = 0; i < 2; i++) {
			int y = 36 + i * 72;
			for (int j = 0; j < 4; j++) {
				int x = j * 72;
				String destImg = ImageUtils.cutImage(srcImg, destImgDir + File.separator + "cut", x, y, width, height);
				results.add(recognizeCutImage(destImg));
			}
		}
		for (String result : results) {
			System.err.println(result);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String recognizeCutImage(String srcImg) {
		String url1 = "http://image.baidu.com/pcdutu/a_upload?fr=html5&target=pcSearchImage&needJson=true";
		String resp1 = HttpClientUtils.sendPostWithFile(url1, srcImg, "UTF-8");
		Map<String, Object> result1 = GsonUtils.fromJsonToMap(resp1);
		String imgUrl = String.valueOf(result1.get("url"));
		String querySign = String.valueOf(result1.get("querySign"));
		String simid = String.valueOf(result1.get("simid"));
		String url2 = "http://image.baidu.com/pcdutu/a_similar?queryImageUrl=$imgUrl&querySign=$querySign&simid=$simid&word=&querytype=0&t=1514459504295&rn=60&sort=&fr=pc&pn=0";
		url2 = url2.replace("$imgUrl", imgUrl).replace("$querySign", querySign).replace("$simid", simid);
		String resp2 = HttpClientUtils.sendGet(url2, "UTF-8");
		Map<String, Object> result2 = GsonUtils.fromJsonToMap(resp2);
		if (result2.containsKey("result")) {
			Object resultObj = result2.get("result");
			List<Map<String, Object>> records = GsonUtils.fromJsonToList(String.valueOf(resultObj));
			for (int i = 0, len = records.size(); i < len; i++) {
				Map<String, Object> record = records.get(i);
				if (!record.containsKey("simid_info")) continue;
				Map<String, Object> simidInfo = (Map<String, Object>) record.get("simid_info");
				if (!simidInfo.containsKey("tags")) continue;
				Map<String, Object> tags = (Map<String, Object>) simidInfo.get("tags");
				/**
				if (tags.containsKey("keyword-cont")) {
					List<Map<String, Object>> keywordCont1s = (List<Map<String, Object>>) tags.get("keyword-cont");
					for (Map<String, Object> kc1 : keywordCont1s) {
						String keyword = (String) kc1.get("keyword");
					}
				}
				*/
				List<String> words = new ArrayList<String>();
				if (tags.containsKey("keyword-cont2")) {
					List<Map<String, Object>> keywordCont2s = (List<Map<String, Object>>) tags.get("keyword-cont2");
					for (Map<String, Object> kc2 : keywordCont2s) {
						String maxword = String.valueOf(kc2.get("maxword"));
						if (StringUtils.isNotBlank(maxword)) {
							words.addAll(Arrays.asList(maxword.split(" ")));
						}
					}
				}
				return String.join("|", words);
			}
		}
		return null;
	}

}
