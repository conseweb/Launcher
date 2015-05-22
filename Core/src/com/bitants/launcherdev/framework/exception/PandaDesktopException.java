package com.bitants.launcherdev.framework.exception;

import com.bitants.launcherdev.kitset.util.StringUtil;


/**
 * 异常类
 */
public class PandaDesktopException extends Exception {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -937407699940229079L;

    /**errorCode*/
    private int errorCode = 0;

    /**
     * 构造函数
     * @param errorCode 错误码
     */
    public PandaDesktopException(int errorCode) {
        super();
        setErrorCode(errorCode);
    }

    /**
     * 获取错误码
     * @return 错误码
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * 设置错误码
     * @param errorCode 错误码
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 获取错误信息
     * @return String
     */
    public String getErrorMesg() {
    	return StringUtil.getNotNullString(ErrorCode.createInstance().getErrorCodeMsgMap().get(errorCode));
    }

}
