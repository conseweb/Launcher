package com.nd.launcherdev.launcher.edit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;

import com.nd.launcherdev.framework.effect.EffectConfig;
import com.nd.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.nd.launcherdev.kitset.util.FileUtil;
import com.nd.launcherdev.kitset.util.TelephoneUtil;
import com.nd.launcherdev.launcher.edit.data.LauncherEditAddItemInfo;
import com.nd.launcherdev.launcher.edit.data.LauncherEditEffectItemInfo;
import com.nd.launcherdev.launcher.edit.data.LauncherEditIndividalItemInfo;
import com.nd.launcherdev.launcher.edit.data.LauncherEditThemeItemInfo;
import com.nd.launcherdev.launcher.edit.data.LauncherEditWallpaperItemInfo;
import com.nd.launcherdev.widget.LauncherWidgetInfo;
import com.nd.launcherdev.widget.LauncherWidgetInfoManager;
import com.bitants.launcher.R;
import com.nd.launcherdev.framework.effect.EffectConfig;
import com.nd.launcherdev.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.nd.launcherdev.kitset.util.TelephoneUtil;
import com.nd.launcherdev.launcher.edit.data.LauncherEditEffectItemInfo;
import com.nd.launcherdev.launcher.edit.data.LauncherEditIndividalItemInfo;
import com.nd.launcherdev.widget.LauncherWidgetInfo;

/**
 * 编辑模式添加数据集数据
 * 
 * @author Anson
 */
public class LauncherEditDataFactory {
	
	private static final String[] TYPES_WALLPAPER = new String[] { ".jpg", ".jpeg", ".gif", ".png", ".bmp", ".wbmp" };
	
	/**
	 * 获取屏幕编辑模式添加数据列表
	 * 
	 * @param context
	 * @return
	 */
	public static List<LauncherEditAddItemInfo> getAddDataInfos(Context context) {
		List<LauncherEditAddItemInfo> result = new ArrayList<LauncherEditAddItemInfo>();
		result.add(LauncherEditAddItemInfo.makeWidgetSystemItemInfo(context));
		result.add(LauncherEditAddItemInfo.makeAddAppslistItemInfo(context));
		return result;
	}
	
	/**
	 * 获取个性化数据列表
	 * 
	 * @param context
	 * @return
	 */
	public static List<LauncherEditIndividalItemInfo> getIndividalDataInfos(Context context) {
		List<LauncherEditIndividalItemInfo> result = new ArrayList<LauncherEditIndividalItemInfo>();
		return result;
	}
	
	/**
	 * 获取小部件数据列表
	 */
	public static List<ICommonDataItem> getWidgetDataInfos(Context context) {
		ArrayList<ICommonDataItem> widgetList = new ArrayList<ICommonDataItem>();
		/**加载91小部件信息*/	
		widgetList.addAll(LauncherWidgetInfoManager.getInstance().loadAllInstalledLauncherWidgetInfos(false));
		/**加载内部小部件信息*/
		widgetList.addAll(LauncherWidgetInfoManager.getInstance().loadAllLauncherWidgetInside());
		Collections.sort(widgetList, LauncherWidgetInfoManager.WIDGET_TITLE_COMPARATOR);
		/**系统小部件入口*/
		widgetList.add(0, LauncherWidgetInfo.makeSystemWidget());
		return widgetList;
	}
	
	/**
	 * 获取主题数据列表
	 */
	public static List<LauncherEditThemeItemInfo> getThemeDataInfos(Context context) {
		List<LauncherEditThemeItemInfo> result = new ArrayList<LauncherEditThemeItemInfo>();
		result.add(LauncherEditThemeItemInfo.makeOnLineThemeItem(context));

		/*List<SimpleTheme> simpleThemeList = new ArrayList<SimpleTheme>();
		
		SimpleTheme defaultTheme = new SimpleTheme();
		defaultTheme.id = ThemeGlobal.DEFAULT_THEME_ID;
		defaultTheme.name = context.getResources().getString(R.string.launcher_edit_mode_theme_online);
		defaultTheme.enName = context.getResources().getString(R.string.launcher_edit_mode_theme_default);
		simpleThemeList.add(defaultTheme);
		
		simpleThemeList.addAll(ThemeOperator.getThemeListBySceneId(CommonGlobal.DEFAULT_SCENE_ID));
		
		for (int i = 0; i < simpleThemeList.size(); i++) {
			SimpleTheme simpleTheme = simpleThemeList.get(i);
			LauncherEditThemeItemInfo themeItemInfo = new LauncherEditThemeItemInfo();
			themeItemInfo.themeId = simpleTheme.id;
			themeItemInfo.themeType = simpleTheme.themeType;
			themeItemInfo.aptPath = simpleTheme.aptPath;
			themeItemInfo.icon = context.getResources().getDrawable(R.drawable.theme_default_thumb);
			if(simpleTheme.isRecommend) {
				themeItemInfo.type = LauncherEditThemeItemInfo.TYPE_THEME_RECOMMEND;
				themeItemInfo.serverThemeId = simpleTheme.serverThemeId;
				if(null != simpleTheme.preview){
					themeItemInfo.icon = simpleTheme.preview.get();
				}
			} else {
				themeItemInfo.type = LauncherEditThemeItemInfo.TYPE_THEME;
			}
			if (Global.isZh()) {
				themeItemInfo.title = simpleTheme.name;
			} else {
				themeItemInfo.title = simpleTheme.enName;
			}
			result.add(themeItemInfo);
		}*/
		return result;
	}
	
	
	/**
	 * 获取壁纸数据列表
	 */
	public static List<LauncherEditWallpaperItemInfo> getWallpaperDataInfos(Context context) {
		List<LauncherEditWallpaperItemInfo> wallpapers = new ArrayList<LauncherEditWallpaperItemInfo>();
		
		/**内置壁纸应用项*/
		wallpapers.add(LauncherEditWallpaperItemInfo.makeWallpaperScrollItemInfo(context));
		wallpapers.add(LauncherEditWallpaperItemInfo.makeWallpaperOnlineItemInfo(context));
		wallpapers.add(LauncherEditWallpaperItemInfo.makeWallpaperPhotoItemInfo(context));
		
		/**桌面壁纸*/
		List<LauncherEditWallpaperItemInfo> moboWallpapers = 
				getPandaHomeWallpapers(getMoboWallpaperDir(),context,LauncherEditWallpaperItemInfo.TYPE_WALLPAPER_MOBO);
		
		if (moboWallpapers != null && !moboWallpapers.isEmpty()) {
			Collections.sort(moboWallpapers,new Comparator<LauncherEditWallpaperItemInfo>(){
				@Override
			    public int compare(LauncherEditWallpaperItemInfo itemInfo1, LauncherEditWallpaperItemInfo itemInfo2) {  
					File file1 = new File(itemInfo1.path);
					File file2 = new File(itemInfo2.path);
					if(file1.lastModified() > file2.lastModified()) {
						return -1;
					}else{
						return 1;
					}
			    }
			});
			wallpapers.addAll(moboWallpapers);
		}
		return wallpapers;
	}
	
	
	/**
	 * 获取滑屏特效数据列表
	 */
	public static List<LauncherEditEffectItemInfo> getSlideEffectDataInfos(Context context) {
		List<LauncherEditEffectItemInfo> result = new ArrayList<LauncherEditEffectItemInfo>();
		Resources res = context.getResources();
		String[] effects = res.getStringArray(R.array.settings_common_effects_screen_array);
		int[] effectValues = EffectConfig.SCREEN_EFFECT_VALUES;
		String[] effectDrawables = res.getStringArray(R.array.settings_common_effects_screen_drawable);
		for (int i = 0; i < effects.length; i++) {
			LauncherEditEffectItemInfo info = new LauncherEditEffectItemInfo();
			info.title = effects[i];
			info.icon = res.getDrawable(res.getIdentifier(effectDrawables[i], "drawable", context.getPackageName()));
			info.type = effectValues[i];
			result.add(info);
		}
		return result;
	}
	
	
	private static String getMoboWallpaperDir() {
		return FileUtil.getPath(Environment.getExternalStorageDirectory() + "/Dianxinos/myphone/wallpaper/Pictures/");
	}
	
	/**
	 * 获取壁纸
	 * @author Michael
	 * Date:2014-1-28下午2:54:59
	 * @param path
	 * @param context
	 * @param type
	 * @return
	 */
	private static List<LauncherEditWallpaperItemInfo> getPandaHomeWallpapers(String path, Context context, int type) {
		List<LauncherEditWallpaperItemInfo> result = null;
		if (!TelephoneUtil.isSdcardExist()) {
			return result;
		}

		File f = new File(path);
		if (f == null || !f.exists()) {
			return result;
		}

		File[] files = f.listFiles(new FilenameFilter() {
			public boolean accept(File fl, String path) {
				File file = new File(fl + "/" + path);
				if (!file.exists() || !file.canRead()) {
					return false;
				} else if (file.isDirectory()) {
					return true;
				}
				String filename = file.getName();
				boolean isAccept = false;
				for (String type : TYPES_WALLPAPER) {
					int index = filename.lastIndexOf(".");
					if (index == -1)
						return false;
					isAccept = filename.substring(index).equalsIgnoreCase(type) ? true : false;
					if (isAccept) {
						break;
					}
				}
				return isAccept;
			}
		});
		
		if (files == null || files.length <= 0) {
			return result;
		}

		result = new ArrayList<LauncherEditWallpaperItemInfo>();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file == null || !file.exists()) {
				continue;
			}
			LauncherEditWallpaperItemInfo info = new LauncherEditWallpaperItemInfo();
			try {
				info.type = type;
				info.path = file.getCanonicalPath();
				info.title = file.getCanonicalPath();
				result.add(info);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
