package com.bitants.launcherdev.framework.httplib;

import android.content.Context;
import com.bitants.launcherdev.kitset.Analytics.HiAnalytics;
import com.bitants.launcherdev.kitset.util.DocumentHelper;
import com.bitants.launcherdev.kitset.util.StringUtil;
import com.bitants.launcherdev.kitset.util.TelephoneUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 服务端接口交互类 </br>
 * Created on: 2014-2-12 </br>
 * Author: pdw </br>
 */
public class DxHttpClient {
	//全局统计开关接口
	private static final String CONFIG_SWITCH_URL = "http://logstatic.sj.91.com/config.xml";
	

	// 测试"192.168.254.69:803";
	// 生产“pandahome.sj.91.com”
	
	//实时统计接口
	private static final String REALTIME_STATE_URL = "http://pandahome.sj.91.com/theme.ashx/SuppleFunc?Mt=4&Format=json&PID=106";
	
	private static final String TAG_REALTIME_FUN = "PostRealFunc";
	
	/**
	 * 获取是否可以提交实时统计开关 </br>
	 * Create On 2014-2-12下午03:13:31 </br>
	 * Author : pdw </br>
	 * @param ctx
	 * @return 获取状态
	 */
	public static SwitchState getRealTimeStateSwitch(Context ctx) {
		if (!TelephoneUtil.isNetworkAvailable(ctx)) {//网络不可用
			return SwitchState.NETWORK_ERROR;
		}
		try {
			HttpCommon httpCom = new HttpCommon(CONFIG_SWITCH_URL);
			Document doc = httpCom.getDocument();
			if (doc == null)
				return SwitchState.NETWORK_ERROR;
			String result = DocumentHelper.getValByTagName(doc, TAG_REALTIME_FUN);
			if (StringUtil.isEmpty(result))
				return SwitchState.PARSE_ERROR;
			boolean re =  "1".equals(result);
			if (re) {
				return SwitchState.SWITCH_ON;
			} else {
				return SwitchState.SWITCH_OFF;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return SwitchState.NETWORK_ERROR;
	}
	
	/**
	 * 实时活跃打点 </br>
	 * Create On 2014-2-12下午03:54:32 </br>
	 * Author : pdw </br>
	 * @param ctx
	 * @param label
	 * @param encode
	 * void
	 */
	public static final boolean stateActivityRealTime(Context ctx, int eventId, String label, boolean encode) {
		if (!TelephoneUtil.isNetworkAvailable(ctx)) 
			return false;
		String url = getRealTimeStateUrl(ctx, eventId, label, encode);
		HttpCommon httpCom = new HttpCommon(url);
		boolean result = httpCom.httpFeedback();
		return result;
	}
	
	private static final String getRealTimeStateUrl(Context ctx, int eventId, String label, boolean encode) {
		StringBuffer sb = new StringBuffer(REALTIME_STATE_URL);
		HttpRemoteRequest.appendAttrValue(sb, "DivideVersion", TelephoneUtil.getVersionName(ctx, ctx.getPackageName()));
		HttpRemoteRequest.appendAttrValue(sb, "SupPhone", encodeAttrValue(TelephoneUtil.getMachineName())); //型号
		HttpRemoteRequest.appendAttrValue(sb, "SupFirm", TelephoneUtil.getFirmWareVersion());	//Android版本号 
		HttpRemoteRequest.appendAttrValue(sb, "NetWork", "1");
		HttpRemoteRequest.appendAttrValue(sb, "JailBroken", "0");
		HttpRemoteRequest.appendAttrValue(sb, "Fid", String.valueOf(0 - eventId));
		HttpRemoteRequest.appendAttrValue(sb, "IMEI", TelephoneUtil.getIMEI(ctx));
		String cuid = HiAnalytics.getCUID(ctx);
		cuid = StringUtil.isEmpty(cuid) ? "" : URLEncoder.encode(cuid);
		HttpRemoteRequest.appendAttrValue(sb, "cuid", cuid);
		if (encode) {
			HttpRemoteRequest.appendAttrValue(sb, "lbl", encodeAttrValue(label));
		} else {
			HttpRemoteRequest.appendAttrValue(sb, "lbl", label);
		}
		return sb.toString();
	}
	
	public static String encodeAttrValue(String value){
		String returnValue = "";
		try {
			value = URLEncoder.encode(value+"", "UTF-8");
			returnValue = value.replaceAll("\\+", "%20");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return returnValue;
	}
	
	/**
	 * 统计开关
	 * @author Administrator
	 *
	 */
	public enum SwitchState {
		
		NETWORK_ERROR("network error",0), //网络错误
		PARSE_ERROR("parse error",1), //解析错误
		SWITCH_NONE("switch none",2), //尚未获取
		SWITCH_ON("switch on",3), //状态开
		SWITCH_OFF("switch off",4), //状态关
		SWITCH_DONE("switch done",5); //统计时长结束
		
		//状态值
		private int mState;
		
		//描述
		private String desc;

		private SwitchState(String desc, int mState) {
			this.desc = desc;
			this.mState = mState;
		}
		
		@Override
		public String toString() {
			return String.valueOf (desc+";state code->;"+this.mState);
		}
		
		public int getState() {
			return mState;
		}
	}
	
}

