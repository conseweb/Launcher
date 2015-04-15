package com.bitants.launcherdev.kitset.xmlparser;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

/**
 * 用于解析公司xml的类
 */
public class ElementX {

	private String tag; 

	private String text; 

	@SuppressWarnings("unchecked")
	private Hashtable attrs;

	private Vector<ElementX> children; 

	private ElementX parent;

	public ElementX getParent() {
		return parent;
	}

	@SuppressWarnings("unchecked")
	protected ElementX(String name) {
		children = new Vector();
		attrs = new Hashtable();
		this.tag = name;
		text = "";
	}

	@SuppressWarnings("unchecked")
	public void setAttr(String name, String value) {
		this.attrs.put(name, value);
	}

	public String getAttr(String name) {
		return (String) attrs.get(name);
	}

	public void addChild(ElementX node) {
		this.children.addElement(node);
	}

	/**
	 * @return the children
	 */
	public Vector<ElementX> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(Vector<ElementX> children) {
		this.children = children;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	public static String DOCMENT = "docment";
	public static String DTD_ELEMENT = "ELEMENT";
	public static String DTD_CDATA = "CDATA";

	@SuppressWarnings("unchecked")
	public static void parseDtd(String text, Hashtable dtd) {
		int lenght = text.length(); // ????????

		int index = text.indexOf(DTD_ELEMENT); // ???????

		if (index == -1) {
			index = text.indexOf(DTD_ELEMENT.toLowerCase()); // ???????

		}
		if (index == -1) {
			return;
		}
		index += 7;
		char aChar = 0; // ??????

		StringBuffer buffer = new StringBuffer(); // ??????

		String stmp = null;

		int type = 0;

		String name = null;
		while (index < lenght) {
			aChar = text.charAt(index);
			if (aChar == ' ') {
				if (type == 1) {
					name = buffer.toString().toLowerCase();
					buffer.delete(0, buffer.length());
					type = 0;
				}
				index++;
			} else if (aChar == '(') {
				int tmp = text.indexOf(')', index);
				stmp = text.substring(index + 1, tmp).trim();
				stmp = stmp.substring(1).toUpperCase();
				dtd.put(name, stmp);
				index = tmp;
			} else {
				if (type == 0) {
					type = 1;
				}
				if (type == 1) {
					buffer.append(aChar);
				}
				index++;
			}
		}

		buffer.delete(0, buffer.length());
		buffer = null;
		name = null;
	}

	@SuppressWarnings("unchecked")
	public static ElementX parse(String text, Hashtable dtd_) {
		// Application.debug("--\n:"+text.indexOf(0x0d));
		// Application.debug("++\n:"+text.indexOf(next));
		// text = replaceAll(text, next, "<br/>");
		// Application.debug("<br/>:"+text);

		// T.DEBUG("Element:" + text);
		Stack nodeStack = new Stack(); // ?????

		Hashtable dtd = new Hashtable();

		int lenght = text.length(); // ????????

		int index = 0; // ???????

		char aChar = 0; // ??????


		StringBuffer buffer = new StringBuffer(); // ??????
		
		ElementX root = new ElementX(DOCMENT);
		nodeStack.push(root);
		ElementX node = null;

		String stmp = null;

		/* ???��??? */
		while (index < lenght) {
			aChar = text.charAt(index);
			if (aChar == '<') {
				aChar = text.charAt(index + 1);
				stmp = buffer.toString().trim();
				buffer.delete(0, buffer.length());

				if (aChar == '/') {
					if (!nodeStack.empty()) {
						root = (ElementX) nodeStack
								.elementAt(nodeStack.size() - 1);
					}
					root.setText(processEntities(stmp));

					int tmp = text.indexOf('>', index);
					stmp = text.substring(index, tmp + 1);
					String tag = getTag(stmp);

					if (tag.equals(root.getTag())) {
						if (root.children != null) {
							if (root.children.size() > 0) {
								if (root.getText() != null
										&& !root.getText().equals("")) {
									node = new ElementX("");
									node
											.setText(processEntities(root
													.getText()));
									root.addChild(node);
									root.setText(null);
								}
							}
						}

						nodeStack.pop();
					}

					aChar = text.charAt(tmp - 1);
					index = tmp + 1;
				} else if (aChar == '!') {
					if (text.charAt(index + 2) == '-'
							&& text.charAt(index + 3) == '-') {
						int tmp = text.indexOf("-->", index);
						stmp = text.substring(index, tmp + 3);
						aChar = text.charAt(tmp - 1);
						index = tmp + 3;
					} else {
						int tmp = text.indexOf('>', index);
						stmp = text.substring(index, tmp + 1);
						parseDtd(stmp, dtd);
						aChar = text.charAt(tmp - 1);
						index = tmp + 1;
					}
				} else if (aChar == '?') {
					int tmp = text.indexOf('>', index);
					stmp = text.substring(index, tmp + 1);
					aChar = text.charAt(tmp - 1);
					index = tmp + 1;
				} else {
					// 54725176
					if (!nodeStack.empty()) {
						root = (ElementX) nodeStack
								.elementAt(nodeStack.size() - 1);
					}
					if (stmp != null && stmp.length() > 0) {
						node = new ElementX("");
						node.setText(processEntities(stmp));
						root.addChild(node);
						node.parent = root;
					}

					int tmp = text.indexOf('>', index);
					stmp = text.substring(index, tmp + 1);
					// System.out.println(stmp);

					String tag = getTag(stmp);
					node = createElement(stmp);
					node.setTag(tag);
					// System.out.println("parent:"+root.getTag()+"---------tag:"+tag);

					if (text.charAt(tmp - 1) == '/') {
						root.addChild(node);
						node.parent = root;
						index = tmp + 1;
					} else {
						String dtype = null;

						if (dtd_ != null) {
							if (dtd_.get(tag) != null) {
								dtype = (String) dtd_.get(tag);
							}
						}
						if (dtd != null) {
							if (dtd.get(tag) != null) {
								dtype = (String) dtd.get(tag);
							}
						}

						if (dtype != null) {
							if (dtype.equals(DTD_CDATA)) {
								String tag_ = "</" + tag + ">";
								int tmp_ = text.indexOf(tag_, tmp);
								if (tmp_ == -1) {
									tmp_ = text
											.indexOf(tag_.toUpperCase(), tmp);
								}
								node.setText(text.substring(tmp + 1, tmp_));
								root.addChild(node);
								node.parent = root;
								index = tmp_ + tag_.length();
							} else {
								root.addChild(node);
								node.parent = root;
								nodeStack.push(node);
								index = tmp + 1;
							}
						} else {
							root.addChild(node);
							node.parent = root;
							nodeStack.push(node);
							index = tmp + 1;
						}
					}
				}
			} else {
				if (aChar != '\r')// && aChar != '\n'
				{
					buffer.append(aChar);
				}
				index++;
			}
		}
		// System.out.println("size:"+nodeStack.size());

		return (ElementX) nodeStack.pop();
	}

	public static String getTag(String str) {
		int end = str.indexOf(' ');
		int tmp = 1;
		if (str.charAt(1) == '/') {
			tmp = 2;
			end = str.length() - 1;
		}
		if (end == -1 || tmp == 2) {
			if (str.charAt(str.length() - 2) == '/') {
				end = str.length() - 2;
			} else {
				end = str.length() - 1;
			}
		}
		return str.substring(tmp, end).toLowerCase();
	}

	protected static ElementX createElement(String str) {
		ElementX node = new ElementX("");
		int index = 0;
		int length = str.length();
		char aChar = 0;
		int type = 0;
		StringBuffer buffer = new StringBuffer();
		char quot = 0;
		int quot_Type = 0;
		String name = null;
		String value = null;

		while (index < length) {
			aChar = str.charAt(index);
			// if(aChar==0x0A||aChar==0x0D)
			// {
			// continue;
			// }

			if (type == 0) {
				if ('\r' == aChar || '\n' == aChar || '\t' == aChar
						|| ' ' == aChar) {
					type = 1;
				}
				++index;
			} else if (type == 1) {
				if ('=' == aChar) {
					name = buffer.toString().trim();
					buffer.delete(0, buffer.length());
					type = 2;
					quot_Type = 0;
					quot = 0;
				} else {
					buffer.append(aChar);
				}
				++index;
			} else if (type == 2) {
				if (quot_Type == 0) {
					if (' ' != aChar) {
						if ('\'' == aChar || '\"' == aChar) {
							quot = aChar;
						} else {
							buffer.append(aChar);
							quot = ' ';
							quot_Type = 1;
						}
						quot_Type = 1;
					}
					++index;
				} else if (quot_Type == 1) {
					if (quot == aChar) {
						type = 0;
						value = buffer.toString().trim();

						buffer.delete(0, buffer.length());
						node.setAttr(name, processEntities(value));
						// System.out.println("name:"+name+"--value:"+processEntities(value));
						name = null;
						value = null;
					} else {
						buffer.append(aChar);
						++index;
					}
				}
			}
		}
		buffer.delete(0, buffer.length());
		buffer = null;
		name = null;
		value = null;

		return node;
	}

	private static String processEntities(String str) {
		str = convert(str);
		str = replaceTagString(str);
		return str;
	}

	// private static String getEntity(String str)
	// {
	// String entity = str;
	//
	// if (str.equals("&nbsp;"))
	// {
	// entity = " ";
	// } else if (str.equals("&amp;"))
	// {
	// entity = "&";
	// } else if (str.equals("&apos;"))
	// {
	// entity = "'";
	// } else if (str.equals("&gt;"))
	// {
	// entity = ">";
	// } else if (str.equals("&lt;"))
	// {
	// entity = "<";
	// } else if (str.equals("&quot;"))
	// {
	// entity = "\"";
	// }
	//
	// return entity;
	// }
	public static String unicodeToString(String s) {
		if (s == null) {
			return null;
		}
		StringBuffer result = new StringBuffer();
		int tempI, i, ch;
		for (i = 0; i < s.length(); i++) {
			if ((ch = s.charAt(i)) == '\\') // ?????Unicode????????????????????????

			{
				tempI = i;
				i += 2;
				while (s.length() > i && s.charAt(i) == 'u') {
					i++;
				}
				if (s.length() >= i + 4) {
					ch = Integer.parseInt(s.substring(i, i + 4), 16); // ??Unicode16??????????10????

					i += 3;

				} else {
					i = tempI;
				}
			}
			// ?????????????????????????????????result?????????????????t???
			result.append((char) ch);
		}
		return result.toString();
	}

	// ????�I
	public static String replaceAll(String s, String s1, String s2) {
		int i = 0;
		int j = 0;
		StringBuffer stringbuffer = new StringBuffer();
		while ((j = s.indexOf(s1, i)) >= 0) {
			stringbuffer.append(s.substring(i, j));
			stringbuffer.append(s2);
			i = j + s1.length();
		}
		stringbuffer.append(s.substring(i));
		return stringbuffer.toString();
	}

	// public static String repStringX(String s, String s1, String s2, boolean
	// ignoreCase)
	// {
	// String tmp = "";
	// if (s == null || s1 == null || s2 == null)
	// {
	// return s;
	// }
	// int j;
	// if (ignoreCase)
	// {
	// s1 = s1.toLowerCase();
	// int i;
	// while ((i = s.toLowerCase().indexOf(s1)) != -1)
	// {
	// String s3 = s.substring(0, i);
	// String s5 = s.substring(i + s1.length());
	// tmp = tmp + s3 + s2;
	// s = s5;
	// }
	// } else
	// {
	// while ((j = s.indexOf(s1)) != -1)
	// {
	// String s4 = s.substring(0, j);
	// String s6 = s.substring(j + s1.length());
	// tmp = tmp + s4 + s2;
	// s = s6;
	// }
	// }
	// return tmp + s;
	// }
	static String next = new String(new byte[] { 0x0d, 0x0A });

	public static String replaceTagString(String s) {
		s = replaceAll(s, "&copy;", "(c)");
		s = replaceAll(s, "&amp;", "&");
		s = replaceAll(s, "&lt;", "<");
		s = replaceAll(s, "&gt;", ">");
		s = replaceAll(s, "&nbsp;", " ");
		s = replaceAll(s, "&apos;", "'");
		s = replaceAll(s, "&quot;", "\"");
		s = replaceAll(s, "&#039;", "'");
		s = replaceAll(s, "&#32;", " ");
		s = replaceAll(s, "&#8226;", "\u25aa");

		return s.trim();
	}

	public static String convert(String s) {
		do {
			int k1;
			if ((k1 = s.indexOf("&#x")) == -1) {
				break;
			}
			int k2 = k1;
			int j4 = s.length();
			int j5 = k1;
			do {
				if (j5 >= j4) {
					break;
				}
				if (s.charAt(j5) == ';') {
					k2 = j5;
					break;
				}
				j5++;
			} while (true);
			if (k2 == k1) {
				break;
			}
			String str = "\\n" + s.substring(k1 + 3, k2);
			str = unicodeToString(str);
			s = s.substring(0, k1) + str + s.substring(k2 + 1);
		} while (true);
		return s;
	}

	@SuppressWarnings("unchecked")
	public void remove() {
		this.tag = null;
		this.text = null;

		if (this.attrs != null) {
			Enumeration e = this.attrs.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				this.attrs.remove(key);
				key = null;
			}
			e = null;
		}
		this.attrs = null;
		if (this.children != null) {
			for (int i = 0; i < this.children.size(); i++) {
				(this.children.elementAt(i)).remove();
			}

			this.children.removeAllElements();
		}
		this.children = null;
	}

	public int size;

	public int size() {
		if (this.children != null) {
			size += this.children.size();
			for (int i = 0; i < this.children.size(); i++) {
				size += (this.children.elementAt(i)).size();
			}
		}
		return size;
	}

	public static void findElement(ElementX root, String tag, Vector<ElementX> reV) {
		if (root.getTag().equals(tag)) {
			reV.addElement(root);
			return;
		}
		Vector<ElementX> v = root.getChildren();
		for (int i = 0; i < v.size(); i++) {
			/*
			 * if(v.size()==20){ System.out.println(); }
			 */
			ElementX childE = v.elementAt(i);
			if (childE.getTag().equals(tag)) {
				reV.addElement(childE);
			} else {
				Vector<ElementX> child = childE.getChildren();
				if (child.size() > 0) {
					for (int j = 0; j < child.size(); j++) {
						findElement(child.elementAt(j), tag, reV);
					}
				}
			}
		}
	}

	public ElementX getChild(String childTag) {
		for (int i = 0; i < children.size(); i++) {
			ElementX e = children.elementAt(i);
			if (e.getTag().equals(childTag)) {
				return e;
			}
		}
		return null;
	}

	public String getChildText(String childTag, String defValue) {
		if (getChild(childTag) == null) {
			return defValue;
		} else {
			return getChild(childTag).getText();
		}
	}
}
