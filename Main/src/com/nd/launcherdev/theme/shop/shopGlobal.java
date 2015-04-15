package com.nd.launcherdev.theme.shop;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.nd.launcherdev.kitset.util.FileUtil;
import com.nd.launcherdev.kitset.util.FileUtil;


/**
 * 全局类
 *
 */
public class shopGlobal {
	public static final String TAG = "com.nd.android.hilauncherExShopV3";
	
    /**INTENT_THEME_LIST*/
    public final static String INTENT_THEME_LIST = "nd.panda.theme.list";
    
	/**设置*/
    public final static String APPLICATION = "application";
	
    /**BASE_DIR*/
    public final static String BASE_DIR = Environment.getExternalStorageDirectory() + "/Dianxinos";
    
    /**
     * 防止图库扫描该目录图片
     */
    public final static String BASE_DIR_AVOID_MEDIA_SCAN = BASE_DIR + "/.nomedia";
    
    /**THEME_HOME*/
    public final static String PACKAPGES_HOME = BASE_DIR + "/Packages/";
    
    /**THEME_HOME*/
    public final static String CACHES_HOME = BASE_DIR + "/caches/";

    /**百宝箱服务器的图片缓存*/
    public final static String CACHES_HOME_MARKET = CACHES_HOME + "91space/";
   
    public final static String CACHES_HOME_MARKET_AVOID_MEDIA_SCAN = CACHES_HOME_MARKET + "/.nomedia";
        
    /**客户端Loading图和分类图片缓存*/
    public final static String CACHES_CATEGORY_PIC = CACHES_HOME + "themeCategory/";
    
	/**
	 * 短信支付获取NDAction动作值的标识
	 */
	public static final String LITTLE_PAY_NDACTION = "ndaction";
	
	/**程序第一次启动加载时给与赋值*/
    private static Context baseContext;
      
	/**91豆图标*/
	//public static Drawable priceDrawable;
    
	/**
	 * 初始化91豆图标
	 * @param ctx
	 * @param resID 图标资源ID
	 */
//	public static void initPriceDrawable(Context ctx, int resID){
//		
//		if ( priceDrawable==null ) {
//			try{
//				priceDrawable= ctx.getResources().getDrawable(resID);
//				priceDrawable.setBounds(0, 0, priceDrawable.getMinimumWidth(), priceDrawable.getMinimumHeight());
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	/**
	 * 设置价格TextView的图标
	 * @param priceTextView
	 * @param isShow
	 */
	public static void setPriceTextViewDrawable(TextView priceTextView, boolean isShow){
		
		if (priceTextView!=null){
//			if (isShow){
//				priceTextView.setCompoundDrawables(null,null,Global.priceDrawable,null);
//			}else{
				priceTextView.setCompoundDrawables(null,null,null,null);
//			}
		}
	}
	
	/**
	 * 根据服务端返回的主题价格判断是否免费
	 * @param themePrice
	 * @return
	 */
	public static boolean isFreeForPrice(String themePrice){
		if (null == themePrice || themePrice.trim().equals("") || "0".equals(themePrice) ) {
			return true;
		}
		return false;
	}
	
	/**
     * 获取全局Context
     * @return Context
     */
    public static Context getContext() {
        return baseContext;
    }
    
    /**
     * 设置Context
     * @param context Context
     */
    public static void setContext(Context context) {
        baseContext = context;
    }
    
    /**
     * 创建系统常用目录
     */
	public static void createDefaultDir() {
		final String baseDir = shopGlobal.BASE_DIR;
		File dir = new File(baseDir);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}
		dir = new File(shopGlobal.BASE_DIR_AVOID_MEDIA_SCAN);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}
		dir = new File(shopGlobal.PACKAPGES_HOME);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}
		dir = new File(shopGlobal.CACHES_HOME);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}
		
		dir = new File(shopGlobal.CACHES_HOME_MARKET);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}
		
		//创建文件
		dir = new File(shopGlobal.CACHES_HOME_MARKET_AVOID_MEDIA_SCAN);
		if (!dir.exists()){
			try {
				dir.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		dir = new File(shopGlobal.CACHES_CATEGORY_PIC);
		if (!dir.isDirectory()) {
			dir.mkdir();
		}		

	}
	public static String R(int i){		
		String rs = "";		
		if(baseContext!=null)
			rs = baseContext.getResources().getString(i);
		return rs;
	}
    
	
	public static boolean isZh(){
		Locale lo;
		if( null == baseContext ) {
			return true;
		} else {
			lo = baseContext.getResources().getConfiguration().locale;
		}
		if (lo.getLanguage().equals("zh")) 
			return true;
		return false;
	}
	
	
	private static long timeOld = 0;
	public static void dpost(Context ctx, String str){
		if(timeOld==0){
			timeOld = System.currentTimeMillis();
		}else{
			if(System.currentTimeMillis() - timeOld <3000)
				return;
		}
		
    	if(str==null)
    		str = "null point";
    	if(ctx==null)
    		Log.e(TAG, "context is null!!!");
    	Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
    	timeOld = System.currentTimeMillis();
    }
	
	public static void ddpost(String str){
		if (baseContext!=null){
			Toast.makeText(baseContext, str+"", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static String getURLContent(String surl) {
		InputStream is = null;
		try {
			URL url = new URL(Utf8URLencode(surl));
			HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.setConnectTimeout(SystemConst.CONNECTION_TIMEOUT);
			is = httpUrl.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			String tmp = null;
			while ((tmp = br.readLine()) != null) {
				sb.append(tmp);
			}
			br.close();
			return sb.toString();
		} catch (Exception e) {
			Log.e(TAG, "getURLContent:" + surl);
			//Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static String Utf8URLencode(String text) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if ((c >= 0) && (c <= 255) && (c!=32)) {
				result.append(c);
			} else {
				byte[] b = new byte[0];
				try {
					b = Character.toString(c).getBytes("UTF-8");
				} catch (Exception ex) {
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					result.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return result.toString();
	}
	
	/**
	 * 处理xml的时候需要加上"utf-8"
	 * @param surl
	 * @param encode
	 * @return
	 */
	public static String getURLContent(String surl, String encode) {		
		InputStream is = null;
		try {
			Log.d(TAG, "get url="+ Utf8URLencode(surl) );
			URL url = new URL( Utf8URLencode(surl));
			HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.setConnectTimeout(SystemConst.CONNECTION_TIMEOUT);
			is = httpUrl.getInputStream();			
			BufferedReader br = new BufferedReader(new InputStreamReader(is,encode));
			StringBuffer sb = new StringBuffer();
			String tmp = null;
			while ((tmp = br.readLine()) != null) {
				sb.append(tmp);
			}
			br.close();
			return sb.toString();
		} catch (Exception e) {
			Log.e(TAG, "getURLContent:" + surl);
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static boolean downloadImageByURL(String imgurl, String localPicPath) {
		Log.d(TAG, "downloadImageByurl = " + imgurl);

		File f = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			f = new File(localPicPath);
			if (!f.exists()) {
				URL url = new URL(imgurl);
				URLConnection con = url.openConnection();
				con.setConnectTimeout(8 * 1000);
				con.setRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6");
				is = con.getInputStream();
				if (con.getContentEncoding() != null
						&& con.getContentEncoding().equalsIgnoreCase("gzip")) {
					is = new GZIPInputStream(con.getInputStream());
				}
				byte[] bs = new byte[1024];
				int len = -1;
				os = new FileOutputStream(f);
				while ((len = is.read(bs)) != -1) {
					os.write(bs, 0, len);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (f != null && f.exists()) {
				FileUtil.delFile(f.getAbsolutePath());
			}
			return false;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public static String url2path (String url, String rootpath){
		if (url==null)
			url = "";
		String rs = rootpath;
		String picname = getPicNameFromUrlWithSuff(url);
		rs = rs+picname;
		rs = SUtil.renameRes(rs);
		return rs;
	}	
	
	/**
	 * 从图片url中获得图片名
	 * @param url
	 * @return
	 */
	public static String getPicNameFromUrlWithSuff(String url){
		if ( url==null || "".equals(url) )
			return "";
		String str = url;
		String [] s = str.split("\\/");
		//当s.length为0时，会出现java.lang.StringIndexOutOfBoundsException
		if (s.length > 0) {
			str = s[s.length - 1];
		} else {
			str = "";
		}
		return str; 
	}
	
	public static int paseInt(String s){
		int rs = -1;
		try{
			rs = Integer.parseInt(s);
		}catch(Exception e){
			Log.e(TAG, "pase int error");
		}		
		return rs;
	}
	
	public static int paseInt(Object o){
		return paseInt(o.toString());
	}
	
	public static boolean isEmpty(Object o){
		if(o==null||o.toString().equals(""))
			return true;
		return false;
	}
	
	/**
	 * 隐藏软键盘(已经 有实例化Global)
	 * add by zjf 2010-09-15
	 */
	public static  void hideKeyboard( View view) {		
		hideKeyboard(shopGlobal.getContext(), view);
	}
	
	/**
	 * 	隐藏软键盘
	 * add by zxb 2010-03-15
	 * @param ctx
	 * @param view
	 */
	public static void hideKeyboard(Context ctx, View view){
		if( null==view || ctx==null ) return;
		InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow( view.getWindowToken(), 0 );
	}
	
	public static boolean isLowScreen() {
		if (baseContext==null) {
			return false;
		}
		WindowManager wm = (WindowManager)baseContext.getSystemService(Context.WINDOW_SERVICE);
	    int w = wm.getDefaultDisplay().getWidth();
	    if(w <= 320 )
	    	return true;
	    else
	    	return false;
	}
	
	public static Drawable getDrawableFromPath(String path){
		
		Drawable cachedImage = null;
		
		File pic = new File(path);
 		if (pic.exists()) {
 			try {
 				cachedImage = Drawable.createFromPath(path);
	 			//文件存在但是读取出来为空的情况
	 			if (cachedImage==null) {
	 				Log.e("Global.getDrawableFromPath", "图片文件被损坏 null");
	 				//删除图片,等待下次重新下载
	 				FileUtil.delFile(path);
	 			}
	 		} catch (OutOfMemoryError e) {
				Log.e("Global.getDrawableFromPath", "Out of memory",e);
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}
 		}
 		
 		return cachedImage;
	}
	
	/**
	 * 初始化主题商城
	 * @param appContext
	 * @param activityContext
	 */
	public static void initThemeShopApp(Context appContext, Context activityContext){
		if (shopGlobal.getContext()==null){
			shopGlobal.setContext(appContext);	
			shopGlobal.createDefaultDir();
			//Global.initPriceDrawable(appContext,R.drawable.theme_shop_v2_theme_price_91);
		}
		initDBPath(shopGlobal.getContext());
		
		//NdComPlatformShopTool.initNdComPlatform(activityContext);
	}
	
	/**
	 * 初始化主题商城
	 * @param appContext
	 * @param activityContext
	 */
	public static void initThemeShopAppFromMyThemeCenter(Context appContext, final Context activityContext){
		if (shopGlobal.getContext()==null){
			shopGlobal.setContext(appContext);	
			shopGlobal.createDefaultDir();
			//Global.initPriceDrawable(appContext,R.drawable.theme_shop_v2_theme_price_91);
		}
		initDBPath(shopGlobal.getContext());
		
//		ThreadUtil.executeMore(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					NdComPlatformShopTool.initNdComPlatform(activityContext);
//				} catch (Exception e) {
//					NdComPlatformShopTool.hasInitNdPlatform = false;
//					e.printStackTrace();
//				}
//			}
//		});
	}
	
	/**
	 * 初始化数据库路径
	 */
	public static void initDBPath(Context ctx){
//		//初始化数据库存放位置
//		if ( !LocalAccessor.initDBPath ) {
//			if (SUtil.isSdPresent() && ctx!=null){
//				
//				LocalAccessor.initDBPath = true;
//				String namespace = ctx.getPackageName();
//				String localDbPath = Environment.getDataDirectory() + "/data/" + namespace + "/databases/shopdata.db";
//				String sdcardDbPath = BASE_DIR+"/themeShareData/shopdata.db";
//				
//				if ( FileUtil.isFileExits(sdcardDbPath) &&
//						!FileUtil.isFileExits(localDbPath) ){
//					
//					//sdcard有文件且/data/data无数据库,并且安装有主题单行本,则使用sdcard数据库
//					if ( ThemeShopV2AppThemeTool.isInstallAPKThemeID(ctx, "com.nd.android.pandathemeshop3.normal")
//							|| ThemeShopV2AppThemeTool.isInstallAPKThemeID(ctx, "com.nd.android.pandathemeshop3.free") ){
//						LocalAccessor.DATABASE_NAME = sdcardDbPath; 
//					}
//				}
//			}
//		}
	}
	
	/**
	 * 缓存清理
	 * @param autoClear 是否为后台自动清理
	 */
	public synchronized static void clearCachePic(boolean autoClear){
		//统计缓存信息
		File cacheFile = new File(CACHES_HOME_MARKET);
		if (cacheFile.exists()) {
			if (cacheFile.isDirectory()) {
				File[] pics=cacheFile.listFiles();
				if (pics!=null) {
					//自动清理时缓存文件小于1500时不进行清理,处理一次处理300个图片
					int maxNum = 1500;
					int nums = pics.length; 
					if ( autoClear && nums<maxNum ){
						return ;
					}
					int i = 1;
					int iDelMax = autoClear?300:800;
					for (File pfile : pics) {
						
						if (".nomedia".equals(pfile.getName())){
							continue;
						}
						if (i>iDelMax) {
							break;
						}
						if (pfile.isFile()) {
							pfile.delete();
						}
						i++;
					}
				}
			}
		}
	}
}