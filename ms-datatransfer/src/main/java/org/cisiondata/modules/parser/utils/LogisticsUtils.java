package org.cisiondata.modules.parser.utils;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cisiondata.utils.exception.BusinessException;
import org.cisiondata.utils.http.HttpUtils;
import org.cisiondata.utils.json.GsonUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class LogisticsUtils {

	private static String URL = "https://api.cisiondata.cn/devplat/ext/api/v1";

	public static void parse(File src, String destDir) {
		Map<String, String> rootMap = parseXml(src);
		parseResult(destDir, rootMap, readResult(rootMap));
	}

	// 读取xml文件中的相关信息
	@SuppressWarnings("unchecked")
	private static Map<String, String> parseXml(File src) throws BusinessException {
		// 创建SAXReader的对象reader
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(src);
			Element rootElement = document.getRootElement();
			Map<String, String> map = new HashMap<>();
			List<Attribute> attributes = rootElement.attributes();
			for (int i = 0, len = attributes.size(); i < len; i++) {
				Attribute attr = attributes.get(i);
				String name = attr.getName();
				if ("ACCESSID".equals(name)) {
					name = "accessId";
				} else if ("LX".equals(name)) {
					name = "lx";
				} else if ("QUERY".equals(name)) {
					name = "query";
				} else if ("ROWNUMPERPAGE".equals(name)) {
					name = "rowNumPerPage";
				} else if ("SCROLLID".equals(name)) {
					name = "scrollId";
				} else if ("TOKEN".equals(name)) {
					name = "token";
				}
				map.put(name, attr.getValue());
			}
			return map;
		} catch (DocumentException e) {
			return null;
		}
	}

	// 获取到数据生成XML
	private static void createResultXml(String path, Map<String, String> rootMap, Map<String, String> tableMap,
			List<Map<String, String>> resultListMap) throws BusinessException {
		Document document = DocumentHelper.createDocument();// 建立document对象，用来操作xml文件
		Element root = document.addElement("XmlSet");// 建立根节点
		for (Map.Entry<String, String> map : rootMap.entrySet()) {
			root.addAttribute(map.getKey(), map.getValue());
		}
		Element table = root.addElement("XmlTable");
		for (Map.Entry<String, String> map : tableMap.entrySet()) {
			table.addAttribute(map.getKey(), map.getValue());
		}
		Element rows = null;
		for (int i = 0; i < resultListMap.size(); i++) {
			rows = table.addElement("rows");
			for (Map.Entry<String, String> map : resultListMap.get(i).entrySet()) {
				Element element = rows.addElement(map.getKey());
				element.setText(map.getValue());
			}
		}
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			XMLWriter writer = new XMLWriter(new FileWriter(new File(path)), format);
			writer.write(document);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 错误信息创建XML
	private static void createErrorResultXml(String path, Map<String, String> rootMap, Map<String, String> tableMap,
			List<Map<String, String>> resultListMap) throws BusinessException {
		Document document = DocumentHelper.createDocument();// 建立document对象，用来操作xml文件
		Element root = document.addElement("XmlSet");// 建立根节点
		for (Map.Entry<String, String> map : rootMap.entrySet()) {
			root.addAttribute(map.getKey(), map.getValue());
		}
		Element table = root.addElement("XmlTable");
		for (Map.Entry<String, String> map : tableMap.entrySet()) {
			table.addAttribute(map.getKey(), map.getValue());
		}
		Element rows = null;
		for (int i = 0; i < resultListMap.size(); i++) {
			rows = table.addElement("error");
			for (Map.Entry<String, String> map : resultListMap.get(i).entrySet()) {
				Element element = rows.addElement(map.getKey());
				element.setText(map.getValue());
			}
		}
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			XMLWriter writer = new XMLWriter(new FileWriter(new File(path)), format);
			writer.write(document);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 调取接口 获取数据
	private static String readResult(Map<String, String> map) throws BusinessException {
		StringBuffer params = new StringBuffer();
		boolean flag = true;
		for (Map.Entry<String, String> str : map.entrySet()) {
			if ("lx".equals(str.getKey())) {
				params.append("/labels/indices/financial/types/logistics");
			}
		}
		for (Map.Entry<String, String> str : map.entrySet()) {
			if (!"lx".equals(str.getKey())) {
				if (flag) {
					flag = false;
					params.append("?" + str.getKey() + "=" + str.getValue());
				} else {
					params.append("&" + str.getKey() + "=" + str.getValue());
				}
			}
		}
		return HttpUtils.sendGet(URL + params.toString());
	}

	// 对数据接口处理
	private static void parseResult(String destDir, Map<String, String> rootMap, String result)
			throws BusinessException {
		Map<String, Object> map = GsonUtils.fromJsonToMap(result);
		List<Map<String, String>> resultListMap = new ArrayList<>();
		Map<String, String> tableMap = new HashMap<>();
		String fileName = "CD-Q-" + rootMap.get("lx") + "-" + rootMap.get("query") + ".xml";
		String path = destDir + File.separator + fileName;
		if ("1".equals(map.get("code").toString())) {
			Map<String, Object> maps = GsonUtils.fromJsonToMap(result);
			Map<String, Object> mapResult = GsonUtils.fromJsonToMap(maps.get("data").toString());
			resultListMap = GsonUtils.fromJsonToList(mapResult.get("resultList").toString());
			tableMap.put("accessId", rootMap.get("accessId"));
			tableMap.put("query", rootMap.get("query"));
			tableMap.put("scrollId", mapResult.get("scrollId").toString());
			tableMap.put("totalRowNum", mapResult.get("totalRowNum").toString());
			createResultXml(path, rootMap, tableMap, resultListMap);
		} else {
			Map<String, String> resultMap = new HashMap<>();
			tableMap.put("accessId", rootMap.get("accessId"));
			tableMap.put("query", rootMap.get("query"));
			resultMap.put("code", map.get("code").toString());
			resultMap.put("failure", map.get("failure").toString());
			resultListMap.add(resultMap);
			createErrorResultXml(path, rootMap, tableMap, resultListMap);
		}
	}

}
