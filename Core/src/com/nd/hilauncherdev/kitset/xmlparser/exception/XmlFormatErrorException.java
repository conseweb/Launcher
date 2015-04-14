package com.nd.hilauncherdev.kitset.xmlparser.exception;

/**
 * xml格式异常类
 */
public class XmlFormatErrorException extends Exception {
	/**
	 * 序列ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 构造函数
	 */
	public XmlFormatErrorException() {
		super();
	}

	/**
	 * 构造函数
	 * @param message 异常消息
	 */
	public XmlFormatErrorException(String message) {
		super(message);
	}
}
