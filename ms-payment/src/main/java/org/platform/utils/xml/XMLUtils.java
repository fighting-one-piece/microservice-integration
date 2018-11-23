package org.platform.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class XMLUtils {
	
	/**
     * @param xmltext
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static Map<String, String> parseXML(String xmltext) throws JDOMException, IOException {
        xmltext = xmltext.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");
        if(null == xmltext || "".equals(xmltext))  return null;
        Map<String, String> result = new HashMap<String, String>();
        InputStream in = new ByteArrayInputStream(xmltext.getBytes("UTF-8"));
        Document document = new SAXBuilder().build(in);
        Element root = document.getRootElement();
        Iterator<Element> iterator = root.getChildren().iterator();
        while(iterator.hasNext()) {
            Element element = iterator.next();
            String k = element.getName();
            String v = "";
            List<Element> children = element.getChildren();
            if(children.isEmpty()) {
                v = element.getTextNormalize();
            } else {
                v = getChildrenText(children);
            }
            result.put(k, v);
        }
        in.close();
        return result;
    }
     
    /**
     * 获取子结点的xml
     * @param children
     * @return String
     */
    public static String getChildrenText(List<Element> children) {
        StringBuilder sb = new StringBuilder();
        if(!children.isEmpty()) {
            Iterator<Element> iterator = children.iterator();
            while(iterator.hasNext()) {
                Element element = iterator.next();
                String name = element.getName();
                String value = element.getTextNormalize();
                List<Element> list = element.getChildren();
                sb.append("<" + name + ">");
                if(!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }
        return sb.toString();
    }
     
}
