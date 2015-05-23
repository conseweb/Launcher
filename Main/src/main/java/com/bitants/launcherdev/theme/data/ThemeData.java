package com.bitants.launcherdev.theme.data;

import com.bitants.common.theme.data.BaseThemeData;
import com.bitants.common.theme.module.ModuleConstant;

/**
 * <br>Title: 主题皮肤信息常量
 */
public class ThemeData extends BaseThemeData {
	
	/** 情景主题ID */
	public final static String SCENE_ID = "scene_id";
	
	//-------91快捷---------begin//
	/**应用列表*/
	public static final String MAIN_DOCK_ALLAPPS="main_dock_allapps";
	
	//-------91快捷---------end//
	
	/**
	 * 初次安装dockbar中的【程序列表】正常态
	 */
	public static final String PANDAHOME_STYLE_ICON_TRAY_EXPAND = "pandahome_style_icon_tray_expand";
	/**
	 * 初次安装dockbar中的【程序列表】点击态
	 */
	public static final String PANDAHOME_STYLE_ICON_TRAY_EXPAND_PRESSED = "pandahome_style_icon_tray_expand_pressed";
	/**
	 * 图片key集合(0.图片key 1.所属组件ID)
	 */
	public static final String [][] drawableKeys = { 
	};
	
    /**
     * 多态图片
     */
	public static String[] statelistDrawableKeys = {
    };
    
	public static final String[] DEFAULT_FOLDER_APP = {  ICON_ALARMCLOCK, ICON_CALCULATOR,
            ICON_MUSIC, ICON_CALENDAR, ICON_EMAIL, ICON_GALLERYPICKER,/*ICON_VIDEO_CAMERA,*/ ICON_SETTINGS };
	
	/**
	 * 大图标模式的高清图标集合(除iconKeys数据里的图标)
	 */
	private static final String[] largeIconKeys = new String[] { 
			};
    
	@Override
	public void buildThemeData() {
		super.buildThemeData();
		// 主题大图标声明
		for(int i=0; i<largeIconKeys.length; i++){
    		largeIconMap.put(largeIconKeys[i], ModuleConstant.MODULE_ICONS);
    	}
		// 主题图片声明
		for(int i=0; i<drawableKeys.length; i++){
    		drawableMap.put(drawableKeys[i][0], drawableKeys[i][1]);
    	}
	}
	
}
