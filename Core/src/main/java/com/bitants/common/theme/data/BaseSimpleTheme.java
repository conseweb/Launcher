package com.bitants.common.theme.data;

import java.lang.ref.WeakReference;

import android.graphics.drawable.Drawable;

/**
 * <br>Description: 简单主题类,只记录主题主要信息
 */
public class BaseSimpleTheme {

	/**
	 * 主题Id
	 */
    public String id = "";
    
    /**
     * 主题唯一标识
     */
    public String IDFlag = "" ;
    
    /**
     * 中文名称
     */
    public String name = "";

    /**
     * 英文名称
     */
    public String enName = "";
    
    /**
     * 中文描述
     */
    public String desc = "";
    
    /**
     * 英文描述
     */
    public String enDesc = "";

    /**
     * 主题包版本
     */
    public String version = "";
    
    /**
     * 魔镜桌面标识
     */
    public int savedFlag = -1;

    /**
     * 主题包版本号
     */
    public int versionCode = -1;
    
    /**
     * 主题类型
     */
    public int themeType = 0;
    
    /**
     * 主题安装时间
     */
    public long installTime = 0;
    
    /**
     * apt主题资源目录
     */
    public String aptPath = "";
    
    /**
     * 服务端定义的资源类型(用于升级)
     */
    public int resType = 0;
    
    public boolean supportV6 = false;
    /**
     * 主题资源是否已被加密
     */
    public boolean guarded = false;
    /**
     * 主题资源加密算法版本号
     */
    public int guardedVersion = 1;
    /**
     * 主题支持的桌面最低版本(该版本号大于桌面版本号时，表示桌面版本过低，不支持该主题)
     */
    public int launcherMinVersion = 5998;
    
    /**
     * 主题预览图
     */
    public WeakReference<Drawable> preview;
    
    /**
     * 服务器ID
     */
    public String serverThemeId = "";
    
}