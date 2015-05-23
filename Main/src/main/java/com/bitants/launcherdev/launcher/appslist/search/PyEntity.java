package com.bitants.launcherdev.launcher.appslist.search;

public class PyEntity {
	public char srcChar; // 原字符
	public String py; // 拼音
	public String pyNumber; // 拼音转化数字串

	public int hitIndex;
	public boolean hitShort; // 命中简拼
	public boolean hitWhole; // 命中全拼
	public boolean wholeUsed; // 使用全拼

	public PyEntity(String py) {
		this.py = py;
		this.pyNumber = converPyToNumber(py);
	}

	public PyEntity(char ch, String py) {
		this.srcChar = ch;
		this.py = py;
		this.pyNumber = converPyToNumber(py);
	}

	/**
	 * 
	 * 将输入的拼音转成数字
	 * 
	 * @param str
	 * @return
	 */
	public static String converPyToNumber(String str) {
		if (str == null)
			return "";

		char[] chars = str.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (char c : chars) {
			sb.append(getOneNumFromAlpha(c));
		}
		return sb.toString();
	}

	/**
	 * 
	 * 将字母转换成数字
	 * 
	 * @param firstAlpha
	 * @return
	 */
	public static char getOneNumFromAlpha(char firstAlpha) {
		// TODO Auto-generated method stub
		switch (firstAlpha) {
		case 'a':
		case 'b':
		case 'c':
		case 'A':
		case 'B':
		case 'C':
			return '2';
		case 'd':
		case 'e':
		case 'f':
		case 'D':
		case 'E':
		case 'F':
			return '3';
		case 'g':
		case 'h':
		case 'i':
		case 'G':
		case 'H':
		case 'I':
			return '4';
		case 'j':
		case 'k':
		case 'l':
		case 'J':
		case 'K':
		case 'L':
			return '5';
		case 'm':
		case 'n':
		case 'o':
		case 'M':
		case 'N':
		case 'O':
			return '6';
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
			return '7';
		case 't':
		case 'u':
		case 'v':
		case 'T':
		case 'U':
		case 'V':
			return '8';
		case 'w':
		case 'x':
		case 'y':
		case 'z':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
			return '9';
		default:
			return firstAlpha;
		}
	}
}
