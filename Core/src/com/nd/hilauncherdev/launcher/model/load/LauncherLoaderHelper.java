package com.nd.hilauncherdev.launcher.model.load;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContentProviderOperation.Builder;
import android.view.View;
import android.view.ViewGroup;

import com.nd.hilauncherdev.launcher.BaseLauncher;
import com.nd.hilauncherdev.launcher.info.ApplicationInfo;
import com.nd.hilauncherdev.launcher.info.ItemInfo;
import com.nd.hilauncherdev.launcher.model.BaseLauncherModel;
import com.nd.hilauncherdev.launcher.screens.ScreenViewGroup;
import com.nd.hilauncherdev.launcher.screens.dockbar.BaseMagicDockbar;
import com.nd.hilauncherdev.launcher.screens.CellLayout;
import com.nd.hilauncherdev.launcher.view.icon.ui.LauncherIconViewConfig;
import com.nd.hilauncherdev.launcher.view.icon.ui.folder.FolderIconTextView;

/**
 * Description: 桌面加载和情景桌面辅助类接口(需继续调整)
 * Author: guojy
 * Date: 2013-10-22 上午11:02:52
 */
public abstract class LauncherLoaderHelper {
	
	/**
	 * 桌面加载时，从数据库读取Favorites数据
	 * @param context
	 * @param loader
	 * @param mModel
	 * @return false表示加载异常
	 */
	public abstract boolean loadFavoritesDataFromDB(Context context, LauncherLoader loader, BaseLauncherModel mModel);
	
	/**
	 * 绑定数据
	 * @param shortcuts
	 * @param start
	 * @param end
	 * @param workspace
	 * @param mDockbar
	 */
	public abstract void bindItems(List<ItemInfo> shortcuts, int start, int end, BaseLauncher mLauncher,
			ScreenViewGroup workspace, BaseMagicDockbar mDockbar);
	
	/**
	 * 应用更新重新绑定时，更新workspace上的图标
	 * @param apps
	 * @param mLauncher
	 */
	public abstract void updateAppsInWorkspace(List<ApplicationInfo> apps, BaseLauncher mLauncher);
	
	/**
	 * 是否为我的手机对象
	 * @param item
	 * @return
	 */
	public boolean isMyPhoneItem(ItemInfo item){
		return false;
	}
	
	/**
	 * 新安装用户设置是否显示应用的名称
	 * @param mContext
	 */
	public void setShowDockbarTitleForNewInstall(Context mContext){
		
	}
	
	/**
	 * dockbar是否显示应用的名称
	 * @return
	 */
	public abstract boolean isShowDockbarText();
	
	/**
	 * 初始化打点统计
	 */
	public abstract void initHiAnalytics(Context mContext);
	
	
	/**
	 * 启动打点统计
	 * @param mContext
	 */
	public abstract void startUpHiAnalytics(Context mContext);
	
	
	/**
	 * load所有app后回调
	 * @param callback
	 * @param handler
	 * @param mContext
	 */
	public abstract void loadAndBindAllApps(Callbacks callback, DeferredHandler handler, Context mContext);
	
	/**
	 * new一个CellLayout的LayoutParams
	 * @author dingdj
	 * Date:2014-3-26下午3:09:33
	 */
	public abstract CellLayout.LayoutParams newCellLayoutLayoutParams(ViewGroup.MarginLayoutParams vm);
	
	
	//====================================以下用于普通图标和情景桌面图标=============================//
	/**
	 * 是否为新安装应用，用于绘制new图标
	 * @param info
	 * @return
	 */
	public boolean isNewInstallApp(ApplicationInfo info) {
		return false;
	}
	
	/**
	 *  View在workspace的当前屏时候回调
	 * @param view
	 * @return 是否调用成功
	 */
	public boolean onWorkspaceCurrentScreen(View view){
		return false;
	}
	
	/**
	 * 获取holeType
	 * @author dingdj
	 * Date:2014-3-26下午3:14:59
	 *  @param view
	 *  @param info
	 *  @param wh
	 */
	public void getLeftTopXYByHoleType(FolderIconTextView view, ApplicationInfo info, int[] wh){
		
	}
	
	/**
	 * 是否icon图标填充整个View
	 * @author dingdj
	 * Date:2014-3-27下午12:05:57
	 *  @param view
	 *  @return
	 */
	public boolean isSceneFillContentView(View view){
		return false;
	}
	
	/**
	 * 是否按短边缩放的形式填充整个View
	 * @author dingdj
	 * Date:2014-3-27下午12:06:01
	 *  @param view
	 *  @return
	 */
	public boolean isSceneFillContentFitCenter(View view){
		return false;
	}
	
	/**
	 * 情景桌面下，是否显示图标的名称
	 * @author dingdj
	 * Date:2014-3-27下午12:06:01
	 *  @param view
	 *  @return
	 */
	public boolean isShowTextOnScene(){
		return false;
	}
	
	/**
	 * Description: 是否该View被锁定无法拖动和合并文件夹
	 * @author dingdj
	 * Date:2014-4-9下午4:06:30
	 *  @param v
	 *  @return
	 */
	public boolean isLockedView(View v){
		return false;
	}
	
	/**
	 * 获取桌面滑屏特效数组,用于生成随机特效,数组的前两个特效分别为默认、随机
	 * @return
	 */
	public int[] getScreenEffectsForRandom(){
		return null;
	}
	
	/**
	 * 获取匣子特效数组,用于生成随机特效,数组的前两个特效分别为默认、随机
	 * @return
	 */
	public int[] getDrawerEffectsForRandom(){
		return null;
	}
	
	//====================================以下用于情景桌面数据=============================//
	/**
	 * 初始化情景桌面CellLayout和DockCellLayout设置
	 * @param mContext
	 */
	public void initForScene(Context mContext){
		
	}
	
	/**
	 * 情景桌面添加数据入库时
	 * @param context
	 * @param values
	 */
	public void onAddSceneItemToDatabase(Context context, ContentValues values){
		
	}
	
	/**
	 * 情景桌面批量添加数据入库时
	 * @param context
	 * @param values
	 */
	public void onAddSceneItemsToDatabase(Context context, Builder builder){
		
	}
	
	/**
	 * 情景桌面下，spanX spanY与实际像素大小匹配
	 * @param spanX
	 * @param spanY
	 * @param cellWidth
	 * @param cellHeight
	 * @param item
	 * @return
	 */
	public int[] spanXYMatherForScene(int spanX, int spanY, int cellWidth, int cellHeight, Object item){
		return null;
	}

	/**
	 * 初始化情景模式下的数据
	 * @author dingdj
	 * Date:2014-3-24下午4:56:53
	 *  @param width
	 *  @param height
	 *  @param view
	 */
	public void initSceneWH(View view, LauncherIconViewConfig config, boolean calcRect){
		
	}
}
