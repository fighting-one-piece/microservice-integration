package org.cisiondata.modules.address.service.impl;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.cisiondata.modules.abstr.dao.GenericDAO;
import org.cisiondata.modules.abstr.entity.Query;
import org.cisiondata.modules.abstr.service.impl.GenericServiceImpl;
import org.cisiondata.modules.address.dao.AdministrativeDivisionDAO;
import org.cisiondata.modules.address.entity.AdministrativeDivision;
import org.cisiondata.modules.address.service.IAdministrativeDivisionService;
import org.cisiondata.utils.exception.BusinessException;
import org.cisiondata.utils.http.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("administrativeDivisionService")
public class AdministrativeDivisionServiceImpl extends GenericServiceImpl<AdministrativeDivision, Long> 
	implements IAdministrativeDivisionService {
	
	private static Logger LOG = LoggerFactory.getLogger(AdministrativeDivisionServiceImpl.class);
	
	@Resource(name = "administrativeDivisionDAO")
	private AdministrativeDivisionDAO administrativeDivisionDAO = null;
	
	@Override
	public GenericDAO<AdministrativeDivision, Long> obtainDAOInstance() {
		return administrativeDivisionDAO;
	}
	
	public void insertAdministrativeDivisionFromParser() throws BusinessException {
		String url = "http://www.stats.gov.cn/tjsj/tjbz/xzqhdm/201703/t20170310_1471429.html";
		try {
			String html = HttpUtils.sendGet(url);
//			Document document = Jsoup.connect(url).get();
			Document document = Jsoup.parse(html);
			Elements elements = document.select("p.MsoNormal");
			Iterator<Element> iterator = elements.iterator();
			List<String> lines = new ArrayList<String>();
			while (iterator.hasNext()) {
				Element element = iterator.next();
				lines.add(element.text());
			}
			List<AdministrativeDivision> administrativeDivisions = new ArrayList<AdministrativeDivision>();
			String topCode = null, secondCode = null;
			for (int i = 0, len = lines.size(); i < len; i++) {
				String[] kv = lines.get(i).trim().split(" ");
				String currentCode = kv[0].trim().replace("　", "").replace("　　", "").replace("    ", "");
				String region = kv[1].trim().replace("　", "").replace("　　", "").replace("    ", "");
				String fullCurrentCode = fill(currentCode, 12);
				AdministrativeDivision administrativeDivision = new AdministrativeDivision();
				administrativeDivision.setRegion(region);
				administrativeDivision.setCode(fullCurrentCode);
				if (currentCode.endsWith("0000")) {
					administrativeDivision.setParentCode("000000000000");
					topCode = currentCode;
				} else if (currentCode.endsWith("00")) {
					administrativeDivision.setParentCode(fill(topCode, 12));
					secondCode = currentCode;
				} else {
					administrativeDivision.setParentCode(fill(secondCode, 12));
					List<AdministrativeDivision> subAdministrativeDivisions = parseVillagesTowns(currentCode);
					System.out.println("subAdministrativeDivisions: " + subAdministrativeDivisions.size());
					if (subAdministrativeDivisions.size() > 0) {
						administrativeDivisionDAO.insertBatch(subAdministrativeDivisions);
					}
				}
				administrativeDivisions.add(administrativeDivision);
			}
			System.out.println("administrativeDivisions: " + administrativeDivisions.size());
			if (administrativeDivisions.size() > 0) {
				administrativeDivisionDAO.insertBatch(administrativeDivisions);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void insertAdministrativeDivisionDictionary() throws BusinessException {
		BufferedWriter bw = null;
		try {
			String dicPath = AdministrativeDivision.class.getClassLoader().getResource(
				"dictionary/administrative_division.dic").getPath();
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dicPath)));
			List<AdministrativeDivision> ads = readAdministrativeDivisionsByParentCode(AdministrativeDivision.ROOT);
			for (int i = 0, iLen = ads.size(); i < iLen; i++) {
				AdministrativeDivision ad = ads.get(i);
				bw.write(ad.getRegion());
				bw.newLine();
				List<AdministrativeDivision> sads = readAdministrativeDivisionsByParentCode(ad.getCode());
				for (int j = 0, jLen = sads.size(); j < jLen; j++) {
					AdministrativeDivision sad = sads.get(j);
					bw.write(sad.getRegion());
					bw.newLine();
					List<AdministrativeDivision> tads = readAdministrativeDivisionsByParentCode(sad.getCode());
					for (int k = 0, kLen = tads.size(); k < kLen; k++) {
						AdministrativeDivision tad = tads.get(k);
						bw.write(tad.getRegion());
						bw.newLine();
						List<AdministrativeDivision> foads = readAdministrativeDivisionsByParentCode(tad.getCode());
						for (int l = 0, lLen = foads.size(); l < lLen; l++) {
							AdministrativeDivision foad = foads.get(l);
							bw.write(foad.getRegion());
							bw.newLine();
							bw.write(foad.getRegion().replace("办事处", ""));
							bw.newLine();
							List<AdministrativeDivision> fiads = readAdministrativeDivisionsByParentCode(foad.getCode());
							for (int m = 0, mLen = fiads.size(); m < mLen; m++) {
								AdministrativeDivision fiad = fiads.get(m);
								bw.write(fiad.getRegion());
								bw.newLine();
								bw.write(fiad.getRegion().replace("居委会", ""));
								bw.newLine();
								bw.flush();
							}
						}
						bw.flush();
					}
				}
			}
			bw.flush();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if (null != bw) bw.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	@Override
	public List<AdministrativeDivision> readAdministrativeDivisionsByParentCode(String parentCode) 
			throws BusinessException {
		Query query = new Query();
		query.addCondition("parentCode", parentCode);
		return administrativeDivisionDAO.readDataListByCondition(query);
	}
	
	@SuppressWarnings("unused")
	private void parseProvince() throws Exception {
		String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2015/index.html";
		Document document = Jsoup.connect(url).get();
		Elements elements = document.select("tr.provincetr td");
		Iterator<Element> iterator = elements.iterator();
		while (iterator.hasNext()) {
			Element element = iterator.next();
			System.out.println(element.text());
		}
	}
	
	private List<AdministrativeDivision> parseVillagesTowns(String countyCode) {
		/**
		String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2015/11/01/110101.html";
		String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2015/44/02/440203.html";
		**/
		List<AdministrativeDivision> administrativeDivisions = new ArrayList<AdministrativeDivision>();
		String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2015/%s/%s/%s.html";
		url= String.format(url, countyCode.substring(0, 2), countyCode.substring(2, 4), countyCode);
		try {
			Document document = Jsoup.parse(new URL(url).openStream(), "GBK", url);
			Elements elements = document.select("tr.towntr");
			Iterator<Element> iterator = elements.iterator();
			while (iterator.hasNext()) {
				Element element = iterator.next();
				String content = element.text();
				String[] kv = content.split(" ");
				AdministrativeDivision administrativeDivision = new AdministrativeDivision();
				administrativeDivision.setRegion(kv[1]);
				administrativeDivision.setCode(kv[0]);
				administrativeDivision.setParentCode(fill(countyCode, 12));
				administrativeDivisions.add(administrativeDivision);
				administrativeDivisions.addAll(parseResidentsCommittee(kv[0].substring(0, 9)));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return administrativeDivisions;
	}
	
	private List<AdministrativeDivision> parseResidentsCommittee(String townCode) {
		/**
		String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2015/41/01/02/410102002.html";
		String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2015/51/01/04/510104020.html";
		**/
		List<AdministrativeDivision> administrativeDivisions = new ArrayList<AdministrativeDivision>();
		String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2015/%s/%s/%s/%s.html";
		url = String.format(url, townCode.substring(0, 2), townCode.substring(2, 4), townCode.substring(4, 6), townCode);
		try {
			Document document = Jsoup.parse(new URL(url).openStream(), "GBK", url);
			Elements elements = document.select("tr.villagetr");
			Iterator<Element> iterator = elements.iterator();
			while (iterator.hasNext()) {
				Element element = iterator.next();
				String content = element.text();
				String[] kv = content.split(" ");
				AdministrativeDivision administrativeDivision = new AdministrativeDivision();
				administrativeDivision.setRegion(kv[2]);
				administrativeDivision.setCode(kv[0]);
				administrativeDivision.setParentCode(fill(townCode, 12));
				administrativeDivisions.add(administrativeDivision);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return administrativeDivisions;
	}
	
	private String fill(String code, int length) {
		StringBuilder sb = new StringBuilder(code);
		for (int i = 0, len = length - sb.length(); i < len; i++) {
			sb.append("0");
		}
		return sb.toString();
	}
	
	@SuppressWarnings("unused")
	private void writeToDictionaryFile(List<AdministrativeDivision> ads, BufferedWriter bw) {
		if (null == ads || ads.size() == 0) return;
		try {
			for (int i = 0, iLen = ads.size(); i < iLen; i++) {
				AdministrativeDivision ad = ads.get(i);
				bw.write(ad.getRegion());
				bw.newLine();
				writeToDictionaryFile(readAdministrativeDivisionsByParentCode(ad.getCode()), bw);
			}
			bw.flush();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
}
