package com.bitants.launcherdev.theme.adaption;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.bitants.launcherdev.theme.data.BaseThemeData;

/**
 * 用于配置主题中图标对应的程序调用信息，包括了 主题中图标的key、包名、类名、action、uri,type, 
 * 是否使用启动窗体类
 * @author 章军飞
 * date 2011-1-13
 */
public final class ThemeIntentMetaData {
	
	//TODO linqiang
	public static final String PANDASPACE_CLS = "com.dragon.android.pandaspace.main.MainActivity";
	public static final String PANDASPACE_PCK = "com.dragon.android.pandaspace";
	
	public static final Object[][] METADATA =  {
		/**
		 * 浏览器
		 */
		{BaseThemeData.ICON_BROWSER, "com.android.browser", "com.android.browser.BrowserActivity", Intent.ACTION_VIEW, Uri.parse( "http://www.google.com" ), "", false },
		/**
		 * 电话
		 */
		{BaseThemeData.ICON_PHONE, 
			new String[]{"com.android.phone","com.android.dialer","com.android.dialer","com.zte.smartdialer","com.android.contacts","com.android.contacts","com.android.contacts","com.android.contacts","com.android.contacts","com.android.contacts","com.android.contacts","com.sonyericsson.android.socialphonebook"}, 
			new String[]{"com.android.phone.LaunchCallInterface","com.android.dialer.ContactsLauncherActivity","com.android.dialer.DialtactsActivity","com.zte.smartdialer.DialerApp","com.android.contacts.activities.TwelveKeyDialer","com.android.contacts.activities.DialtactsActivity","com.android.contacts.DialtactsActivity","com.android.contacts.TwelveKeyDialer","com.sec.android.app.contacts.DialerEntryActivity","com.android.contacts.DialtactsActivity","com.android.contacts.DialtactsContactsEntryActivity","com.sonyericsson.android.socialphonebook.DialerEntryActivity"},
			Intent.ACTION_VIEW, Uri.parse( "tel:" ), "", false },
		/**
		 * 联系人
		 */
		{BaseThemeData.ICON_CONTACTS, 
			new String[]{"com.meizu.mzsnssyncservice","com.yulong.android.contacts","com.android.contacts","com.android.contacts","com.motorola.blur.contacts","com.android.contacts","com.motorola.blur.contacts","com.sonyericsson.android.socialphonebook","com.sonyericsson.android.socialphonebook","com.qihoo360.contacts"}, 
			new String[]{"com.meizu.mzsnssyncservice.ui.SnsTabActivity","com.yulong.android.contacts.ui.main.ContactMainActivity","com.android.contacts.activities.PeopleActivity","com.sec.android.app.contacts.ContactsEntryActivity","com.motorola.blur.contacts.ViewIdentitiesFacetActivity","com.sec.android.app.contacts.PhoneBookTopMenuActivity","com.motorola.blur.contacts.ViewIdentitiesFacetActivity","com.sonyericsson.android.socialphonebook.LaunchActivity","com.sonyericsson.android.socialphonebook.SocialPhonebookActivity","com.qihoo360.contacts.ui.mainscreen.MainTabBase"},
			Intent.ACTION_VIEW, Uri.parse( "content://contacts/people/" ), "", false },
		/**
		 * 地图
		 */
		{BaseThemeData.ICON_MAPS, "com.google.android.apps.maps", "com.google.android.maps.MapsActivity",Intent.ACTION_VIEW, Uri.parse( "geo:38.899533,-77.036476" ), "", true },
		/**
		 * 短信
		 */
		{BaseThemeData.ICON_MMS,
			new String[]{"com.android.mms","com.motorola.blur.conversations","com.android.mms","com.android.mms","com.android.mms","com.sonyericsson.conversations","com.google.android.talk"},
			new String[]{"com.yulong.android.mms.ui.MmsMainListFormActivity","com.motorola.blur.conversations.ui.ConversationList","com.android.mms.ui.MmsTabActivity","com.android.mms.ui.ConversationComposer","com.android.mms.ui.ConversationList","com.sonyericsson.conversations.ui.ConversationListActivity","com.google.android.talk.SigningInActivity"},
			Intent.ACTION_MAIN, Uri.parse( "content://mms-sms/"), "vnd.android-dir/mms-sms", false },
		/**
		 * 相机
		 */
		{BaseThemeData.ICON_CAMERA, 
			new String[]{"com.lge.camera","com.meizu.media.camera","com.htc.camera","com.oppo.camera","com.android.gallery3d","com.android.gallery3d","com.google.android.GoogleCamera","com.android.gallery3d","com.android.camera","com.android.camera","com.motorola.Camera","com.google.android.camera","com.sonyericsson.android.camera","com.sonyericsson.android.camera","com.sec.android.app.camera","com.google.android.gallery3d"},
			new String[]{"com.lge.camera.CameraAppLauncher","com.meizu.media.camera.CameraLauncher","com.htc.camera.CameraEntry","com.oppo.camera.CameraLauncher","com.android.camera.Camera","com.android.camera.CameraLauncher","com.android.camera.CameraLauncher","com.android.camera.CameraActivity","com.android.camera.CameraEntry","com.android.camera.ServiceEntry","com.motorola.Camera.Camera","com.android.camera.Camera","com.sonyericsson.android.camera.CameraActivity","com.sonyericsson.android.camera.CameraActivityForCaputureOnly","com.sec.android.app.camera.Camera","com.android.camera.CameraLauncher"},
			MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA, Uri.EMPTY, "", true },
		/**
		 * 摄像机
		 */
		{BaseThemeData.ICON_VIDEO_CAMERA, 
			new String[]{"com.android.camera","com.motorola.Camera"},
			new String[]{"com.android.camera.CamcorderEntry","com.motorola.Camera.Camcorder"},MediaStore.INTENT_ACTION_VIDEO_CAMERA,  Uri.EMPTY, "", true },
		/**
		 * 邮件
		 */
		{BaseThemeData.ICON_EMAIL, 
				new String[]{"com.asus.email","com.lge.email","com.android.email","com.android.email","com.htc.android.mail","com.motorola.blur.email","com.google.android.email","com.motorola.blur.email", "com.htc.android.mail"}, 
				new String[]{"com.android.email.activity.Welcome","com.lge.email.ui.setupwizard.Welcome","com.android.email.activity.EmailActivity","com.android.email.activity.Welcome","com.htc.android.mail.MailListTab","com.motorola.blur.email.mailbox.ViewFolderActivity","com.android.email.activity.Welcome","com.motorola.blur.email.mailbox.MailListActivity", "com.htc.android.mail.MultipleActivitiesMain"},
				Intent.ACTION_SENDTO, Uri.parse( "mailto:xxx@abc.com" ), "", true },
		/**
		 * 电子市场
		 */
		{BaseThemeData.ICON_GOOGLE_PLAY, "com.android.vending", "com.android.vending.AssetBrowserActivity",Intent.ACTION_VIEW, Uri.EMPTY, "", false },
		/**
		 * 应用商店
		 */
		{BaseThemeData.ICON_APP_STORE, "com.bitants.launcher", "com.nd.hilauncherdev.appstore.AppStoreSwitchActivity",Intent.ACTION_VIEW, Uri.EMPTY, "", false },
		/**
		 * 设置
		 */
		{BaseThemeData.ICON_SETTINGS, 
			new String[]{"com.android.settings","com.android.settings"}, 
			new String[]{"com.android.settings.framework.activity.HtcSettings","com.android.settings.Settings"},"android.settings.SETTINGS", Uri.EMPTY, "", true },
		/**
		 * 音乐
		 */
		{BaseThemeData.ICON_MUSIC, 
			new String[]{"com.miui.player","com.meizu.media.music","com.oppo.music","com.motorola.blur.music","com.android.music","com.htc.music","com.htc.music","com.android.music","com.google.android.music","com.sec.android.app.music","com.sec.android.app.music"}, 
			new String[]{"com.miui.player.ui.MusicBrowserActivity","com.meizu.media.music.MusicActivity","com.oppo.music.MainListActivity","com.motorola.blur.music.DashboardActivity","com.android.music.list.activity.MpMainTabActivity","com.htc.music.browserlayer.MusicBrowserTabActivity","com.htc.music.HtcMusic","com.android.music.MusicBrowserActivity","com.android.music.MusicBrowserActivity","com.sec.android.app.music.MusicBrowserTabActivity","com.sec.android.app.music.MusicActionTabActivity"},
			"android.intent.action.MUSIC_PLAYER", Uri.EMPTY, "", true },
		/**
		 * 图片库
		 */
		{BaseThemeData.ICON_GALLERYPICKER, 
			new String[]{"com.meizu.media.gallery","com.htc.album","com.android.gallery3d","com.oppo.gallery3d","com.cooliris.media","com.sonyericsson.album","com.miui.gallery","com.motorola.blurgallery","com.htc.album","com.htc.album","com.android.gallery","com.motorola.gallery","com.android.camera","com.htc.album","com.htc.album","com.cooliris.media","com.cooliris.media","com.google.android.gallery3d","com.android.gallery","com.sonyericsson.android.mediascape","com.sec.android.gallery3d","com.android.gallery3d","com.sonyericsson.gallery","com.lenovo.scg"}, 
			new String[]{"com.meizu.media.gallery.GalleryActivity","com.htc.album.AlbumMain.ActivityMainTabHost","com.android.gallery3d.vivo.GalleryTabActivity","com.oppo.gallery3d.app.Gallery","com.cooliris.media.Gallery","com.sonyericsson.album.MainActivity","com.miui.gallery.app.Gallery","com.motorola.cgallery.Dashboard","com.htc.album.AlbumTabSwitchActivity","com.htc.album.AlbumMain.ActivityMainDropList","com.android.camera.GalleryPicker","com.motorola.gallery.TopScreen","com.android.camera.GalleryPicker","com.htc.album.AlbumMain.ActivityMainCarousel","com.htc.album.CollectionsActivity","com.cooliris.media.Gallery","com.cooliris.media.Gallery","com.android.gallery3d.app.Gallery","com.android.gallery.ui.MainActivity","com.sonyericsson.android.mediascape.refactor.activity.GetContentImageActivity","com.sec.android.gallery3d.app.Gallery","com.android.gallery3d.app.Gallery","com.sonyericsson.gallery.Gallery","com.lenovo.scg.app.Gallery"},
			Intent.ACTION_VIEW, Uri.parse( "content://media/internal/images/media/"), "", true },
		/**
		 * 计算器
		 */
		{BaseThemeData.ICON_CALCULATOR, 
				new String[]{"com.asus.calculator","com.meizu.flyme.calculator","com.htc.calculator","com.android.bbkcalculator","com.android.calculator2", "com.sec.android.app.calculator","com.sec.android.app.popupcalculator"},
				new String[]{"com.asus.calculator.Calculator","com.meizu.flyme.calculator.Calculator","com.htc.calculator.Calculator","com.android.bbkcalculator.Calculator","com.android.calculator2.Calculator", "com.sec.android.app.calculator.Calculator","com.sec.android.app.popupcalculator.Calculator"},
				Intent.ACTION_VIEW, Uri.EMPTY, "", false },
		/**
		 * 语音拨号
		 */
		{BaseThemeData.ICON_VOICE_DIALER, "com.android.voicedialer", "com.android.voicedialer.VoiceDialerActivity",Intent.ACTION_VIEW, Uri.EMPTY, "", false },
		/**
		 * 语音搜索
		 */
		{BaseThemeData.ICON_VOICE_SEARCH, 
			new String[]{"com.google.android.voicesearch", "com.google.android.googlequicksearchbox"},
			new String[]{"com.google.android.voicesearch.RecognitionActivity", "com.google.android.googlequicksearchbox.VoiceSearchActivity"},
			Intent.ACTION_VIEW, Uri.EMPTY, "", false },
		/**
		 * 闹钟
		 */
		{BaseThemeData.ICON_ALARMCLOCK, 
			new String[]{"com.asus.deskclock","com.lge.clock","com.android.alarmclock","com.android.deskclock","com.android.BBKClock","com.lenovomobile.deskclock","com.ontim.clock","com.ty.clock","com.android.BBKClock","com.lge.clock","com.oppo.alarmclock","com.lenovo.deskclock","com.sonyericsson.organizer","com.android.alarmclock","com.mstarsemi.clock.alarm.deskclock","com.android.deskclock","com.android.alarmclock","com.android.deskclock","com.android.deskclock","com.google.android.deskclock","com.htc.android.worldclock","com.motorola.blur.alarmclock","com.sec.android.app.clockpackage","com.google.android.deskclock","zte.com.cn.alarmclock","com.yulong.android.xtime","com.android.deskclock","com.android.deskclock"}, 
			new String[]{"com.asus.deskclock.DeskClock","com.lge.clock.DefaultAlarmClockActivity","com.meizu.flyme.alarmclock.AlarmClock","com.android.deskclock.MyTabActivity","com.android.BBKClock.Timer","com.lenovomobile.clock.Clock","com.ontim.clock.ClockApp","com.ty.clock.MainActivity","com.android.BBKClock.Timer","com.lge.clock.AlarmClockActivity","com.oppo.alarmclock.AlarmClock","com.lenovo.clock.Clock","com.sonyericsson.organizer.Organizer","com.android.alarmclock.ClockActivity","com.mstarsemi.clock.stopwatch.TimeClockActivity","com.android.deskclock.DeskClockTabActivity","com.android.alarmclock.AlarmClock","com.android.deskclock.DeskClock","com.android.deskclock.AlarmClock","com.google.android.deskclock.DeskClock","com.htc.android.worldclock.WorldClockTabControl","com.motorola.blur.alarmclock.AlarmClock","com.sec.android.app.clockpackage.ClockPackage","com.android.deskclock.DeskClock","zte.com.cn.alarmclock.AlarmClock","yulong.xtime.ui.main.XTimeActivity","com.android.deskclock.AlarmsMainActivity","com.android.deskclock.ClockActivity"},
			Intent.ACTION_VIEW, Uri.EMPTY, "", false },
		/**
		 * 日历
		 */
		{BaseThemeData.ICON_CALENDAR, 
				new String[]{"com.asus.calendar","com.android.calendar","com.lenovo.calendar","com.bbk.calendar","com.google.android.calendar","com.android.calendar","com.android.calendar", "com.android.calendar", "com.google.android.calendar", "com.htc.calendar","com.android.calendar", "com.htc.calendar", "com.htc.calendar", "com.motorola.calendar", "com.yulong.android.calendar"}, 
				new String[]{"com.android.calendar.AllInOneActivity","com.meizu.flyme.calendar.AllInOneActivity","com.lenovo.calendar.SplashActivity","com.bbk.calendar.MainActivity","com.android.calendar.AllInOneActivity","com.android.calendar.MonthActivity","com.android.calendar.AllInOneActivity","com.android.calendar.LaunchActivity", "com.android.calendar.CalendarActivity",
					"com.android.calendar.LaunchActivity", "com.htc.calendar.LaunchActivity", "com.htc.calendar.CalendarActivityMain", "com.htc.calendar.LaunchActivity", "com.android.calendar.AllInOneActivity", "com.yulong.android.calendar.ui.base.LaunchActivity"},
				Intent.ACTION_VIEW, Uri.EMPTY, "", false },
		/**
		 * 天气
		 */
		{BaseThemeData.ICON_WEATHER, 
				new String[]{"com.asus.weathertime","com.meizu.flyme.weather","com.htc.Weather", "com.miui.weather2"}, 
				new String[]{"com.asus.weathertime.WeatherTimeSettings","com.meizu.flyme.weather.WeatherMainActivity","com.htc.Weather.WeatherActivity", "com.miui.weather2.ActivityWeatherCycle"},
				Intent.ACTION_VIEW, Uri.EMPTY, "", false },
		/**
		 * QQ
		 */
		{BaseThemeData.ICON_QQ, 
				new String[]{"com.tencent.qq", "com.tencent.mobileqq"}, 
				new String[]{"com.tencent.qq.SplashActivity", "com.tencent.mobileqq.activity.SplashActivity"},
				Intent.ACTION_VIEW, Uri.EMPTY, "", false },
		/**
		 * 新浪微博
		 */
		{BaseThemeData.ICON_WEIBO, 
				new String[]{"com.sina.weibo"}, 
				new String[]{"com.sina.weibo.SplashActivity"},
				Intent.ACTION_VIEW, Uri.EMPTY, "", false },
		/**
		 * Camera360
		 */
		{BaseThemeData.ICON_CAMERA360, 
				new String[]{"vStudio.Android.Camera360","vStudio.Android.Camera360"}, 
				new String[]{"vStudio.Android.Camera360.activity.FirstInitActivity","vStudio.Android.Camera360.GPhotoMain"},
				Intent.ACTION_VIEW, Uri.EMPTY, "", true },
		/**
		 * 微信
		 */
		{BaseThemeData.ICON_MM, 
				new String[]{"com.tencent.mm"}, 
				new String[]{"com.tencent.mm.ui.LauncherUI"},
				Intent.ACTION_VIEW, Uri.EMPTY, "", false },
		/**
		 * 淘宝
		 */
		{BaseThemeData.ICON_TAOBAO, 
				new String[]{"com.taobao.taobao","com.taobao.taobao","com.taobao.taobao"}, 
				new String[]{"com.taobao.tao.welcome.Welcome","com.taobao.tao.MainActivity2","com.taobao.tao.Welcome"},
				Intent.ACTION_VIEW, Uri.EMPTY, "", true },
		/**
		 * 安卓市场
		 */
		{BaseThemeData.ICON_HIAPK, 
				new String[]{"com.hiapk.marketpho","com.hiapk.marketpho"}, 
				new String[]{"com.hiapk.marketpho.SplashFrame","com.hiapk.marketpho.MarketMainFrame"},
				Intent.ACTION_VIEW, Uri.EMPTY, "", true },
		/**
		 * 91手机助手
		 */
		{BaseThemeData.ICON_PANDASPACE, 
				new String[]{PANDASPACE_PCK, "com.dragon.android.pandaspace"}, 
				new String[]{PANDASPACE_CLS, "com.dragon.android.pandaspace.main.LoadingActivity"},
				Intent.ACTION_VIEW, Uri.EMPTY, "", false },
		/**
		 * Facebook
		 */
		{BaseThemeData.ICON_FACEBOOK, 
				new String[]{"com.facebook.katana"}, 
				new String[]{"com.facebook.katana.LoginActivity"},
				Intent.ACTION_VIEW, Uri.EMPTY, "", false },
		/**
		 * Twitter
		 */
		{BaseThemeData.ICON_TWITTER, 
				new String[]{"com.twitter.android"}, 
				new String[]{"com.twitter.android.StartActivity"},
				Intent.ACTION_VIEW, Uri.EMPTY, "", false },
		/**
		 * Skype
		 */
		{BaseThemeData.ICON_SKYPE, 
				new String[]{"com.skype.rover"}, 
				new String[]{"com.skype.rover.Main"},
				Intent.ACTION_VIEW, Uri.EMPTY, "", false }
	};
}
