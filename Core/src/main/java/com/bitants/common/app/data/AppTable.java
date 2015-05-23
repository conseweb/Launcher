package com.bitants.common.app.data;

import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.launcher.info.ApplicationInfo;


public class AppTable {
	public final static String ID = "_id";
	public final static String PCK = "pck";
	public final static String CLS = "cls";
	public final static String TITLE = "tit";
	public final static String SCREEN = "scr";
	public final static String POS = "pos";
	public final static String CONTAINER = "con";
	public final static String TYPE = "type";
	public final static String INSTALL_TIME = "time";
	public final static String USED_TIME = "used";
	public final static String CELLX = "cellx";
	public final static String CELLY = "celly";
	public final static String ISSYSTEM = "issys";
	public final static String PINYIN = "pinyin";
	public final static String ISHIDDEN = "ishidden";
	
	public final static int INDEX_ID = 0;
	public final static int INDEX_PCK = 1;
	public final static int INDEX_CLS = 2;
	public final static int INDEX_TITLE = 3;
	public final static int INDEX_SCREEN = 4;
	public final static int INDEX_POS = 5;
	public final static int INDEX_CONTAINER = 6;
	public final static int INDEX_TYPE= 7;
	public final static int INDEX_INSTALL_TIME = 8;
	public final static int INDEX_USED_TIME = 9;
	public final static int INDEX_CELL_X = 10;
	public final static int INDEX_CELL_Y = 11;
	public final static int INDEX_PINYIN = 12;
	public final static int INDEX_ISSYSTEM = 13;
	public final static int INDEX_ISHIDDEN = 14;
	
	public final static String TABLE_NAME = "AppTable";

	public static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS 'AppTable' ('"
			+ ID 
			+ "' INTEGER PRIMARY KEY AUTOINCREMENT, '"
			+ PCK
			+ "' varchar(100) NOT NULL, '" 
			+ CLS
			+ "' varchar(100) NOT NULL, '" 
			+ TITLE 
			+ "' varchar(100) NOT NULL, '"
			+ SCREEN 
			+ "' INTEGER default 0, '" 
			+ POS
			+ "' INTEGER, '" 
			+ CONTAINER
			+ "' INTEGER default 0, '" 
			+ TYPE
			+ "' INTEGER default 0, '" 
			+ INSTALL_TIME 
			+ "' INTEGER, '"
			+ USED_TIME
			+ "' INTEGER default 0, '"
			+ CELLX
			+ "' INTEGER default 0, '" 
			+ CELLY
			+ "' INTEGER default 0, '"
			+ PINYIN
			+ "' varchar(100), '"
			+ ISSYSTEM
			+ "' INTEGER default 0, '"
			+ ISHIDDEN
			+ "' INTEGER default 0"
			+ "%1$s"//扩展字段
			+ ")";
	
	private final static String INSERT = "insert into AppTable(%s, %s, %s, %s, %s, %s) values('%s', '%s', '%s', %d, %d, %d)";
	private static final String UPDATE_USEDTIME = "update AppTable set used = used + 1 where pck = '%s' and cls = '%s'";
	
	public final static String TRUNCATE = "Truncate AppTable";
	public final static String SELECT = "SELECT * from AppTable order by " + CONTAINER + ", " + POS;
	public final static String SELECT_EXCEPT_HIDDEN = "SELECT * from AppTable WHERE type = 0 and ishidden = 0  order by " + CONTAINER + ", " + POS;
	public final static String SELECT_ALL_APP = "SELECT * from AppTable where type = 0";
	public final static String SELECT_ALL_APP_SIZE = "SELECT count(*) from AppTable where type = 0";
	public final static String SELECT_RECENT_INSTALLED = "SELECT * from AppTable where type = 0 order by " + INSTALL_TIME + " desc limit %d";
	
	/**语言切换需要*/
	public final static String SELECT_ALL_APP_FOR_LOCALE = "SELECT _id,pck,cls from AppTable where type = 0";
	public final static String UPDATE_APP_TITLE_BY_ID = "UPDATE AppTable SET tit = '%s' WHERE _id = %s";
    public final static String SELECT_APP_NUM_OF_USE = "SELECT _id,pck,cls,tit from AppTable where type = 0 and used > %s order by used desc";
	
	/**
	 * 备份还原匣子
	 * 去除不存在程序
	 */
	public final static String DELETE = "DELETE FROM AppTable WHERE _id = ";

	public static String getInsertSQL(ApplicationInfo info) {
		return String.format(INSERT, PCK, CLS, TITLE, POS, INSTALL_TIME, ISSYSTEM, 
				info.componentName.getPackageName(), info.componentName.getClassName(), info.title == null ? "" : StringUtil.filtrateInsertParam(info.title), info.pos, info.installTime, info.isSystem);
	}
	
	/**
	 * 更新使用次数
	 */
	public static String getUpdateUsedtime(String pck, String cls) {
		return String.format(UPDATE_USEDTIME, pck, cls);
	}
	
	/**
	 * 最近安装应用程序
	 */
	public static String getRcentInstalled(int limit) {
		return String.format(SELECT_RECENT_INSTALLED, limit);
	}
}
