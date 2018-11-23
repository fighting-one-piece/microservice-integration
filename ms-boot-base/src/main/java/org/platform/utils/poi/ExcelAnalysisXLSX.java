package org.platform.utils.poi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 读取excel xlsx(07、10)
 * 
 * @author dallas16
 *
 */
public class ExcelAnalysisXLSX extends DefaultHandler {
	/**
	 * excel样式
	 */
	private StylesTable stylesTable;
	/**
	 * 好像
	 */
	private ReadOnlySharedStringsTable sharedStringsTable;

	/**
	 * 单元格中的数据可能的数据类型
	 *
	 */
	enum xssfDataType {
		BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,
	}

	/**
	 * 存储某个cell的类型
	 */
	private xssfDataType nextDataType = xssfDataType.NUMBER;
	/**
	 * 某个cell的值
	 */
	private String value = "";

	private short formatIndex;
	private String formatString;
	private final DataFormatter formatter = new DataFormatter();

	/**
	 * 存储某一行的数据
	 */
	private List<String> rowlist = new ArrayList<String>();
	/**
	 * excel的sheet名称
	 */
	private String sheetName;
	/**
	 * excel 的路径
	 */
	private String path;
	/**
	 * 准备存放读取结果
	 */
	private List<List<String>> datas = new ArrayList<List<String>>();
	/**
	 * 当前的数据时第几列
	 */
	private int thisColumn;
	private int lastColumnNumber;

	/**
	 * 匹配开头和结尾是否是数字
	 */
	@SuppressWarnings("unused")
	private static Pattern p1 = Pattern.compile("^\\d.*\\d$");

	/**
	 * 对单元格的数据进行处理，重写了DefaultHandler中characters方法（其实DefaultHandler中的所有方法都没有方法体），
	 * 这个方法在读取过程中会被自动调用
	 * 
	 * @param ch
	 * @param start
	 * @param length
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		value = value + new String(ch, start, length);
	}

	/**
	 * 在读取元素结束时的 处理，主要是判断是不是一个单元格结束，是不是一行结束，是的话进行相应的处理
	 * 是单元格则将数据（value）添加到rowlist的相应位置 是一行结束的话则将 rowlist添加到sheetVo中
	 * 
	 * @param uri
	 * @param localName
	 * @param qName
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("v".equals(qName)) {
			endDeal();
		} else if ("row".equals(qName)) {
			if (lastColumnNumber == -1) {
				lastColumnNumber = 0;
			}
			this.dealData();
			lastColumnNumber = -1;
		}

	}

	/**
	 * 在读取元素开始时的 处理，
	 * 
	 * @param uri
	 * @param localName
	 * @param qName
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("inlineStr".equals(qName) || "v".equals(qName)) {
			value = "";
		} else if ("c".equals(qName)) {
			String r = attributes.getValue("r");
			int firstDigit = -1;
			int length = r.length();
			for (int c = 0; c < length; ++c) {
				if (Character.isDigit(r.charAt(c))) {
					firstDigit = c;
					break;
				}
			}
			thisColumn = nameToColumn(r.substring(0, firstDigit));
			dealDataType(attributes);
		}

	}

	/**
	 * 对指定的sheet进行处理
	 * 
	 * @param styles
	 * @param strings
	 * @param sheetInputStream
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void processSheet(StylesTable styles, ReadOnlySharedStringsTable strings, InputStream sheetInputStream)
			throws IOException, ParserConfigurationException, SAXException {

		InputSource sheetSource = new InputSource(sheetInputStream);
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxFactory.newSAXParser();
		XMLReader sheetParser = saxParser.getXMLReader();
		this.stylesTable = styles;
		this.sharedStringsTable = strings;
		sheetParser.setContentHandler(this);
		sheetParser.parse(sheetSource);
	}

	/**
	 * 
	 * @param excelUtilBean.getPath()
	 *            需要读取的excel的路径
	 * @param excelUtilBean.getSheetName()
	 *            如果值不为空则按名称进行解析
	 * @param excelUtilBean.getSheetNumber()
	 *            如果值为空则按指定的顺序进行解析
	 * @throws IOException
	 * @throws OpenXML4JException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	@SuppressWarnings("unused")
	public void process() throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {
		OPCPackage pkg = OPCPackage.open(path);
		ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
		XSSFReader xssfReader = new XSSFReader(pkg);
		StylesTable styles = xssfReader.getStylesTable();
		XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
		int index = 0;
		boolean flag = false;
		while (iter.hasNext()) { // 做判断，看是否是自己需要解析的那一个sheet
			InputStream stream = iter.next();
			String sheetName = iter.getSheetName();
			if (iter.getSheetName().equals(sheetName)) {
				processSheet(styles, strings, stream);
				flag = true;
				break;
			}
			stream.close();
			++index;
		}
		if (!flag) {
			String errorInfo = "名为  ‘" + sheetName + "’ 的sheet不存在！";
			throw new RuntimeException(errorInfo);
		}
		pkg = null;
		strings = null;
		xssfReader = null;
		styles = null;
	}

	/**
	 * 结束元素时的处理，根据情况将数据添加到 rowlist中
	 */
	public void endDeal() {
		String thisStr = null;
		thisStr = dealData(value, thisStr); // 对单元格的数据进行类型处理
		if (lastColumnNumber == -1) {
			lastColumnNumber = 0;
		}
		for (int i = lastColumnNumber + 1; i < thisColumn; ++i) {
			rowlist.add("  ");
		}
		rowlist.add(thisStr == null ? "" : thisStr);
		if (thisColumn > -1) {
			lastColumnNumber = thisColumn;
		}
	}

	/**
	 * 将数据添加到sheetVo中去，也是根据条件进行不同的处理
	 */
	public void dealData() {
		if (rowlist != null && rowlist.size() != 0) {
			datas.add(rowlist);
			rowlist = null;

		}
		if (rowlist == null) {
			rowlist = new ArrayList<String>();
		}
	}

	/**
	 * 好像是计算当前是第几列
	 * 
	 * @param name
	 * @return
	 */
	private int nameToColumn(String name) {
		int column = -1;
		int length = name.length();
		for (int i = 0; i < length; ++i) {
			int c = name.charAt(i);
			column = (column + 1) * 26 + c - 'A';
		}
		return column;
	}

	/**
	 * 对解析出来的数据进行类型处理
	 * 
	 * @param value
	 *            单元格的值（这时候是一串数字）
	 * @param thisStr
	 *            一个空字符串
	 * @return
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	public String dealData(String value, String thisStr) {
		switch (nextDataType) {// 这几个的顺序不能随便交换，交换了很可能会导致数据错误
		case BOOL:
			char first = value.charAt(0);
			thisStr = first == '0' ? "FALSE" : "TRUE";
			break;
		case ERROR:
			thisStr = "\"ERROR:" + value.toString() + '"';
			break;
		case FORMULA:
			thisStr = '"' + value.toString() + '"';
			break;
		case INLINESTR:
			XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());

			thisStr = rtsi.toString();
			rtsi = null;
			break;
		case SSTINDEX:
			String sstIndex = value.toString();
			try {
				int idx = Integer.parseInt(sstIndex);
				XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
				thisStr = rtss.toString();
				rtss = null;
			} catch (NumberFormatException ex) {
				thisStr = value.toString();
			}
			break;
		case NUMBER:
			String n = value.toString();
			if (this.formatString != null)
				thisStr = formatter.formatRawCellContents(Double.parseDouble(n), this.formatIndex, this.formatString);
			else
				thisStr = n;
			break;
		default:
			thisStr = " ";

			break;
		}

		try {
			Date date = new Date(thisStr);

		} catch (Exception e) {

		}

		return thisStr;
	}

	/**
	 * 处理数据类型
	 * 
	 * @param attributes
	 */
	public void dealDataType(Attributes attributes) {
		this.nextDataType = xssfDataType.NUMBER;
		this.formatIndex = -1;
		this.formatString = null;
		String cellType = attributes.getValue("t");
		String cellStyleStr = attributes.getValue("s");

		if ("b".equals(cellType))
			nextDataType = xssfDataType.BOOL;
		else if ("e".equals(cellType))
			nextDataType = xssfDataType.ERROR;
		else if ("inlineStr".equals(cellType))
			nextDataType = xssfDataType.INLINESTR;
		else if ("s".equals(cellType))
			nextDataType = xssfDataType.SSTINDEX;
		else if ("str".equals(cellType))
			nextDataType = xssfDataType.FORMULA;
		else if (cellStyleStr != null) {
			int styleIndex = Integer.parseInt(cellStyleStr);
			XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
			this.formatIndex = style.getDataFormat();
			this.formatString = style.getDataFormatString();
			if (this.formatString == null)
				this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
		}
	}

	/**
	 * 将数据添加到rowlist中
	 * 
	 * @param str
	 *            单元格的数据
	 * @param num
	 *            列号
	 */
	public void addRowlist(String str, int num) {

		if (rowlist.size() >= num) {
			rowlist.add(num, str == null ? "" : str);
		} else {
			int size = rowlist.size();
			int newNum = num + 1;
			for (int i = size; i < newNum; i++) {
				rowlist.add("");
			}
			rowlist.add(num, str == null ? "" : str);
		}
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<List<String>> getDatas() {
		return datas;
	}

	public void setDatas(List<List<String>> datas) {
		this.datas = datas;
	}

	public static void main(String[] args) throws Exception {

		ExcelAnalysisXLSX excel = new ExcelAnalysisXLSX();
		excel.setPath("F:\\document\\code\\b.xlsx");
		excel.setSheetName("Sheet1");
		excel.process();
		List<List<String>> datas = excel.getDatas();
		for (List<String> data : datas) {
			System.out.println(data);
		}
	}

}
