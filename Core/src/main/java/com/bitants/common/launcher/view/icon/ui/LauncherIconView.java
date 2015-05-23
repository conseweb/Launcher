package com.bitants.common.launcher.view.icon.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.bitants.common.kitset.util.ColorUtil;
import com.bitants.common.kitset.util.PaintUtils2;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.launcher.config.preference.BaseSettingsPreference;
import com.bitants.common.launcher.info.ApplicationInfo;
import com.bitants.common.launcher.view.icon.icontype.IconType;
import com.bitants.common.launcher.view.icon.receiver.DefaultReceiverManager;
import com.bitants.common.launcher.view.icon.receiver.LauncherIconViewReceiver;
import com.bitants.common.launcher.view.icon.ui.strategy.DrawStrategy;
import com.bitants.common.launcher.view.icon.ui.util.IconTypeFactoryManager;
import com.bitants.common.launcher.support.BaseIconCache;

/**
 * 桌面IconMaskTextView、AppMaskTextView、DockBarCell类的基类
 * 主要类说明：
 * LauncherIconViewConfig: 绘画的配置类、里面的一系列布尔值代表着不同的绘画策略，当值为true时，该策略生效。
 * LauncherIconData: 根据view在layout时所获得的width、height来生成不同绘画策略所需的数据,如iconRect、textHeight、
 *               titlePaint等
 * 
 * IconType: 根据ApplicationInfo的不同来获取不同的iconType、主要是判断接收的广播及处理方式、及图标刷新的方法(refreshUI())。
 * LauncherIconViewReceiver:广播的处理类 最后委托到对应的IconType来进行注册和接收
 *                 
 * 主要工作流程:一般我们先  new IconMaskTextView()(以此类为例,其它类似); 此处调用initParams来初始化上述的主要类(除了IBehavior).
 *                        setTag(Object); 根据Object(为ApplicationInfo、或AnythingInfo)来获取IBehavior,调用behavior中的方法
 *                                        来判断是否需要画蒙板;
 *                        onAttachedToWindow(); 根据ApplicationInfo来注册不同的广播，调用refreshUI();这里又会调用behavior中
 *                                              的refreshIcon来获取图标。
 *                        onLayout(); 根据layout的宽高调用initValue(widthSize, heightSize)(抽象方法,不同子类具体实现); 此处会设置IconMaskData中的值，然后调用
 *                                    component.setDrawStategyData();来设置各个绘画策略的数据。 
 *                        onDraw();   数据都已准备好了,调用LauncherIconViewConfig中的container来进行绘画。                           
 *                                      
 *
 */
public abstract class LauncherIconView extends View implements LauncherIconViewReceiver.IconMaskUpdateListener {

	private static final String Tag = "LauncherIconView";

	/**
	 * 绘画策略配置
	 */
	protected LauncherIconViewConfig config;

	/**
	 * 绘画策略所需数据
	 */
	protected LauncherIconData data;

	/**
	 * 广播管理类
	 */
	protected LauncherIconViewReceiver receiver;
	
	protected IconType iconType;

	protected Bitmap icon;

	protected ApplicationInfo appInfo;
	
	protected String savedText;
	
	protected boolean isDataReady;
	
	protected boolean isNeedRegisterBroadcastReceiver = true;
	

	protected Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0 && appInfo != null) {
				setIconBitmap(appInfo.iconBitmap);
				setText(appInfo.title);
				invalidate();
				return;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public LauncherIconView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initParams(attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public LauncherIconView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initParams(attrs);
	}

	/**
	 * @param context
	 */
	public LauncherIconView(Context context) {
		super(context);
		initParams(null);
	}

	/**
	 * 初始化参数
	 * 
	 */
	protected void initParamsDefault(AttributeSet attrs) {
		if (config == null) {
			config = new LauncherIconViewConfig(getContext());
		}
		if (data == null) {
			data = createIconMaskData(attrs);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int widthSize = right - left;
		int heightSize = bottom - top;
		initValue(widthSize, heightSize);
		data.setTitle(savedText);
		isDataReady = true;
	}
	
	/**
	 * 参数初始化
	 *  @param attrs
	 */
	abstract protected void initParams(AttributeSet attrs);

	/**
	 * 当宽、高发生变化重新计算绘画数据
	 * 
	 * @param w
	 * @param h
	 */
	abstract protected void initValue(int w, int h);


	/**
	 * 创建数据
	 * 
	 * @return
	 */
	abstract protected LauncherIconData createIconMaskData(AttributeSet attrs);
	

	/**
	 * 程序图标
	 */
	public void setIconBitmap(Bitmap bitmap) {
		icon = bitmap;
		data.setIcon(bitmap);
	}

	@Override
	public void setTag(Object tag) {
		super.setTag(tag);
		appInfo = IconTypeFactoryManager.getAppFromTag(tag);
		iconType = IconTypeFactoryManager.getIconType(tag);
	}

	/**
	 * 标题
	 * 
	 * @param text
	 */
	public void setText(CharSequence text) {
		if (StringUtil.isEmpty(text)) {
			config.setDrawText(false);
			return;
		}
		savedText = text.toString();
		data.setTitle(text);
		config.setDrawText(true);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if(isNeedRegisterBroadcastReceiver && iconType != null){
			LauncherIconViewReceiver receiver = new LauncherIconViewReceiver(this);
			if(DefaultReceiverManager.registerReceiver(receiver, getContext(), iconType.getIntentFilter(this))){
				this.receiver = receiver;
			}
		}
		refreshUI();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(isNeedRegisterBroadcastReceiver && iconType != null){
			DefaultReceiverManager.unRegisterReceiver(receiver, this);
		}
	}

	
	public void refreshUI() {
		// TODO Auto-generated method stub
		Object tag = this.getTag();
		if (tag == null || iconType == null)
			return;
		Bitmap icon = iconType.refreshIcon(config, tag, getContext(), handler);
		if (icon != null) {
			setIconBitmap(icon);
			invalidate();
		}
		
	}
	/**
	 * 推荐图标安装后更新IconType
	 */
	public void updateIconType(){
		setTag(this.getTag());
	}

	public class ClickStateShow {
		/**
		 * 点击地缩小的系数
		 * */
		float onTouchScale = 0.9f;

		// 透明图层设置，用于情景桌面
		public void draw(Canvas canvas, float centX, float centY) {
			canvas.scale(onTouchScale, onTouchScale, centX, centY);
		}
	}


	public LauncherIconData getData() {
		return data;
	}

	public IconType getIconType() {
		return iconType;
	}

	public void setIconType(IconType iconType) {
		this.iconType = iconType;
	}
	
	
	protected void drawCanvas(Canvas canvas, LauncherIconViewConfig config, LauncherIconData data){
		SparseArray<DrawStrategy> containter = config.getContainter();
		Rect iconRect = data.getIconRect(config);
		Rect maskRect = data.getMaskRect(config);
		boolean isLargeIconMode = config.isLargeIconMode();
		boolean isDefaultTheme = BaseIconCache.isDefaultThemeWithDefaultModuleId(getContext());
		if(appInfo != null){
			config.setCustomIcon(appInfo.customIcon);
		}
		for(int i = 0; i < containter.size(); i++) {
			  int key = containter.keyAt(i); 
			  DrawStrategy drawStrategy = containter.get(key);
			  drawStrategy.draw(canvas, config, data, iconRect, maskRect, isLargeIconMode, isDefaultTheme);
		}
	}

	public ApplicationInfo getAppInfo() {
		return appInfo;
	}
	
	/**
	 * 获取图标的大小
	 *  @return
	 */
	public Rect getIconRect(){
		if(isDataReady){
			if(config.isLargeIconMode()){
				return data.iconRects.maxRectAndScale.rect;
			}else{
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.MEDUIM_ICON_SIZE)
					return data.iconRects.mediumRectAndScale.rect;
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.SMALL_ICON_SIZE)
					return data.iconRects.minRectAndScale.rect;
				if(BaseSettingsPreference.getInstance().getAppIconType() == IconSizeManager.LARGE_ICON_SIZE)
					return data.iconRects.maxRectAndScale.rect;
			}
		}
		return new Rect();
	}

	@Override
	public void updateText() {
		int textColor = BaseSettingsPreference.getInstance().getAppNameColor();
		int shadowColor = ColorUtil.antiColorAlpha(255, textColor);
		Paint paint = data.titlePaint;
		paint.setColor(textColor);
		data.alphaPaint.setColor(textColor);
		paint.setShadowLayer(1, 1, 1, shadowColor);
		paint.setTextSize(BaseSettingsPreference.getInstance().getAppNameSize());
		PaintUtils2.assemblyTypeface(paint);
	}

	/**
	 * @return the config
	 */
	public LauncherIconViewConfig getConfig() {
		return config;
	}
	
	
	

}
