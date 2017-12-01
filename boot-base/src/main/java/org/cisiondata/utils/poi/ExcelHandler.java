package org.cisiondata.utils.poi;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.cisiondata.utils.date.DateFormatter;
import org.cisiondata.utils.file.DefaultLineHandler;
import org.cisiondata.utils.file.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Excel超大数据读取，抽象Excel2007读取器，excel2007的底层数据结构是xml文件，采用SAX的事件驱动的方法解析xml，
 * 需要继承DefaultHandler，在遇到文件内容时，事件会触发，这种做法可以大大降低内存的耗费，特别使用于大数据量的文件。
 */
public abstract class ExcelHandler extends DefaultHandler {
	
	private Logger LOG = LoggerFactory.getLogger(ExcelHandler.class);

	//共享字符串表
	private SharedStringsTable sharedStringsTable = null;
	//当前Sheet
	private int currentSheetIndex = -1;
	//
	private boolean nextIsString = false;
	//当前行
	private int currentRow = 0;
	//当前列
	private int currentColumn = 0;
	//单元格的内容
	private String content = null;
	//当前行数据
	private List<String> currentRowData = new ArrayList<String>();
	//日期标志
	private boolean dateFlag = false;
	//数字标志
	private boolean numberFlag = false;
    //
	private boolean isTElement = false;
	//终止行
	private int shutdownRow = 0;
	//
	private InputStream sheetInputStream = null;
	
	/**
	 * 遍历工作簿中所有的电子表格
	 * @param filename
	 * @throws Exception
	 */
	public void process(String filename) {
		try {
			OPCPackage opcPackage = OPCPackage.open(filename, PackageAccess.READ);
			XSSFReader xssfReader = new XSSFReader(opcPackage);
			this.sharedStringsTable = xssfReader.getSharedStringsTable();
			System.err.println(sharedStringsTable);
			XMLReader xmlReader = getXMLReader();
			Iterator<InputStream> sheets = xssfReader.getSheetsData();
			while (sheets.hasNext()) {
				currentRow = 0;
				currentSheetIndex++;
				sheetInputStream = sheets.next();
				FileUtils.read(sheetInputStream, new DefaultLineHandler()).forEach(d -> System.err.println(d));
				xmlReader.parse(new InputSource(sheetInputStream));
				sheetInputStream.close();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * 只遍历一个电子表格，其中sheetId为要遍历的sheet索引，从1开始
	 * @param filename
	 * @param sheetId
	 * @throws Exception
	 */
	public void process(String filename, int sheetId) {
		try {
			OPCPackage opcPackage = OPCPackage.open(filename, PackageAccess.READ);
			XSSFReader xssfReader = new XSSFReader(opcPackage);
			this.sharedStringsTable = xssfReader.getSharedStringsTable();
			XMLReader xmlReader = getXMLReader();
			//根据 rId#或rSheet#查找sheet
			sheetInputStream = xssfReader.getSheet("rId" + sheetId);
			xmlReader.parse(new InputSource(sheetInputStream));
			sheetInputStream.close();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	public XMLReader getXMLReader() {
		XMLReader xmlReader = null;
		try {
			xmlReader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
			xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
			xmlReader.setFeature("http://xml.org/sax/features/validation", false);
	        xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
	        xmlReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			xmlReader.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
			xmlReader.setContentHandler(this);
		} catch (SAXException e) {
			LOG.error(e.getMessage(), e);
		}
		return xmlReader;
	}

	/**
	 * row表示每一行的数据
	 * r表示第几行
	 * c表示每个单元格的内容
	 * s表示这个单元格的样式
	 * t=s表示这个单元格有值
	 * v为值的id
	 */
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
//		System.err.println("start uri: " + uri + " localName: " + localName + " name: " + name);
		if ("c".equals(name)) {
//			System.err.println(attributes.getValue("r"));
			//如果下一个元素是SST的索引，则将nextIsString标记为true
			String cellTType = attributes.getValue("t");
			nextIsString = "s".equals(cellTType) ? true : false;
			//日期格式
			String cellSType = attributes.getValue("s");
			dateFlag = "1".equals(cellSType) ? true : false;
			numberFlag = "2".equals(cellSType) ? true : false;
		}
		//当元素为t时
		isTElement = "t".equals(name) ? true : false;
		//置空
		content = "";
	}

	public void endElement(String uri, String localName, String name) throws SAXException {
//		System.err.println("end uri: " + uri + " localName: " + localName + " name: " + name);
		if (currentRow == shutdownRow) {
			try {
				sheetInputStream.close();
			} catch (Exception e) {
			}
			return;
		}
		// 根据SST的索引值的到单元格的真正要存储的字符串
		// 这时characters()方法可能会被调用多次
		if (nextIsString) {
			try {
				int idx = Integer.parseInt(content);
				content = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx)).toString();
			} catch (Exception e) {
//				LOG.error(e.getMessage(), e);
			}
		}
		//t元素也包含字符串
		if (isTElement) {
			String value = content.trim();
			currentRowData.add(currentColumn, value);
			currentColumn++;
			isTElement = false;
			//v => 单元格的值，如果单元格是字符串则v标签的值为该字符串在SST中的索引
			//将单元格内容加入currentRowData中，在这之前先去掉字符串前后的空白符
		} else if ("v".equals(name)) {
			String value = content.trim();
			value = StringUtils.isBlank(value) ? " " : value;
			try {
				if (dateFlag) {
					Date date = HSSFDateUtil.getJavaDate(Double.valueOf(value));
					value = DateFormatter.TIME.get().format(date);
				}
				if (numberFlag) {
					BigDecimal bd = new BigDecimal(value);
					value = bd.setScale(3, BigDecimal.ROUND_UP).toString();
				}
			} catch (Exception e) {
//				LOG.error(e.getMessage(), e);
			}
//			System.err.println("currentColumn: " + currentColumn + " value: " + value);
			currentRowData.add(currentColumn, value);
			currentColumn++;
		} else {
			//如果标签名称为 row，这说明已到行尾，调用optRows()方法
			if (name.equals("row")) {
				getRows(currentSheetIndex + 1, currentRow, currentRowData);
				currentRow++;
				currentColumn = 0;
				currentRowData.clear();
			}
		}

	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		content += new String(ch, start, length);
	}

	/**
	 * 获取行数据回调
	 * 
	 * @param sheetIndex
	 * @param curRow
	 * @param rowData
	 */
	public abstract void getRows(int sheetIndex, int curRow, List<String> rowData);
	
	public void setShutdownRow(int shutdownRow) {
		this.shutdownRow = shutdownRow;
	}

	/**
	 * 测试方法
	 */
	public static void main(String[] args) throws Exception {
		List<List<String>> resultList = new ArrayList<List<String>>();
		String path ="F:\\document\\code\\b.xlsx";
		ExcelHandler excelHandler = new ExcelHandler() {
			public void getRows(int sheetIndex, int curRow, List<String> rowData) {
				System.out.println("Sheet:" + sheetIndex + ", Row:" + curRow+ ", Data:" + rowData);
				resultList.add(new ArrayList<String>(rowData));
			}
		};
		excelHandler.setShutdownRow(2);
		excelHandler.process(path);
		resultList.forEach(data -> System.err.println(data));
	}
}

