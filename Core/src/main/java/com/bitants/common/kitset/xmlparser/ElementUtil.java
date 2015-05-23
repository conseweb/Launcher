package com.bitants.common.kitset.xmlparser;

import java.util.ArrayList;
import java.util.List;

import com.bitants.common.kitset.xmlparser.exception.ElementNotFoundException;
import com.bitants.common.kitset.xmlparser.exception.ElementPathErrorException;

/**
 * Element工具类
 */
public class ElementUtil {

	/**
	 * 查找元素
	 * @param root
	 * @param path
	 * @return List<Element>
	 * @throws ElementPathErrorException
	 * @throws ElementNotFoundException
	 */
	public static List<Element> findElements(Element root, String path) throws ElementPathErrorException, ElementNotFoundException {
		if (path.equals("") || path.startsWith(".") || path.endsWith(".") || path.indexOf("..") >= 0) {
			throw new ElementPathErrorException("ElementPath:" + path);
		}

		List<Element> parents = new ArrayList<Element>();
		parents.add(root);
		return findElements(parents, path);

	}

	/**
	 * 查找元素
	 * @param elements
	 * @param path
	 * @return List<Element>
	 * @throws ElementNotFoundException
	 */
	private static List<Element> findElements(List<Element> elements, String path) throws ElementNotFoundException {
		String nodeName;
		String nextPath;
		int pointOffset = path.indexOf('.');
		if (pointOffset < 0) {
			nodeName = path;
			nextPath = null;
		} else {
			nodeName = path.substring(0, pointOffset);
			nextPath = path.substring(pointOffset + 1, path.length());
		}

		List<Element> result = new ArrayList<Element>();
		for (Element element : elements) {

			if (element.getName().equals(nodeName)) {
				result.add(element);

			}
		}

		if (nextPath == null) {
			return result;
		}

		if (result.isEmpty()) {
			throw new ElementNotFoundException("Can't found node named:" + nodeName);
		}

		List<Element> nextSearchList = new ArrayList<Element>();
		for (Element e : result) {
			nextSearchList.addAll(e.getChildren());
		}

		if (nextSearchList.isEmpty()) {
			throw new ElementNotFoundException("Can't found next node by parent:" + nodeName);
		}

		return findElements(nextSearchList, nextPath);

	}
}
