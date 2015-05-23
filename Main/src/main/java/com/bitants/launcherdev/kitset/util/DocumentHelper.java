package com.bitants.launcherdev.kitset.util;



import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 */
public class DocumentHelper {
	
	public static String getValByTagName(Document doc, String tagName){
		NodeList list = doc.getElementsByTagName(tagName);
		if(list.getLength() > 0){
			Node node = list.item(0);
			Node valNode = node.getFirstChild();
			if(valNode != null){
				String val = valNode.getNodeValue();
				return val;
			}
		}
		return null;
	}

}
