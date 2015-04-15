/**
 * @author Michael
 * Date:2014-5-19上午10:22:49
 *
 */
package com.bitants.launcherdev.cache;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.launcher.config.BaseConfig;
import com.bitants.launcherdev.launcher.support.BaseIconCache;
import com.bitants.launcherdev.theme.ThemeManagerFactory;
import com.bitants.launcherdev.kitset.util.BaseBitmapUtils;
import com.bitants.launcherdev.kitset.util.ReflectUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 根据主题名称分为不同的文件夹
 *
 * @author Michael Date:2014-5-19上午10:22:49
 *
 */
public class DiskCacheSchemaB {

    private static DiskCacheSchemaB instance;

    public static final String EXPIRED_THEME_PREFIX = "EXPIRED_PANDAHOME_THEME_";

    private static final String V6_PREFIX = "v6_";

    /**
     * 91快捷更换了图片导致旧版主题无法使用新的图片
     */
    private static final String V611_PREFIX = "v611_";

    /**
     * 缓存文件存在于sd卡上的路径
     */
    public static final String cachePath = BaseConfig.getBaseDir() + "/.cache/icons/";

    public static final String APK_TYPE = "1";

    public static final String OTHER_ICON_TYPE = "2";

    private boolean isCacheDirCreate = false;

    private DiskCacheSchemaB() {
        try {
            File file = new File(cachePath);
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    isCacheDirCreate = parentFile.mkdir();
                    if(!isCacheDirCreate){//创建文件夹失败返回
                        return;
                    }
                }
                isCacheDirCreate = file.mkdir();
            }else{
                isCacheDirCreate = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * author Michael Date:2014-4-25下午1:48:42
     * @return instance
     */
    public static DiskCacheSchemaB getInstance() {
        if (instance == null) {
            instance = new DiskCacheSchemaB();
        }
        return instance;
    }

    /**
     * 从disk缓存中获取图标
     *
     * author Michael Date:2014-4-25下午1:58:30
     * @param iconType 图标类型
     * @param key key
     * @param themeId 主题ID
     * @param info ResolveInfo
     * @param iconCache iconCache 不能为空
     * @param reuseBitmap 重复使用图标
     * @return bitmap
     */
    public Bitmap getBitmapFromDiskCache(String iconType, String key, String themeId, ResolveInfo info,
                                         BaseIconCache iconCache,  Bitmap reuseBitmap) {
        if (iconType.equals(OTHER_ICON_TYPE)) {
            return getBitmapFromDiskCacheByOtherIconType(key, themeId, iconCache, reuseBitmap);
        } else if (iconType.equals(APK_TYPE)) {// apk类型图片
            return getBitmapFromDiskCacheByApkIconType(key, themeId, info, iconCache,  reuseBitmap);
        }
        return iconCache.getmDefaultIcon();
    }


    /**
     * 从diskcache中获取图标: 91快捷、我的手机、推荐等图标类型
     * @param key key
     * @param themeId themeId
     * @param iconCache iconCache 不能为空
     * @param reuseBitmap reuseBitmap
     * @return bitmap
     */
    public Bitmap getBitmapFromDiskCacheByOtherIconType(String key, String themeId,BaseIconCache iconCache,
                                                        Bitmap reuseBitmap) {
        try {
            String originalKey = key;
            key = V611_PREFIX + key;
            Bitmap bitmap = getBitmapFromSdcard(themeId, key, reuseBitmap);
            if(bitmap != null) {
                return bitmap;
            }

            // 需要重新生成图片 并将信息存入缓存Map中
            Drawable drawable = ThemeManagerFactory.getInstance().getCurrentTheme().getWrapper().
                    getDefaultResource(originalKey);
            if(drawable == null) {
                return iconCache.getmDefaultIcon();
            }

            bitmap = BaseBitmapUtils.drawable2Bitmap(drawable);
            if(bitmap == null) {
                return iconCache.getmDefaultIcon();
            }

            if(!BaseIconCache.DEFAULT_THEME_NAME_SMALL.equals(themeId)){//如果是小图标不进行切割蒙板的处理
                bitmap = BaseBitmapUtils.createIconBitmapFor91Icon(bitmap, BaseConfig.getApplicationContext());
            }

            if(bitmap != null) {
                if(isCacheDirCreate) {//将生成的图片写入SD卡
                    encodeBitmapToSdCard(bitmap, key, themeId);
                }
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return iconCache.getmDefaultIcon();
    }


    /**
     * 从diskcache中获取系统快捷的图标
     * @param key key
     * @param themeId 主题ID
     * @param iconCache iconCache 不能为空
     * @param originalBitmap 系统快捷的原始图标
     * @return bitmap
     */
    public Bitmap getBitmapFromDiskCacheBySystemShortCutIconType(String key, String themeId,
                                                                  BaseIconCache iconCache, Bitmap originalBitmap) {
        try {
            Bitmap bitmap = getBitmapFromSdcard(themeId, key, null);
            if(bitmap != null) {
                return bitmap;
            }

            if (originalBitmap == null) {
                return iconCache.getmDefaultIcon();
            }

            // 需要重新生成图片 并将信息存入缓存Map中
            if(!BaseIconCache.DEFAULT_THEME_NAME_SMALL.equals(themeId)){//如果是小图标不进行切割蒙板的处理
                bitmap = BaseBitmapUtils.createIconBitmapFor91Icon(originalBitmap,
                        BaseConfig.getApplicationContext());
            }
            if(bitmap != null) {
                if(isCacheDirCreate) {//将生成的图片写入SD卡
                    encodeBitmapToSdCard(bitmap, key, themeId);
                }
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return iconCache.getmDefaultIcon();
    }


    /**
     * 从diskcache中获取图标: apk图标类型
     * @param key key
     * @param themeId themeID
     * @param info ResolveInfo
     * @param iconCache iconCache
     * @param reuseBitmap 可重复使用的图标
     * @return Bitmap
     */
    public Bitmap getBitmapFromDiskCacheByApkIconType(final String key, final String themeId, final ResolveInfo info,
                                                      final BaseIconCache iconCache, Bitmap reuseBitmap) {
        try {
            Bitmap bitmap = getBitmapFromSdcard(themeId, key, reuseBitmap);
            if(bitmap != null) {
                //检查是否过期
                checkApkCacheValid(themeId, key, info, iconCache.getmPackageManager());
                return bitmap;
            }

            // 需要重新生成图片 并将信息存入缓存Map中
            Drawable drawable = iconCache.loadDrawable(info);
            if(drawable == null) {
                return iconCache.getmDefaultIcon();
            }

            bitmap = BaseBitmapUtils.createIconBitmapThumbnail(drawable, BaseConfig.getApplicationContext(), reuseBitmap);
            if(!BaseIconCache.DEFAULT_THEME_NAME_SMALL.equals(themeId)){//如果是小图标不进行切割蒙板的处理
                bitmap = BaseBitmapUtils.createIconBitmapForApkIcon(bitmap, BaseConfig.getApplicationContext());
            }
            if(bitmap != null) {
                if(isCacheDirCreate) {//将生成的图片写入SD卡
                    encodeBitmapToSdCard(bitmap, key, themeId);
                }
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iconCache.getmDefaultIcon();
    }


    /**
     * 从SD卡获取图片
     * @param themeID 主题ID
     * @param key 图标key
     * @param reuseBitmap 可重用bitmap
     * @return Bitmap
     */
    private Bitmap getBitmapFromSdcard(String themeID, String key, Bitmap reuseBitmap) {
        try {
            if(!isCacheDirCreate) {
                return null;
            }
            String cacheDirPath = getThemeIconCacheDir(themeID);
            File cacheDir = new File(cacheDirPath);

            if(!cacheDir.exists()){
                cacheDir.mkdir();
                return null;
            }

            String cacheFilePath = cacheDirPath + key;
            File cacheFile = new File(cacheFilePath);
            if(!cacheFile.exists() || cacheFile.length() < 1) {
                return null;
            }
            Bitmap bitmap = null;
            if(Build.VERSION.SDK_INT > 10 && reuseBitmap != null){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                ReflectUtil.setFieldValueByFieldName(options, "inBitmap", reuseBitmap);
                try{
                    bitmap = BitmapFactory.decodeFile(cacheFilePath, options);
                    Log.e("DiskCacheSchemaB", "重新使用圖標成功");
                }catch(Exception e) {
                    Log.e("DiskCacheSchemaB", "重新使用圖標失敗");
                    e.printStackTrace();
                    bitmap = null;
                }
            }
            if(bitmap == null) {
                bitmap = BitmapFactory.decodeFile(cacheFilePath);
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检测APK图标是否过期
     * @param themeID 主题ID
     * @param key key
     * @param resolveInfo resolveInfo
     * @param packageManager packageManager
     */
    private void checkApkCacheValid(String themeID, String key, final ResolveInfo resolveInfo,
                                    final PackageManager packageManager) {
        // 检测下apk的图标是否改变过 如果已改变删除
        String cacheDirPath = getThemeIconCacheDir(themeID);
        File cacheDir = new File(cacheDirPath);

        if(!cacheDir.exists()){
            return;
        }
        final long dirTime = cacheDir.lastModified();

        String cacheFilePath = cacheDirPath + key;
        final File cacheFile = new File(cacheFilePath);
        if(!cacheFile.exists()){
            return;
        }

        DiskCacheTaskManager.getInstance().postCheckFileValid(key, new Runnable() {
            @Override
            public void run() {
                try {
                    long apkInstallTime = DiskCache.getApkInstallTime(resolveInfo, packageManager);
                    if (apkInstallTime > dirTime) {
                        cacheFile.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 将bitmap保存到sd卡
     * @param bitmap bitmap
     * @param key key
     * @param themeId themeId
     */
    private void encodeBitmapToSdCard(final Bitmap bitmap, String key, String themeId) {
        if(bitmap == null || StringUtil.isEmpty(key) || StringUtil.isEmpty(themeId)) {
            return;
        }
        final String cacheFilePath = getThemeIconCacheDir(themeId) + key;
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                FileOutputStream fileOutputStream = null;
                File file = null;
                try {
                    file = new File(cacheFilePath);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    if(file != null && file.exists()){
                        file.delete();
                    }
                } finally {
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }

        };

        DiskCacheTaskManager.getInstance().postTask(key, runnable);
    }



    /**
     * 根据主题名称删除cache 耗时操作，不要在UI线程中使用
     *
     * author Michael Date:2014-5-22下午2:31:26
     * @param themeId 主题ID
     * @return 是否删除成功
     */
    public static boolean deleteCacheByThemeId(String themeId) {
        try {
            File file = new File(cachePath + themeId);
            if (file.exists()) {
                for (File subFile : file.listFiles()) {
                    subFile.delete();
                }
            }
            return file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getThemeIconCacheDir(String themeId) {
        return cachePath + themeId + "/";
    }

}
