package com.bitants.launcherdev.kitset.xmlparser.exception;

/**
 * 节点找不到异常类
 */
public class ElementNotFoundException extends Exception {

    /**
     * 序列ID
     */
	private static final long serialVersionUID = 1L;

	/**
	 * 构造函数
	 */
	public ElementNotFoundException() {
		super();
	}

	/**
	 * 构造函数
	 * @param message 异常消息
	 */
	public ElementNotFoundException(String message) {
		super(message);
	}

}
