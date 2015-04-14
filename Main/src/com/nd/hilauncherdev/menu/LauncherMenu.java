package com.nd.hilauncherdev.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.nd.hilauncherdev.launcher.Launcher;
import com.nd.hilauncherdev.launcher.Workspace;
import com.nd.hilauncherdev.launcher.edit.LauncherEditView;
import com.nd.hilauncherdev.launcher.screens.BaseLauncherMenu;
import com.nd.hilauncherdev.settings.HomeSettingsActivity;
import com.bitants.launcher.R;

public class LauncherMenu extends BaseLauncherMenu{
	private MenuAdapter firScreenAdapter;
	private List<MenuItemInfo> firList;

	private WeakHashMap<String, Bitmap> bitmapCache = new WeakHashMap<String, Bitmap>();

	/**
	 * MNnu 菜单 -- 添加
	 */
	public static final String MENU_ADD = "menu_add";
	public static final String MENU_THEME = "menu_theme";
	public static final String MENU_EFFECT = "menu_effect";
	public static final String MENU_SYS_SETTING = "menu_sys_setting";
	public static final String MENU_CUSTOM_SETTING = "menu_custom_setting";
	public static final String MENU_SHARE = "menu_share";
	
	public final static String[] FIR_SCREEN_BITMAP_KEY = new String[] { 
		MENU_ADD,// 添加小部件0
		MENU_THEME,// 美化手机1
		MENU_EFFECT,// 特效选择2
		MENU_SYS_SETTING,//系统设置3
		MENU_SHARE,//分享4
		MENU_CUSTOM_SETTING//桌面设置5
	};

	public LauncherMenu(Launcher context) {
		super(context);
	}

	@Override
	public void setContent(){
		GridView gridView = (GridView) layout.findViewById(R.id.dataView);
		// 取得数据
		firList = new ArrayList<MenuItemInfo>();
		for (int i = 0; i < FIR_SCREEN_BITMAP_KEY.length; i++) {
			MenuItemInfo item = new MenuItemInfo();
			item.bitmap = getBitmap(FIR_SCREEN_BITMAP_KEY[i]);
			item.key = FIR_SCREEN_BITMAP_KEY[i];
			item.text = context.getResources().getText(key2StringId(FIR_SCREEN_BITMAP_KEY[i])).toString();
			firList.add(item);
		}
		// Adapter
		firScreenAdapter = new MenuAdapter(context);
		firScreenAdapter.setItemInfos(firList);
		gridView.setAdapter(firScreenAdapter);
		// 点击事件
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (MENU_ADD.equals(view.getTag().toString())) {
					((Workspace)context.mWorkspace).changeToSpringMode(LauncherEditView.TAB_ADD);
				} 
				else if (MENU_THEME.equals(view.getTag().toString())) {
					((Workspace)context.mWorkspace).changeToSpringMode(LauncherEditView.TAB_THEME);
				} 
				else if (MENU_EFFECT.equals(view.getTag().toString())) {
					((Workspace)context.mWorkspace).changeToSpringMode(LauncherEditView.TAB_EFFECT);
				} 
				else if (MENU_SYS_SETTING.equals(view.getTag().toString())) {
					context.startActivity(new Intent(Settings.ACTION_SETTINGS));
				} 
				else if (MENU_CUSTOM_SETTING.equals(view.getTag().toString())) {
					Intent intent = new Intent();
					intent.setClass(context,HomeSettingsActivity.class);
					context.startActivity(intent);
				} 
				else if (MENU_SHARE.equals(view.getTag().toString())) {
					
				}
				menuWindow.dismiss();
			}
		});
	}
	
	
	@Override
	public void applyTheme() {
		// 添加获取主题背景图
		if(menuWindow != null){			
			menuWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.menu_bg));
		}
	}

	
	/**
	 * 获取bitmap
	 * 
	 * @param key
	 * @return
	 */
	private Bitmap getBitmap(String key) {
		if(bitmapCache == null){
			bitmapCache = new WeakHashMap<String, Bitmap>();
		}
		if (key != null && bitmapCache.get(key) != null) {
			return bitmapCache.get(key);
		}
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), key2DrawableId(key));
		bitmapCache.put(key, bitmap);
		return bitmap;
	}

	private int key2DrawableId(String key) {
		int id = 0;
		// 第一屏图片转换
		if (FIR_SCREEN_BITMAP_KEY[0].equals(key)) {
			id = R.drawable.menu_fir_fir;
		} else if (FIR_SCREEN_BITMAP_KEY[1].equals(key)) {
			id = R.drawable.menu_fir_sec;
		} else if (FIR_SCREEN_BITMAP_KEY[2].equals(key)) {
			id = R.drawable.menu_fir_thi;
		} else if (FIR_SCREEN_BITMAP_KEY[3].equals(key)) {
			id = R.drawable.menu_fir_for;
		} else if (FIR_SCREEN_BITMAP_KEY[4].equals(key)) {
			id = R.drawable.menu_fir_five;
		} else if (FIR_SCREEN_BITMAP_KEY[5].equals(key)) {
			id = R.drawable.menu_fir_six;
		}
		return id;
	}
	
	private int key2StringId(String key) {
		int id = 0;
		// 第一屏文字转换
		if ((FIR_SCREEN_BITMAP_KEY[0]).equals(key)) {
			id = R.string.menu_add;
		} else if ((FIR_SCREEN_BITMAP_KEY[1]).equals(key)) {
			id = R.string.menu_theme;
		} else if ((FIR_SCREEN_BITMAP_KEY[2]).equals(key)) {
			id = R.string.menu_effect;
		} else if ((FIR_SCREEN_BITMAP_KEY[3]).equals(key)) {
			id = R.string.menu_sys_setting;
		} else if ((FIR_SCREEN_BITMAP_KEY[4]).equals(key)) {
			id = R.string.menu_share;
		} else if ((FIR_SCREEN_BITMAP_KEY[5]).equals(key)) {
			id = R.string.menu_custom_setting;
		}
		return id;
	}

	@Override
	public void fontChange() {
//		firScreenAdapter.notifyDataSetChanged();
	}


}
