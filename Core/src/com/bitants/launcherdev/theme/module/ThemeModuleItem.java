package com.bitants.launcherdev.theme.module;

import java.io.Serializable;

/**
 * Description: 主题模块项 <br>
 */
public class ThemeModuleItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 模块类型--主题包模块
	 */
	public final static int TYPE_THEME = 0;
	/**
	 * 模块类型--单一模块包
	 */
	public final static int TYPE_MODULE = 1;
	/**
	 * 模块KEY
	 */
	private String key;
	/**
	 * 模块对应的插件或应用包名
	 * 如天气皮肤可对应多个天气插件，多个包名时以;分隔。com.nd.weather.widget;com.baidu.weather.widget
	 */
	private String pgk;
	/**
	 * 模块所属主题ID
	 */
	private String id;
	/**
	 * 模块类型(0.主题 1.模块包)
	 */
	private int type;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPgk() {
		return pgk;
	}

	public void setPgk(String pgk) {
		this.pgk = pgk;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
