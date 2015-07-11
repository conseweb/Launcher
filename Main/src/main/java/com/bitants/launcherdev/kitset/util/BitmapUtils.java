package com.bitants.launcherdev.kitset.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.bitants.common.kitset.util.BaseBitmapUtils;
import com.bitants.common.kitset.util.FileUtil;
import com.bitants.common.utils.ALog;
//import com.bitants.launcherdev.framework.httplib.HttpCommon;
//import com.google.android.mms.ContentType;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片处理相关内容
 */
public class BitmapUtils extends BaseBitmapUtils {
//	/**
//	 * 保存在线图片
//	 * @param urlString
//	 * @param filePath
//	 * @return 成功返回保存的路径，失败返回null
//	 */
//	public static String saveInternateImage(String urlString, String filePath) {
//
//		InputStream is = null;
//		Bitmap bmp = null;
//		try {
//			// HttpResponse response =
//			// WebUtil.getSimpleHttpGetResponse(urlString, null);
//			// if (response == null)
//			// return null;
//			HttpCommon httpCommon = new HttpCommon(urlString);
//			HttpEntity entity = httpCommon.getResponseAsEntityGet(null);
//			if (entity == null)
//				return null;
//			Header resHeader = entity.getContentType();
//			String contentType = resHeader.getValue();
//			CompressFormat format = null;
////			if (contentType != null && contentType.equals(ContentType.IMAGE_PNG)) {
//			if (contentType != null && contentType.equals("image/png")) {
//				format = Bitmap.CompressFormat.PNG;
//			} else {
//				format = Bitmap.CompressFormat.JPEG;
//			}
//			is = entity.getContent();
//			bmp = BitmapFactory.decodeStream(is);
//			if (saveBitmap2file(bmp, filePath, format))
//				return filePath;
//			else
//				// 失败，删除文件
//				FileUtil.delFile(filePath);
//		} catch (Exception e) {
//			Log.d(TAG, "function saveInternateImage expose exception:" + e.toString());
//			// 失败，删除文件
//			FileUtil.delFile(filePath);
//		} finally {
//
//			try {
//				if (is != null)
//					is.close();
//				if (bmp != null)
//					bmp.recycle();
//			} catch (IOException e) {
//			}
//		}
//
//		return null;
//	}
	
	public static byte[] compressToBytes(Bitmap bitmap, CompressFormat format, int quality) {
		if (bitmap == null || bitmap.isRecycled()) {
			ALog.w("bad bitmap: " + bitmap);
			return null;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream(65536);
		bitmap.compress(format, quality, baos);
		return baos.toByteArray();
	}
}
