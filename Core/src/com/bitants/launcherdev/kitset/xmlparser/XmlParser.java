package com.bitants.launcherdev.kitset.xmlparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bitants.launcherdev.kitset.xmlparser.exception.XmlFormatErrorException;

/**
 * XML解析器
 */
public class XmlParser {

	static int deep = 0;

	public static Element buildXmlRootByString(String xml) throws XmlFormatErrorException {
		xml = prepareParse(xml);
		xml = checkXml(xml);
		ElementImpl root = new ElementImpl();
		root.setName("root");
		parseMultiElement(root, xml);
		return root.getChildren().get(0);
	}

	private static void parseMultiElement(Element parent, String xml) {

		String next = parseSingleElement(parent, xml);
		try {
			checkXml(next);
			while (next != null) {
				next = parseSingleElement(parent, next);
			}
		} catch (Exception e) {
			return;
		}
	}

	private static String parseSingleElement(Element parent, String elementXml) {

		ElementImpl e = new ElementImpl();
		parent.getChildren().add(e);
		e.setParent(parent);

		String startTag = getElementStartTag(elementXml);
		String elementName = getElementName(startTag);
		String endTag;
		String elementValue;
		if (startTag.endsWith("/>")) {
			endTag = startTag;
			elementValue = "";
		} else {
			endTag = "</" + elementName + ">";
			elementValue = elementXml.substring(elementXml.indexOf('>') + 1, elementXml.indexOf(endTag));
		}

		e.setName(elementName);

		Map<String, String> attrMap = getElementAttribute(startTag, elementName);
		e.setAttributes(attrMap);

		if (elementValue.equals("")) {
			e.setValue(elementValue);
		} else {
			try {
				checkXml(elementValue);
				e.setValue("");
				parseMultiElement(e, elementValue);
			} catch (XmlFormatErrorException exception) {
				e.setValue(elementValue.replace("<![CDATA[", "").replace("]]>", ""));
			}
		}

		String nextChildrenContent = null;
		if (elementXml.indexOf(endTag) + endTag.length() < elementXml.length()) {
			nextChildrenContent = elementXml.substring(elementXml.indexOf(endTag) + endTag.length()).trim();
		}

		return nextChildrenContent;
	}

	private static String prepareParse(String xml) {
		xml = xml.replace("&lt;br&gt;", "\n").replace("&lt;br /&gt;", "\n").replace("&amp;nbsp;", "  ");

		Pattern p = Pattern.compile("<\\w.+");
		Matcher m = p.matcher(xml.toString());
		m.find();
		return m.group();
	}

	private static String checkXml(String xmlStr) throws XmlFormatErrorException, NullPointerException {
		if (xmlStr == null) {
			throw new NullPointerException();
		}
		String xml = xmlStr.trim();
		if (!xml.matches("<\\w.+")) {
			throw new XmlFormatErrorException("Xml isn't start with '<' and char.");
		}
		return xml;
	}

	/**
	 * getElementStartTag
	 * 
	 * @param content
	 *            string
	 * @return String
	 */
	private static String getElementStartTag(String content) {
		content = content.trim();
		return content.substring(0, content.indexOf('>') + 1);
	}

	/**
	 * getElementName
	 * 
	 * @param startTagContent
	 *            String
	 * @return String
	 */
	private static String getElementName(String startTagContent) {

		if (startTagContent.indexOf(' ') != -1) {
			return startTagContent.substring(1, startTagContent.indexOf(' '));
		} else {
			return startTagContent.substring(1, startTagContent.indexOf('>'));
		}
	}

	/**
	 * getElementAttribute
	 * 
	 * @param startTag
	 * @param tagName
	 * @return Map
	 */
	private static Map<String, String> getElementAttribute(String startTag, String tagName) {
		Map<String, String> attrMap = new HashMap<String, String>();

		String attrStr = "";
		// attrStr = startTag.replace("\"", "").replace("<" + tagName,
		// "").replace("/>", "").replace(">", "").trim();
		attrStr = startTag.replace("<" + tagName, "").replace("/>", "").replace(">", "").trim();
		// int strCuror=0;
		if (attrStr.length() > 0) {
			// attrStr=attrStr.replace("\" ", "\" ");
			String[] attrStrs = attrStr.split("\" ");
			// String[] attrStrs = attrStr.split(" ");
			for (String attr : attrStrs) {
				String[] attrStrMap = attr.split("=");
				attrMap.put(attrStrMap[0], attrStrMap[1].replace("\"", ""));
			}

		}
		return attrMap;
	}

	/**
	 * test
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File("f:\\ring.php.xml")));
			while (in.ready()) {
				sb.append(in.readLine());
			}
			Element e = buildXmlRootByString(sb.toString());
			List<Element> list = ElementUtil.findElements(e, "result.data.item");
			for (Element ele : list) {
				System.out.println(ele.getFirstChild("name").getValue());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
