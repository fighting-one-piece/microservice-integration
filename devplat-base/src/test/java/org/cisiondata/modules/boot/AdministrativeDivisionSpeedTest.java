package org.cisiondata.modules.boot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cisiondata.modules.address.service.impl.AddressServiceImpl;
import org.cisiondata.utils.file.DefaultLineHandler;
import org.cisiondata.utils.file.FileUtils;
import org.cisiondata.utils.http.HttpClientUtils;
import org.cisiondata.utils.json.GsonUtils;

public class AdministrativeDivisionSpeedTest {

	public static void speed() {
		try {
			AddressServiceImpl service = new AddressServiceImpl();
			service.initializing();
			List<String> lines = FileUtils.readFromAbsolute("F:\\document\\doc\\201704\\address100000.txt", new DefaultLineHandler());
			long startTime = System.currentTimeMillis();
			for (int i = 0, len = lines.size(); i < len; i++) {
				Map<String, Object> map = GsonUtils.fromJsonToMap(lines.get(i));
				List<String> words = service.read3AdministrativeDivision(String.valueOf(map.get("address")));
				System.out.println(words);
			}
			System.out.println(lines.size() + " spend time: " + (System.currentTimeMillis() - startTime) / 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void speed01() {
		try {
			List<String> lines = FileUtils.readFromAbsolute("F:\\document\\doc\\201704\\address100000.txt", new DefaultLineHandler());
			long startTime = System.currentTimeMillis();
			for (int i = 0, len = lines.size(); i < len; i++) {
				Map<String, Object> map = GsonUtils.fromJsonToMap(lines.get(i));
				extract3ADFromAddress(String.valueOf(map.get("address")));
//				List<String> words = extract3ADFromAddress(String.valueOf(map.get("address")));
//				System.out.println(words);
				if (i % 1000 == 0) {
					System.out.println(i + " spend time: " + (System.currentTimeMillis() - startTime) / 1000);
				}
			}
			System.out.println(lines.size() + " spend time: " + (System.currentTimeMillis() - startTime) / 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> extract3ADFromAddress(String address) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("address", address);
		String json = HttpClientUtils.sendGet("http://192.168.0.114:18080/ads", params, "UTF-8");
//		String json = HttpClientUtils.get("http://localhost:18080/ads", params, "UTF-8");
		Map<String, Object> map = GsonUtils.fromJsonToMap(json);
		if (null == map) return new ArrayList<String>();
		Object data = map.get("data");
		return null == data ? new ArrayList<String>() : GsonUtils.builder().fromJson((String) data, List.class);
	}
	
	public static void a() {
		List<String> lines1 = FileUtils.readFromClasspath("dictionary/administrative_division_2.dic", new DefaultLineHandler());
		Map<String, String> map1 = new HashMap<String, String>();
		for (int i = 0, len = lines1.size(); i < len; i++) {
			if (i % 2 != 0) map1.put(lines1.get(i), lines1.get(i - 1));
		}
		List<String> lines2 = FileUtils.readFromClasspath("dictionary/administrative_division_2_n.dic", new DefaultLineHandler());
		Map<String, String> map2 = new HashMap<String, String>();
		for (int i = 0, len = lines2.size(); i < len; i++) {
			if (i % 2 != 0) map2.put(lines2.get(i), lines2.get(i - 1));
		}
		Set<String> keys = map1.keySet();
		List<String> lines = new ArrayList<String>();
		for (Map.Entry<String, String> entry : map2.entrySet()) {
			if (!keys.contains(entry.getKey())) {
				lines.add(entry.getValue());
				lines.add(entry.getKey());
			}
		}
		FileUtils.write("F://a.txt", lines);
	}
	
	public static void b() {
		List<String> lines1 = FileUtils.readFromClasspath("dictionary/administrative_division_1_2.dic", new DefaultLineHandler());
		List<String> lines2 = FileUtils.readFromClasspath("dictionary/administrative_division_1_2_n.dic", new DefaultLineHandler());
		List<String> lines = new ArrayList<String>();
		for (int i = 0, len = lines2.size(); i < len; i++) {
			String line = lines2.get(i);
			if (!lines1.contains(line)) {
				lines.add(line);
			}
		}
		FileUtils.write("F://a.txt", lines);
	}
	
	public static void main(String[] args) {
		b();
//		speed01();
//		System.out.println(extract3ADFromAddress("澄迈"));
	}
	
}
