package com.bitants.common.push;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.bitants.common.kitset.util.AndroidPackageUtils;
import com.bitants.common.kitset.util.StringUtil;
import com.bitants.common.launcher.config.BaseConfig;
import com.bitants.common.launcher.config.db.ConfigDataBase;
import com.bitants.common.push.model.CompaigPushInfo;
import com.bitants.common.push.model.NotifyBigPicPushInfo;
import com.bitants.common.push.model.NotifyPushInfo;
import com.bitants.common.push.model.PushMsg;
import com.bitants.common.push.model.NotifyIconPushInfo;
import com.bitants.common.push.model.PopupPushInfo;
import com.bitants.common.push.model.ServerConfigInfo;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by michael on 2015-04-16.
 */
public class PushMsgHandler {
    public static final String CREATE_PUSH_DB = "CREATE TABLE IF NOT EXISTS \'launcher_push\' (\'_id\' INTEGER PRIMARY KEY,\'time\' INTEGER,\'isRead\' INTEGER NOT NULL,\'type\' VARCHAR(32) NOT NULL,\'value\' VARCHAR(1024) NOT NULL)";

    public PushMsgHandler() {
    }

    public static ServerConfigInfo fetchPushMsg() {
        try {
            String e = PushManager.getInstance().getPushSDKAdapter().getPushMsg();
            return parseMsgJson(e);
        } catch (Exception var1) {
            var1.printStackTrace();
            return null;
        }
    }

    public static ServerConfigInfo parseMsgJson(String json) {
        try {
            if(json != null && !TextUtils.isEmpty(json)) {
                JSONObject e = new JSONObject(json);
                ServerConfigInfo serverInfo = new ServerConfigInfo();
                if(e.getJSONObject("Result") != null) {
                    JSONObject resultObj = e.getJSONObject("Result");
                    serverInfo.setFetchInterval(resultObj.getInt("refreshTime"));
                    serverInfo.setShowHotIcon(resultObj.getInt("isShow"));
                    if(resultObj.has("PushMsg")) {
                        JSONObject msgObj = resultObj.getJSONObject("PushMsg");
                        serverInfo.setCompaigPushInfo(PushManager.getInstance().getPushSDKAdapter().parseCompaignNotification(msgObj.getJSONArray("items")));
                        serverInfo.setNotifyPushInfo(parseNotifyItemMsg(msgObj.getJSONArray("notifyItems")));
                        serverInfo.setPopupPushInfo(parseNotifyPopupMsg(msgObj.getJSONArray("popupItems")));
                        serverInfo.setNotifyIconPushInfo(parseNotifyIconMsg(msgObj.getJSONArray("iconItems")));
                    }
                }

                return serverInfo;
            } else {
                return null;
            }
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    private static List<NotifyPushInfo> parseNotifyItemMsg(JSONArray notifyArray) throws Exception {
        if(notifyArray != null && notifyArray.length() != 0) {
            ArrayList notifyList = new ArrayList();

            for(int i = 0; i < notifyArray.length(); ++i) {
                int notifyId = PushManager.getInstance().getPushSDKAdapter().getLastCommonPushedId();
                JSONObject object = (JSONObject)notifyArray.get(i);
                int id = object.optInt("id");
                if(id > notifyId) {
                    String item = object.optString("content");
                    if(!StringUtil.isAnyEmpty(new String[]{item})) {
                        NotifyPushInfo notify = parseNotifyPushInfo(item);
                        notify.setId(id);
                        if(!notify.isTarget()) {
                            continue;
                        }

                        saveToDB(id, item, "notify");
                        notifyList.add(notify);
                    }

                    PushManager.getInstance().getPushSDKAdapter().setLastCommonPushedId(id);
                }
            }

            return notifyList;
        } else {
            return null;
        }
    }

    private static List<PopupPushInfo> parseNotifyPopupMsg(JSONArray popupArray) throws Exception {
        if(popupArray != null && popupArray.length() != 0) {
            ArrayList popupList = new ArrayList();

            for(int i = 0; i < popupArray.length(); ++i) {
                int popupId = PushManager.getInstance().getPushSDKAdapter().getLastPopupPushedId();
                JSONObject object = (JSONObject)popupArray.get(i);
                int id = object.optInt("id");
                if(id > popupId) {
                    String item = object.optString("content");
                    saveToDB(id, item, "popup");
                    PopupPushInfo info = parsePopupPushInfo(item);
                    info.setId(id);
                    popupList.add(info);
                    PushManager.getInstance().getPushSDKAdapter().setLastPopupPushedId(id);
                }
            }

            return popupList;
        } else {
            return null;
        }
    }

    private static List<NotifyIconPushInfo> parseNotifyIconMsg(JSONArray iconArray) throws Exception {
        if(iconArray != null && iconArray.length() != 0) {
            ArrayList iconList = new ArrayList();

            for(int i = 0; i < iconArray.length(); ++i) {
                int notifyIconId = PushManager.getInstance().getPushSDKAdapter().getLastIconPushedId();
                JSONObject object = (JSONObject)iconArray.get(i);
                int id = object.optInt("id");
                if(id > notifyIconId) {
                    String item = object.optString("content");
                    if(!StringUtil.isAnyEmpty(new String[]{item})) {
                        NotifyIconPushInfo notifyIcon = parseNotifyPushIconInfo(item);
                        notifyIcon.setId(id);
                        if(notifyIcon.isTarget()) {
                            iconList.add(notifyIcon);
                            saveToDB(id, item, "notify_icon");
                        }
                    }

                    PushManager.getInstance().getPushSDKAdapter().setLastIconPushedId(id);
                }
            }

            return iconList;
        } else {
            return null;
        }
    }

    public static void createPushMsgTable() {
        ConfigDataBase db = null;

        try {
            db = new ConfigDataBase(BaseConfig.getApplicationContext());
            db.execSQL("CREATE TABLE IF NOT EXISTS \'launcher_push\' (\'_id\' INTEGER PRIMARY KEY,\'time\' INTEGER,\'isRead\' INTEGER NOT NULL,\'type\' VARCHAR(32) NOT NULL,\'value\' VARCHAR(1024) NOT NULL)");
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            if(db != null) {
                db.close();
            }

        }

    }

    public static long saveToDB(int id, String str, String type) {
        ConfigDataBase db = null;

        try {
            db = new ConfigDataBase(BaseConfig.getApplicationContext());
            ContentValues e = new ContentValues();
            e.put("_id", Integer.valueOf(id));
            e.put("time", Long.valueOf(System.currentTimeMillis()));
            e.put("isRead", Integer.valueOf(0));
            e.put("type", type);
            e.put("value", str);
            long var6 = db.insertOrThrow("launcher_push", (String)null, e);
            return var6;
        } catch (Exception var10) {
            var10.printStackTrace();
        } finally {
            if(db != null) {
                db.close();
            }

        }

        return -1L;
    }

    public static void deleteMsg(long id) {
        ConfigDataBase db = null;

        try {
            db = new ConfigDataBase(BaseConfig.getApplicationContext());
            db.execSQL("delete from launcher_push where _id = " + id);
        } catch (Exception var7) {
            var7.printStackTrace();
        } finally {
            if(db != null) {
                db.close();
            }

        }

    }

    public static List<PushMsg> getAllMsg() {
        ArrayList list = new ArrayList();
        ConfigDataBase db = null;
        Cursor c = null;

        try {
            db = new ConfigDataBase(BaseConfig.getApplicationContext());
            c = db.query("select * from launcher_push");
            if(c != null) {
                int e = c.getColumnIndexOrThrow("_id");
                int isReadIndex = c.getColumnIndexOrThrow("isRead");
                int typeIndex = c.getColumnIndexOrThrow("type");
                int valueIndex = c.getColumnIndexOrThrow("value");
                int timeIndex = c.getColumnIndexOrThrow("time");

                while(c.moveToNext()) {
                    PushMsg msg = new PushMsg();
                    msg.setId(c.getLong(e));
                    msg.setIsRead(c.getInt(isReadIndex) == 1);
                    msg.setType(c.getString(typeIndex));
                    msg.setValue(c.getString(valueIndex));
                    msg.setTime(c.getLong(timeIndex));
                    list.add(msg);
                }
            }
        } catch (Exception var12) {
            var12.printStackTrace();
        } finally {
            if(c != null) {
                c.close();
            }

            if(db != null) {
                db.close();
            }

        }

        return list;
    }

    public static List<PopupPushInfo> getNotClickedPopupBubbles() {
        ArrayList list = null;
        ConfigDataBase db = null;
        Cursor c = null;

        try {
            db = new ConfigDataBase(BaseConfig.getApplicationContext());
            String e = "com.nd.android.pandahome2".equals(BaseConfig.getApplicationContext().getPackageName())?"1054":"-1";
            c = db.query("select * from launcher_push where _id > " + e + " and isRead = 0 and type = \'" + "popup" + "\'");
            if(c != null) {
                list = new ArrayList();
                int idIndex = c.getColumnIndexOrThrow("_id");
                int valueIndex = c.getColumnIndexOrThrow("value");

                while(c.moveToNext()) {
                    PopupPushInfo info = parsePopupPushInfo(c.getString(valueIndex));
                    info.setId(c.getInt(idIndex));
                    list.add(info);
                }
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        } finally {
            if(c != null) {
                c.close();
            }

            if(db != null) {
                db.close();
            }

        }

        return list;
    }

    public static void setPushMsgRead(long id) {
        ConfigDataBase db = null;

        try {
            db = new ConfigDataBase(BaseConfig.getApplicationContext());
            db.execSQL("update launcher_push set isRead = 1 where _id = " + id);
        } catch (Exception var7) {
            var7.printStackTrace();
        } finally {
            if(db != null) {
                db.close();
            }

        }

    }

    public static List<NotifyPushInfo> getNotReadMsgForMenu(int size) {
        ArrayList list = null;
        ConfigDataBase db = null;
        Cursor c = null;

        try {
            db = new ConfigDataBase(BaseConfig.getApplicationContext());
            c = db.query("select * from launcher_push where isRead = 0 and type =\'notify\' and value like \'%pos%4%\' order by _id desc ");
            if(c != null) {
                list = new ArrayList();
                int e = c.getColumnIndexOrThrow("_id");
                int valueIndex = c.getColumnIndexOrThrow("value");
                size = size > 0?size:2147483647;
                int count = 0;

                while(c.moveToNext()) {
                    NotifyPushInfo info = parseNotifyPushInfo(c.getString(valueIndex));
                    if(!StringUtil.isEmpty(info.getPos()) && info.getPos().equals("4")) {
                        info.setId(c.getInt(e));
                        info.setNotifyIconPath(PushUtil.getNotifyIconPath(PushManager.getInstance().getPushSDKAdapter(), info));
                        list.add(info);
                        ++count;
                        if(count >= size) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        } finally {
            if(c != null) {
                c.close();
            }

            if(db != null) {
                db.close();
            }

        }

        return list;
    }

    public static List<NotifyIconPushInfo> getNotReadPushAppendIcon() {
        ArrayList list = null;
        ConfigDataBase db = null;
        Cursor c = null;

        try {
            db = new ConfigDataBase(BaseConfig.getApplicationContext());
            c = db.query("select * from launcher_push where isRead = 0 and type =\'notify_icon\' and value like \'%append_icon%\'");
            if(c != null) {
                list = new ArrayList();
                int e = c.getColumnIndexOrThrow("_id");
                int valueIndex = c.getColumnIndexOrThrow("value");

                while(c.moveToNext()) {
                    NotifyIconPushInfo info = parseNotifyPushIconInfo(c.getString(valueIndex));
                    if(!StringUtil.isAnyEmpty(new String[]{info.getAppendIcon()})) {
                        info.setId(c.getInt(e));
                        info.setIconPath(PushUtil.getPushIconPath(PushManager.getInstance().getPushSDKAdapter(), info));
                        list.add(info);
                    }
                }
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        } finally {
            if(c != null) {
                c.close();
            }

            if(db != null) {
                db.close();
            }

        }

        return list;
    }

    public static CompaigPushInfo parseCompaigPushInfo(JSONObject object) {
        CompaigPushInfo compaigInfo = new CompaigPushInfo();
        compaigInfo.setId(object.optString("id"));
        compaigInfo.setTitle(object.optString("title"));
        compaigInfo.setContent(object.optString("content"));
        compaigInfo.setUrl(object.optString("url"));
        compaigInfo.setForpeople(object.optString("forpeople"));
        compaigInfo.setIconUrl(object.optString("iconUrl"));
        return compaigInfo;
    }

    public static NotifyPushInfo parseNotifyPushInfo(String str) throws JSONException {
        if(str.startsWith("{")) {
            return parseNotifyPushInfoJson(new JSONObject(str));
        } else {
            String[] array = str.split("&");
            if(array.length > 10) {
                return parseNotifyBigPicPushInfo(str);
            } else {
                NotifyPushInfo obj = new NotifyPushInfo();
                obj.setTitle(array[0].substring(6));
                obj.setContent(array[1].substring(8));
                obj.setIntent(array[2].substring(7));
                obj.setPersist(!StringUtil.isEmpty(array[3].substring(8)) && array[3].substring(8).equals("1"));
                obj.setTarget(isTarget(array[4].substring(7)));
                obj.setNotifyIcon(array[5].substring(12));
                if(array.length > 6) {
                    obj.setPos(array[6].substring(4));
                }

                return obj;
            }
        }
    }

    private static NotifyBigPicPushInfo parseNotifyBigPicPushInfo(String str) {
        String[] array = str.split("&");
        NotifyBigPicPushInfo obj = new NotifyBigPicPushInfo();
        obj.setTitle(array[0].substring(6));
        obj.setContent(array[1].substring(8));
        obj.setIntent(array[2].substring(7));
        obj.setPersist(!StringUtil.isEmpty(array[3].substring(8)) && array[3].substring(8).equals("1"));
        obj.setBigPicUrl(array[4].substring(4));
        obj.setBtn1_text(array[5].substring(5));
        obj.setBtn1_intent(array[6].substring(11));
        obj.setBtn2_text(array[7].substring(5));
        obj.setBtn2_intent(array[8].substring(11));
        obj.setTarget(isTarget(array[9].substring(7)));
        obj.setNotifyIcon(array[10].substring(12));
        return obj;
    }

    public static NotifyIconPushInfo parseNotifyPushIconInfo(String str) throws JSONException {
        if(str.startsWith("{")) {
            return parseNotifyPushIconInfoJson(new JSONObject(str));
        } else {
            String[] array = str.split("&");
            NotifyIconPushInfo obj = new NotifyIconPushInfo();
            obj.setTitle(array[0].substring(6));
            obj.setContent(array[1].substring(8));
            obj.setIntent(array[2].substring(7));
            obj.setPersist(!StringUtil.isEmpty(array[3].substring(8)) && array[3].substring(8).equals("1"));
            obj.setTarget(isTarget(array[4].substring(7)));
            obj.setIconUrl(array[5].substring(8));
            obj.setIconIntent(array[6].substring(11));
            obj.setIconTitle(array[7].substring(10));
            obj.setShowIconImmediately(!StringUtil.isEmpty(array[8].substring(9)) && array[8].substring(9).equals("1"));
            obj.setNotifyIcon(array[9].substring(12));
            if(array.length > 10) {
                obj.setIconIntentNew(array[10].substring(15));
            }

            if(array.length > 11) {
                obj.setAppendIcon(array[11].substring(12));
            }

            return obj;
        }
    }

    private static boolean isTarget(String pkgName) {
        return StringUtil.isEmpty(pkgName)?true:(pkgName.startsWith("!")?!AndroidPackageUtils.isPkgInstalled(BaseConfig.getApplicationContext(), pkgName.substring(1)):AndroidPackageUtils.isPkgInstalled(BaseConfig.getApplicationContext(), pkgName));
    }

    public static PopupPushInfo parsePopupPushInfo(String str) throws JSONException {
        if(str.startsWith("{")) {
            return parsePopupPushInfoJson(new JSONObject(str));
        } else {
            String[] array = str.split("&");
            PopupPushInfo obj = new PopupPushInfo();
            obj.setTarget(array[0].substring(7));
            obj.setContent(array[1].substring(8));
            obj.setIntent(array[2].substring(7));
            obj.setDuration(array[3].substring(9));
            return obj;
        }
    }

    private static NotifyPushInfo parseNotifyPushInfoJson(JSONObject object) {
        if(object.has("btn1")) {
            return parseNotifyBigPicPushInfoJson(object);
        } else {
            NotifyPushInfo obj = new NotifyPushInfo();
            obj.setTitle(object.optString("title"));
            obj.setContent(object.optString("content"));
            obj.setIntent(object.optString("intent"));
            obj.setPersist(!StringUtil.isEmpty(object.optString("persist")) && object.optString("persist").equals("1"));
            obj.setTarget(isTarget(object.optString("target")));
            obj.setNotifyIcon(object.optString("notify_icon"));
            obj.setPos(object.optString("pos"));
            return obj;
        }
    }

    private static NotifyBigPicPushInfo parseNotifyBigPicPushInfoJson(JSONObject object) {
        NotifyBigPicPushInfo obj = new NotifyBigPicPushInfo();
        obj.setTitle(object.optString("title"));
        obj.setContent(object.optString("content"));
        obj.setIntent(object.optString("intent"));
        obj.setPersist(!StringUtil.isEmpty(object.optString("persist")) && object.optString("persist").equals("1"));
        obj.setBigPicUrl(object.optString("url"));
        obj.setBtn1_text(object.optString("btn1"));
        obj.setBtn1_intent(object.optString("btn1Intent"));
        obj.setBtn2_text(object.optString("btn2"));
        obj.setBtn2_intent(object.optString("btn2Intent"));
        obj.setTarget(isTarget(object.optString("target")));
        obj.setNotifyIcon(object.optString("notify_icon"));
        return obj;
    }

    private static NotifyIconPushInfo parseNotifyPushIconInfoJson(JSONObject object) {
        NotifyIconPushInfo obj = new NotifyIconPushInfo();
        obj.setTitle(object.optString("title"));
        obj.setContent(object.optString("content"));
        obj.setIntent(object.optString("intent"));
        obj.setPersist(!StringUtil.isEmpty(object.optString("persist")) && object.optString("persist").equals("1"));
        obj.setTarget(isTarget(object.optString("target")));
        obj.setIconUrl(object.optString("iconurl"));
        obj.setIconIntent(object.optString("iconintent"));
        obj.setIconTitle(object.optString("icontitle"));
        obj.setShowIconImmediately(!StringUtil.isEmpty(object.optString("showicon")) && object.optString("showicon").equals("1"));
        obj.setNotifyIcon(object.optString("notify_icon"));
        obj.setIconIntentNew(object.optString("iconintent_new"));
        obj.setAppendIcon(object.optString("append_icon"));
        return obj;
    }

    private static PopupPushInfo parsePopupPushInfoJson(JSONObject object) {
        PopupPushInfo obj = new PopupPushInfo();
        obj.setTarget(object.optString("target"));
        obj.setContent(object.optString("content"));
        obj.setIntent(object.optString("intent"));
        obj.setDuration(object.optString("duration"));
        return obj;
    }
}
