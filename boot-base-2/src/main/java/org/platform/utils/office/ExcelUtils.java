package org.platform.utils.office;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.platform.utils.date.DateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(ExcelUtils.class);
	
	private static final SimpleDateFormat SDF = DateFormatter.TIME.get();
	
	public static List<Map<String, Object>> readDatas(InputStream in, Map<Integer, String> header) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(in); 
	    	Sheet sheet = workbook.getSheetAt(0);
	    	int lastCellNum = sheet.getRow(0).getPhysicalNumberOfCells();
	    	for (int i = 1, iLen = sheet.getLastRowNum(); i <= iLen; i++) {
	    		Row row = sheet.getRow(i);
	    		Map<String, Object> result = new HashMap<String, Object>();
	    		for (int j = 0, jLen = lastCellNum; j < jLen; j++) {
	    			Cell cell = row.getCell(j);
	    			if (null == cell) continue;
	    			Object cellValue = null;
	    			switch (cell.getCellType()) {
	    				case BOOLEAN: cellValue = cell.getBooleanCellValue(); break;
	    				case ERROR: cellValue = cell.getErrorCellValue(); break;
	    				case FORMULA: cellValue = cell.getCellFormula(); break;
	    				case NUMERIC: cellValue = cell.getNumericCellValue(); break;
	    				/**
	    				case NUMERIC: {
	    					cellValue = cell.getNumericCellValue(); 
	    					String cellValueTxt = String.valueOf(cellValue);
	    					if (cellValueTxt.endsWith(".0")) {
	    						cellValue = Integer.parseInt(cellValueTxt.substring(0, cellValueTxt.indexOf(".")));
	    					}
	    					break;
	    				}
	    				*/
	    				default: cellValue = cell.getStringCellValue(); break;
	    			}
	    			result.put(header.get(j), cellValue);
	    		}
	    		resultList.add(result);
	    	}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if (null != in) in.close();
				if (null != workbook) workbook.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return resultList;
	}
	
	public static byte[] writeDatas(List<Map<String, Object>> datas, Map<Integer, String> header) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet();
			XSSFRow row = sheet.createRow(0);
			for (int i = 0, len = header.size(); i < len; i++) {
				XSSFCell cell = row.createCell(i);
				cell.setCellValue(String.valueOf(header.get(i)));
			}
			for (int i = 0, iLen = datas.size(); i < iLen; i++) {
				row = sheet.createRow(i + 1);
				Map<String, Object> result = datas.get(i);
				for (int j = 0, jLen = header.size(); j < jLen; j++) {
					XSSFCell cell = row.createCell(j);
					Object cellValue = result.get(header.get(j));
					if (null == cellValue) continue;
					if (cellValue instanceof Boolean) {
						cell.setCellValue((boolean) cellValue);
					} else if (cellValue instanceof Date) {
						cell.setCellValue(SDF.format((Date) cellValue));
					} else if (cellValue instanceof Double) {
						cell.setCellValue((double) cellValue);
					} else {
						cell.setCellValue(String.valueOf(cellValue));
					}
				}
			}
			workbook.write(baos);
			return baos.toByteArray();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if (null != baos) baos.close();
				if (null != workbook) workbook.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		InputStream in = new FileInputStream(new File("F:\\result\\user.xlsx"));
		Map<Integer, String> header = new HashMap<Integer, String>();
		header.put(0, "name");
		header.put(1, "idCard");
		header.put(2, "mobilePhone");
		header.put(3, "direction");
		header.put(4, "city");
		header.put(5, "reason");
		header.put(6, "scanType");
		header.put(7, "note");
		List<Map<String, Object>> datas = readDatas(in, header);
		datas.forEach(data -> {System.err.println(data);});
		byte[] bytes = writeDatas(datas, header);
		OutputStream out = new FileOutputStream(new File("F:\\result\\result.xlsx"));
		out.write(bytes);
		out.close();
	}

}
