package org.cisiondata.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import org.cisiondata.utils.file.PropertiesUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

public class TempTest {

	@Test
	public void t1() throws Exception {
		// DefaultHandler handler = new XmlParserHandler();
		DefaultHandler handler = new DefaultHandler();
		XMLReader xr = XMLReaderFactory.createXMLReader();
		xr.setErrorHandler(handler);
		xr.setDTDHandler(handler);
		xr.setFeature("http://xml.org/sax/features/validation", true);// 开启DTD验证
		xr.setFeature("http://apache.org/xml/features/validation/schema", true);// 开启SCHMAE验证
		xr.parse(new InputSource("F:\\document\\doc\\201708\\1\\t.xml"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void t2() throws Exception {
		InputStream in = new FileInputStream(new File("F:\\document\\doc\\201708\\1\\t.xml"));
		SAXReader reader = new SAXReader(false);
		Document doc = reader.read(in);
		System.err.println(doc.asXML());
		System.err.println("########");
		System.err.println(doc.nodeCount());
		Element root = doc.getRootElement();
		for (Iterator<Element> i = root.elementIterator(); i.hasNext();) {
			Element element = i.next();
			System.err.println(element.getName() + ":" + element.getStringValue());
		}
	}

	@Test
	public void t3() throws Exception {
		InputStream in = new FileInputStream(new File("F:\\a.xml"));
		SAXReader reader = new SAXReader(false);
		Document document = reader.read(in);
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter writer = new XMLWriter(new OutputStreamWriter(
				new FileOutputStream(new File("F:\\b.xml")), "UTF-8"), format);
		writer.write(document);
		writer.close();
	}

	@Test
	public void t4() throws Exception {
		Document document = DocumentHelper.createDocument();// 建立document对象，用来操作xml文件
		Element booksElement = document.addElement("books");// 建立根节点
		booksElement.addComment("This is a test for dom4j ");// 加入一行注释
		Element bookElement = booksElement.addElement("book");// 添加一个book节点
		bookElement.addAttribute("show", "yes");// 添加属性内容
		Element titleElement = bookElement.addElement("title");// 添加文本节点
		titleElement.setText("ajax in action");// 添加文本内容
		try {
			String filename = "F:\\document\\doc\\201708\\1\\to1.xml";
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			XMLWriter writer = new XMLWriter(new FileWriter(new File(filename)), format);
			writer.write(document);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void t5() throws Exception {
		String json = null;
		XMLSerializer xmlSerializer = new XMLSerializer();
        String xml = xmlSerializer.write(JSONSerializer.toJSON(json));
        System.err.println(xml);
	}

	@Test
	public void t6() throws Exception {
		String userDir = System.getProperty("user.dir");
		System.err.println(userDir);
		String configFile = userDir + File.separator + "config" + File.separator + "sys-conf.properties";
		System.err.println(configFile);
		String ipath = PropertiesUtils.getProperty(configFile, "transfer.file.i.path");
		System.err.println(ipath);
	}
}
