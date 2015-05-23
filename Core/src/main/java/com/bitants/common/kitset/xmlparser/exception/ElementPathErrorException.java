package com.bitants.common.kitset.xmlparser.exception;

/**
 * 节点路径错误异常
 */
public class ElementPathErrorException extends Exception {
	/**
	 * 序列ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 构造函数
	 */
	public ElementPathErrorException() {
		super();
	}

	/**
	 * 构造函数
	 * @param message 异常消息
	 */
	public ElementPathErrorException(String message) {
		super(message);
	}
}
