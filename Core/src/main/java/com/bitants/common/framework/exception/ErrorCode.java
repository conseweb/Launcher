package com.bitants.common.framework.exception;

import android.util.SparseArray;

/**
 * 错误代码类
 *
 */
public class ErrorCode {
	
	/**其他错误**/
    public final static int OTHER_ERROR_CODE = 10000;
    
    /**Sdcard不存在**/
    public final static int SDCARD_UNFOUND_CODE = 10001;

    /**主题文件未发现**/
    public final static int THEME_XML_FILE_UNFOUND_CODE = 10002;

    /** 校验zip压缩文件错误异常代码*/
    public static final int ZIP_VALIDATE_ERROR_CODE = 10003;

    /**XML文件格式错误。*/
    public static final int XML_FORMAT_ERROR_CODE = 10004;

    /**xml格式错误根节点必须要ID属性*/
    public static final int XML_FORMAT_ID_UNFOUND_ERROR = 10005;

    /**xml格式错误根节点name属性和en_name属性至少需要一个*/
    public static final int XML_FORMAT_NAME_UNFOUND_ERROR = 10006;
    
    /**主题文件夹重命名失败*/
    public static final int FOLDER_RENAME_ERROR = 10007;
    
    /**不支持加密算法版本，需要下载最新桌面包*/
    public static final int NOT_SUPPORT_GUARD_VERSION_ERROR = 10008;
    
    /**主题加密包合法性验证失败*/
    public static final int GUARD_VALIDATE_ERROR = 10009;
    
    /**主题数据记录保存失败*/
    public static final int THEME_DATA_SAVE_ERROR = 10010;

    /**
     * 构造函数
     */
    private ErrorCode() {

    }

    /**
     * 错误码map
     */
    private static SparseArray<String> errorCodeMsgArray;

    /**
     * 是否创建
     */
    private static boolean isCreate = false;

    /**
     * 单例对象
     */
    private static ErrorCode errorCode = new ErrorCode();

    /**
     * 初始化实例
     */
    public static void initInstance() {
        if (!isCreate) {
            createInstance();
        } else {
            System.out.println("ErrorCode instance has be created");
        }
    }

    /**
     * 创建实例
     * @return ErrorCode
     */
    public static ErrorCode createInstance() {
        if (!isCreate) {
            initMap();
            isCreate = true;
        }
        return errorCode;
    }

    /**
     * initMap
     */
    private static void initMap() {
    	if(errorCodeMsgArray == null){
    		errorCodeMsgArray = new SparseArray<String>();
    	}
    	
        if (errorCodeMsgArray.size() == 0) {
//            errorCodeMsgArray.put(SDCARD_UNFOUND_CODE, mContext.getResources().getString(R.string.sdcard_unfound_msg));
//            errorCodeMsgArray.put(THEME_XML_FILE_UNFOUND_CODE, mContext.getResources().getString(R.string.theme_xml_unfound_msg));
//            errorCodeMsgArray.put(XML_FORMAT_ID_UNFOUND_ERROR, mContext.getResources().getString(R.string.xml_format_id_unfound_error));
//            errorCodeMsgArray.put(XML_FORMAT_NAME_UNFOUND_ERROR, mContext.getResources().getString(R.string.xml_format_name_unfound_error));
//            errorCodeMsgArray.put(ZIP_VALIDATE_ERROR_CODE, mContext.getResources().getString(R.string.zip_validate_error_msg));
//            errorCodeMsgArray.put(XML_FORMAT_ERROR_CODE, mContext.getResources().getString(R.string.xml_format_error_msg));
        }
    }

    /**
     * 获取错误码列表
     * @return Map
     */
    public SparseArray<String> getErrorCodeMsgMap() {
    	if(errorCodeMsgArray == null){
    		errorCodeMsgArray = new SparseArray<String>();
    	}
        return errorCodeMsgArray;
    }

}
