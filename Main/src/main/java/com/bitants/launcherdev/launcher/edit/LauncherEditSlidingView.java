package com.bitants.launcherdev.launcher.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.bitants.launcher.R;
import com.bitants.launcherdev.framework.view.RoundedDrawable;
import com.bitants.common.framework.view.commonsliding.CommonSlidingView;
import com.bitants.common.framework.view.commonsliding.datamodel.ICommonData;
import com.bitants.common.framework.view.commonsliding.datamodel.ICommonDataItem;
import com.bitants.launcherdev.kitset.invoke.ForeignPackage;
import com.bitants.launcherdev.kitset.util.BitmapUtils;
import com.bitants.common.kitset.util.ScreenUtil;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.kitset.util.ThreadUtil;
import com.bitants.launcherdev.launcher.edit.data.LauncherEditEffectItemInfo;
import com.bitants.launcherdev.launcher.edit.data.LauncherEditThemeItemInfo;
import com.bitants.launcherdev.launcher.edit.data.LauncherEditWallpaperItemInfo;
import com.bitants.launcherdev.settings.SettingsPreference;
import com.bitants.common.theme.data.ThemeFormart;
import com.bitants.common.theme.data.ThemeGlobal;
import com.bitants.common.theme.pref.ThemeSharePref;
import com.bitants.launcherdev.widget.LauncherWidgetInfo;

import java.io.File;
import java.util.WeakHashMap;

public class LauncherEditSlidingView extends CommonSlidingView {

	private LauncherEditView launcherEditView;
	private LayoutInflater layoutInflater;
	
	private WeakHashMap<String, View> widgetItemViewCache = new WeakHashMap<String, View>();
	private WeakHashMap<Integer, View> themeItemViewCache = new WeakHashMap<Integer, View>();
	private WeakHashMap<String, Drawable> widgetPreviewImageCache = new WeakHashMap<String, Drawable>();
	private WeakHashMap<String, Drawable> wallpaperCache = new WeakHashMap<String, Drawable>();
	private WeakHashMap<Integer, View> wallpaperItemViewCache = new WeakHashMap<Integer, View>();
	
	private int mIconSize = 48;
		
	public LauncherEditSlidingView(Context context) {
		super(context);
		init();
	}

	public LauncherEditSlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LauncherEditSlidingView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init(){
		
	}

	public void go2FirstScreen() {
		go2Screen(0);
	}

	public void go2Screen(int whichScreen) {
		if(list != null && list.size() == 0){//QA有提bug 低概率此处会出现list大小为0的情况
			return;
		}
		mCurrentData = list.get(0);
		setCurrentScreen(whichScreen);
		scrollTo(whichScreen * pageWidth, 0);
	}
	
	public void setLauncherEditView(LauncherEditView launcherEditView) {
		this.launcherEditView = launcherEditView;
	}
	
	public LauncherEditView getLauncherEditView() {
		return launcherEditView;
	}
	
	
	@Override
	protected void initSelf(Context ctx) {
		this.layoutInflater = LayoutInflater.from(ctx);
	}

	@Override
	public View onGetItemView(ICommonData data, int position) {
		final ICommonDataItem item = data.getDataList().get(position);
		View v = null;
		/**小部件*/
		if (item instanceof LauncherWidgetInfo && ((LauncherWidgetInfo) item).getType() != LauncherWidgetInfo.TYPE_SYSTEM
				&&((LauncherWidgetInfo) item).getType() != LauncherWidgetInfo.TYPE_SYSTEM_CATEGORY) {
			v = createWidgetItemView(data,item);
		}
		/**系统小部件*/
		else if (LauncherEditHelper.isSystemWidgetItem(item)) {
			v = createSystemWidgetItemView(item,position);
		}
		/**主题*/
		else if (LauncherEditHelper.isThemeItem(item)) {
			v = createThemeItemView(item,position);
		}
		/**壁纸*/
		else if (LauncherEditHelper.isWallpaperItem(item)) {
			v = createWallpaperItemView(item,position);
		}
		/**特效*/
		else if (LauncherEditHelper.isEffectItem(item)) {
			v = createEffectItemView(item);
		}
		
		return v;
	}
	
	
	public View createWidgetItemView(ICommonData data,ICommonDataItem item) {
		Context mContext = getContext();
		final LauncherWidgetInfo info = (LauncherWidgetInfo) item;
		String inCacheFlag = info.getPackageName() + "@" + info.getClassName() + "@" + info.getLayoutResName();
		View v = widgetItemViewCache.get(inCacheFlag);
		if (null == v) {
		   v = layoutInflater.inflate(R.layout.launcher_edit_widget_item, this, false);
		   widgetItemViewCache.put(inCacheFlag, v);
		}
		TextView titleTv = (TextView) v.findViewById(R.id.widget_title_view);
		v.setTag(info);
		String title = StringUtil.isAnyEmpty(info.getPreviewTitle()) ? info.getTitle() : info.getPreviewTitle();
		titleTv.setText(title);
		final ImageView iv = (ImageView) v.findViewById(R.id.widget_image_view);
		Drawable previewImage = info.getPreviewImage();
		if (null == previewImage) {
			previewImage = widgetPreviewImageCache.get(inCacheFlag);
			if (null == previewImage) {
				if (info.getPreviewImageResInt() != -1) {
					previewImage = mContext.getResources().getDrawable(info.getPreviewImageResInt());
				} else if(info.getType() == LauncherWidgetInfo.TYPE_DYNAMIC && !StringUtil.isEmpty(info.getPreviewImageResName())){
					try{
						int previewDrawaleId = getContext().getResources().getIdentifier(info.getPreviewImageResName(), "drawable", getContext().getPackageName());
						previewImage =mContext.getResources().getDrawable(previewDrawaleId);
					}catch(Exception e){
						e.printStackTrace();
					}
				}else if (info.getType() == LauncherWidgetInfo.TYPE_OUTSIDE && !StringUtil.isEmpty(info.getPackageName())) {
					try {
						ForeignPackage fp = new ForeignPackage(mContext, info.getPackageName(), false);
						previewImage = new BitmapDrawable(mContext.getResources(), BitmapUtils.decodeStreamABitmap(fp.getContext(),
								fp.getResourceID(info.getPreviewImageResName(), "drawable"), data.getChildViewWidth(), data.getChildViewHeight()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				widgetPreviewImageCache.put(inCacheFlag, previewImage);
			}
		}
		if (null == previewImage) {
			previewImage = info.getIcon();
		}
		iv.setImageDrawable(previewImage);
		return v;
	}
	
	
	public View createSystemWidgetItemView(ICommonDataItem item,int position) {
        Context mContext = getContext();
		View v = widgetItemViewCache.get(position+"");
		if(v == null){
			v = layoutInflater.inflate(R.layout.launcher_edit_widget_item, this, false);
			widgetItemViewCache.put(position+"", v);
		}
		final LauncherWidgetInfo info = (LauncherWidgetInfo) item;
		TextView titleTv = (TextView) v.findViewById(R.id.widget_title_view);
		final ImageView iv = (ImageView) v.findViewById(R.id.widget_image_view);
		iv.setImageDrawable(mContext.getResources().getDrawable(info.getPreviewImageResInt()));
		titleTv.setText(info.getTitle());
		return v;
	}
	
	public View createThemeItemView(ICommonDataItem item,int position) {
        final Context mContext = getContext();
		final LauncherEditThemeItemInfo info = (LauncherEditThemeItemInfo) item;
		View v = themeItemViewCache.get(position);
		if (v == null) {
			v = layoutInflater.inflate(R.layout.launcher_edit_item_boxed,
					this, false);
			themeItemViewCache.put(position, v);
		}
		v.setTag(item);
		final ImageView iv = (ImageView) v
				.findViewById(R.id.item_image_view);
		final TextView tv = (TextView) v.findViewById(R.id.item_text_view);
		ImageView selected = (ImageView) v
				.findViewById(R.id.item_image_selected);
		selected.setVisibility(View.INVISIBLE);
		tv.setText(info.title);
		int type = info.type;
		if(type == LauncherEditThemeItemInfo.TYPE_ONLINE_THEME){
			tv.setVisibility(View.VISIBLE);
			iv.setImageDrawable(info.icon);
			final LayoutParams lp = iv.getLayoutParams();
			lp.height = lp.width = ScreenUtil.dip2px(mContext, mIconSize);
			iv.setLayoutParams(lp);
		}
		else if(type == LauncherEditThemeItemInfo.TYPE_THEME) {
			tv.setVisibility(View.VISIBLE);
			iv.setImageDrawable(info.icon);
			final LayoutParams lp = iv.getLayoutParams();
			lp.width = lp.height = ScreenUtil.dip2px(mContext, mIconSize);
			iv.setLayoutParams(lp);
			final LauncherEditThemeItemInfo themeInfo = (LauncherEditThemeItemInfo) item;
			final String themeId = themeInfo.themeId;
			if (ThemeSharePref.getInstance(mContext).getCurrentThemeId().equals(themeId)) {
				//setLastUsedThemePosition(position);
				selected.setVisibility(View.VISIBLE);
			} else {
				selected.setVisibility(View.INVISIBLE);
			}
			if (wallpaperCache.containsKey(themeId)) {
				info.icon = wallpaperCache.get(themeId);
				iv.setImageDrawable(themeInfo.icon);
			} else {
				final Drawable d = mContext.getResources().getDrawable(
						R.drawable.theme_default_thumb);
				iv.setImageDrawable(d);
				if (!ThemeGlobal.DEFAULT_THEME_ID.equals(themeInfo.themeId)) {
					ThreadUtil.executeMore(new Runnable() {
						@Override
						public void run() {
							try {
								String previewPath =  ThemeGlobal.getThemeThumbPath(themeInfo.themeId, themeInfo.themeType);
								ThemeFormart.createThemeThumbnail(mContext,themeInfo.themeId);
								Bitmap b = BitmapUtils.getImageFile(Uri.parse(previewPath),mContext, lp.width, lp.height);
								themeInfo.icon = new BitmapDrawable(mContext.getResources(), b);
								if (null == themeInfo.icon) {
									themeInfo.icon = d;
									File f = new File(previewPath);
									if (f.exists()) {
										f.delete();
									}
								}
								wallpaperCache.put(themeId,themeInfo.icon);
								handler.post(new Runnable() {
									@Override
									public void run() {
										iv.setImageDrawable(themeInfo.icon);
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							} catch (OutOfMemoryError error) {
								error.printStackTrace();
							}
						}
					});
				}
			}
		}
		return v;
	}
	
	
	public View createWallpaperItemView(ICommonDataItem item,int position) {
        final Context mContext = getContext();
		final LauncherEditWallpaperItemInfo info = (LauncherEditWallpaperItemInfo) item;
		int type = info.type;
		View v = wallpaperItemViewCache.get(position);
		if (v == null) {
			v = layoutInflater.inflate(R.layout.launcher_edit_item_boxed,this, false);
			wallpaperItemViewCache.put(position, v);
		}
		v.setTag(item);
		final ImageView iv = (ImageView)v.findViewById(R.id.item_image_view);
		iv.setBackgroundDrawable(null);
		final TextView tv = (TextView) v.findViewById(R.id.item_text_view);
		ImageView selected = (ImageView) v.findViewById(R.id.item_image_selected);
		selected.setVisibility(View.INVISIBLE);
		tv.setText(info.title);
		if (type == LauncherEditWallpaperItemInfo.TYPE_WALLPAPER_SCROLL 
				|| type == LauncherEditWallpaperItemInfo.TYPE_WALLPAPER_ONLINE
				|| type == LauncherEditWallpaperItemInfo.TYPE_PHOTO) {
			tv.setVisibility(View.VISIBLE);
			iv.setImageDrawable(info.icon);
			tv.setText(info.title);
			LayoutParams lp = iv.getLayoutParams();
			lp.width = lp.height = ScreenUtil.dip2px(mContext, mIconSize);
			iv.setLayoutParams(lp);
		}
		else if(type == LauncherEditWallpaperItemInfo.TYPE_WALLPAPER_MOBO) {
			tv.setVisibility(View.INVISIBLE);
			FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ScreenUtil.dip2px(mContext, mIconSize), 
					ScreenUtil.dip2px(mContext, mIconSize));
			iv.setLayoutParams(p);
			final LauncherEditWallpaperItemInfo wallpaperInfo = (LauncherEditWallpaperItemInfo) item;
			if (wallpaperCache.containsKey(info.path)) {
				info.icon = wallpaperCache.get(wallpaperInfo.path);
				RoundedDrawable d = new RoundedDrawable(((BitmapDrawable)wallpaperInfo.icon).getBitmap());
				d.setScaleType(ScaleType.CENTER_CROP);
				d.setCornerRadius(ScreenUtil.dip2px(mContext, 9));
				iv.setScaleType(ScaleType.FIT_XY);
				iv.setImageDrawable(d);
			} else {
				iv.setScaleType(ScaleType.CENTER_INSIDE);
				iv.setImageResource(android.R.drawable.sym_def_app_icon);
				ThreadUtil.executeMore(new Runnable() {
					@Override
					public void run() {
						try {
							int width = mIconSize;
							int height = mIconSize;
							final Bitmap b = BitmapUtils.getImageFile(Uri.parse(wallpaperInfo.path),mContext
									,ScreenUtil.dip2px(mContext, width),ScreenUtil.dip2px(mContext, height));
							if (b != null) {
								wallpaperInfo.icon = new BitmapDrawable(mContext.getResources(),b);
								wallpaperCache.put(wallpaperInfo.path,wallpaperInfo.icon);
								handler.post(new Runnable() {
									@Override
									public void run() {
										RoundedDrawable d = new RoundedDrawable(((BitmapDrawable)wallpaperInfo.icon).getBitmap());
										d.setCornerRadius(ScreenUtil.dip2px(mContext, 9));
										d.setScaleType(ScaleType.CENTER_CROP);
										iv.setScaleType(ScaleType.FIT_XY);
										iv.setImageDrawable(d);
									}
								});
							}
						} catch (Exception e) {
							e.printStackTrace();
						} catch (OutOfMemoryError error) {
							error.printStackTrace();
						}
					}
				});
			}
		}
		return v;
	}
	
	
	private View createEffectItemView(ICommonDataItem item){
		final LauncherEditEffectItemInfo info = (LauncherEditEffectItemInfo) item;
		View v = layoutInflater.inflate(R.layout.launcher_edit_item_boxed,this, false);
		v.setTag(item);
		final ImageView iv = (ImageView) v.findViewById(R.id.item_image_view);
		TextView tv = (TextView) v.findViewById(R.id.item_text_view);
		ImageView selected = (ImageView) v.findViewById(R.id.item_image_selected);
		selected.setVisibility(View.INVISIBLE);
		tv.setText(info.title);
		tv.setVisibility(View.VISIBLE);
		iv.setImageDrawable(info.icon);
		if (info.isSlideEffect()) {
			if (SettingsPreference.getInstance().getScreenScrollEffects() == info.type) {
				//setLastUsedEffectPosition(position);
				selected.setVisibility(View.VISIBLE);
			} else {
				selected.setVisibility(View.INVISIBLE);
			}
		}
		else if (info.isParticleEffect()) {
			if (!info.isDefaultTheme()) {
				if (SettingsPreference.getInstance().getParticleEffectsThemeId().equals(info.getThemeId())) {
					//setLastUsedEffectPosition(position);
					selected.setVisibility(View.VISIBLE);
				} else {
					selected.setVisibility(View.INVISIBLE);
				}
				//刷新主题指尖特效图片
				/*ThreadUtil.executeMore(new Runnable() {
					@Override
					public void run() {
						try {
							PandaTheme theme = new PandaTheme(mContext, info.getThemeId());
							final Drawable effectIcon = theme.getIconOrDrawableByKey(ThemeData.FINGER_EFFECT_ICON);
							if(null != effectIcon) {
								wallpaperCache.put(theme.getThemeId()+"_effect_icon", effectIcon);
								handler.post(new Runnable() {
									@Override
									public void run() {
										iv.setImageDrawable(effectIcon);
									}
								});
							}
						} catch (Exception e) {
							e.printStackTrace();
						} catch (OutOfMemoryError error) {
							error.printStackTrace();
						}
					}
				});*/
			} else {//桌面自带指尖特效 caizp 2014-02-27
				if (SettingsPreference.getInstance().getParticleEffects() == info.type) {
					//setLastUsedEffectPosition(position);
					selected.setVisibility(View.VISIBLE);
				} else {
					selected.setVisibility(View.INVISIBLE);
				}
			}
		}
		return v;
	}
}
