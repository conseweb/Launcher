/*
 *  Copyright 2010 Emmanuel Astier & Kevin Gaudin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.acra;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import com.bitants.quickappinstall.R;

/**
 * <p>
 * Base class for any application which need to send crash reports. The final
 * application has to extend this class and at least implement the abstract
 * method by returning a valid GoogleDocs Form id string
 * generated by importing ACRA's specific spreadsheet template.
 * </p>
 * <p>
 * If you prefer sending crash reports to your own script on your own server,
 * you can override {@link #getFormUri()} and return any Url string to your
 * sever script (like : "http://www.myserver.com/myscript.php").
 * </p>
 * <p>
 * If some crash reports could not be sent (due to technical issues like loss of
 * network connection), their data is stored in the application private
 * filesystem and sent on the next <strong>Application</strong> start. This
 * means that the reports might be sent quite some time after the crash, because
 * a crash causes the Activities to be destroyed but not the Application.
 * </p>
 * <p>
 * If you would like to receive reports as soon as possible, you may want to
 * call {@link ErrorReporter#checkReportsOnApplicationStart()} on
 * {@link ErrorReporter#getInstance()} in your main {@link android.app.Activity} onCreate()
 * method.
 * </p>
 */
public class CrashReportingLibrary implements
    OnSharedPreferenceChangeListener {
  protected static final String LOG_TAG = "ACRA";

  /**
   * Bundle key for the icon in the status bar notification.
   *
   * @see #getCrashResources()
   */
  public static final String RES_NOTIF_ICON = "RES_NOTIF_ICON";
  /**
   * Bundle key for the ticker text in the status bar notification.
   *
   * @see #getCrashResources()
   */
  public static final String RES_NOTIF_TICKER_TEXT = "RES_NOTIF_TICKER_TEXT";
  /**
   * Bundle key for the title in the status bar notification.
   *
   * @see #getCrashResources()
   */
  public static final String RES_NOTIF_TITLE = "RES_NOTIF_TITLE";
  /**
   * Bundle key for the text in the status bar notification.
   *
   * @see #getCrashResources()
   */
  public static final String RES_NOTIF_TEXT = "RES_NOTIF_TEXT";
  /**
   * Bundle key for the icon in the crash dialog.
   *
   * @see #getCrashResources()
   */
  public static final String RES_DIALOG_ICON = "RES_DIALOG_ICON";
  /**
   * Bundle key for the title in the crash dialog.
   *
   * @see #getCrashResources()
   */
  public static final String RES_DIALOG_TITLE = "RES_DIALOG_TITLE";
  /**
   * Bundle key for the text in the crash dialog.
   *
   * @see #getCrashResources()
   */
  public static final String RES_DIALOG_TEXT = "RES_DIALOG_TEXT";
  /**
   * Bundle key for the user comment input label in the crash dialog. If not
   * provided, disables the input field.
   *
   * @see #getCrashResources()
   */
  public static final String RES_DIALOG_COMMENT_PROMPT = "RES_DIALOG_COMMENT_PROMPT";
  /**
   * Bundle key for the Toast text triggered when the user accepts to send a
   * report in the crash dialog.
   *
   * @see #getCrashResources()
   */
  public static final String RES_DIALOG_OK_TOAST = "RES_DIALOG_OK_TOAST";
  /**
   * Bundle key for the Toast text triggered when the application crashes if
   * the notification+dialog mode is not used.
   *
   * @see #getCrashResources()
   */
  public static final String RES_TOAST_TEXT = "RES_TOAST_TEXT";

  /**
   * This is the identifier (value = 666) use for the status bar notification
   * issued when crashes occur.
   */
  public static final int NOTIF_CRASH_ID = 666;

  /**
   * The key of the application default SharedPreference where you can put a
   * 'true' Boolean value to disable ACRA.
   */
  public static final String PREF_DISABLE_ACRA = "acra.disable";

  /**
   * Alternatively, you can use this key if you prefer your users to have the
   * checkbox ticked to enable crash reports. If both acra.disable and
   * acra.enable are set, the value of acra.disable takes over the other.
   */
  public static final String PREF_ENABLE_ACRA = "acra.enable";

  private Context mContext = null;

  public CrashReportingLibrary(Context ctx) {
    mContext = ctx;
    this.onInit();
  }

  public Context getContext() {
    return mContext;
  }

  /*
  * (non-Javadoc)
  *
  * @see android.app.Application#onCreate()
  */
//    @Override

  public void onInit() {
//        super.onCreate();

    SharedPreferences prefs = getACRASharedPreferences();
    prefs.registerOnSharedPreferenceChangeListener(this);

    // If the application default shared preferences contains true for the
    // key "acra.disable", do not activate ACRA. Also checks the alternative
    // opposite setting "acra.enable" if "acra.disable" is not found.
    boolean disableAcra = false;
    try {
      disableAcra = prefs.getBoolean(PREF_DISABLE_ACRA,
          !prefs.getBoolean(PREF_ENABLE_ACRA, true));
    } catch (Exception e) {
      // In case of a ClassCastException
    }

    if (disableAcra) {
      Log.d(LOG_TAG, "ACRA is disabled for " + mContext.getPackageName()
          + ".");
      return;
    } else {
      initAcra();
    }
  }

  /**
   * Activate ACRA.
   */
  private void initAcra() {
    Log.d(LOG_TAG, "ACRA is enabled for " + mContext.getPackageName()
        + ", intializing...");
    // Initialise ErrorReporter with all required data
    ErrorReporter errorReporter = ErrorReporter.getInstance();
    errorReporter.setFormUri(getFormUri());
    errorReporter
        .setReportingInteractionMode(getReportingInteractionMode());

    errorReporter.setCrashResources(getCrashResources());

    // Activate the ErrorReporter
    errorReporter.init(mContext);

    // Check for pending reports

    errorReporter.checkReportsOnApplicationStart();
  }

  /**
   * <p>
   * Override this method to send the crash reports to your own server script.
   * Your script will have to get HTTP POST request parameters named as
   * described in {@link ErrorReporter} source code (*_KEY fields values).
   * </p>
   * <p>
   * If you override this method with your own url, your implementation of the
   * abstract can be empty as it will not be called by
   * any other method or object.
   * </p>
   *
   * @return A String containing the Url of your custom server script.
   */
  public Uri getFormUri() {
    return Uri.parse("http://xqueenant.appspot.com/mobt/e");
  }

  /**
   * Guess the ReportingInteractionMode chosen by the developer by analysing
   * the content of the Bundle provided by {@link #getCrashResources()}. If it
   * contains {@link #RES_TOAST_TEXT}, TOAST mode is activated. Otherwise,
   * NOTIFICATION mode is used if the Bundle contains the minimal set of
   * resources required. In any other cases, activates the SILENT mode.
   *
   * @return The interaction mode
   */
  ReportingInteractionMode getReportingInteractionMode() {
    Bundle res = getCrashResources();
    if (res != null && res.getInt(RES_TOAST_TEXT) != 0) {
      Log.d(LOG_TAG, "Using TOAST mode.");
      return ReportingInteractionMode.TOAST;
    } else if (res != null && res.getInt(RES_NOTIF_TICKER_TEXT) != 0
        && res.getInt(RES_NOTIF_TEXT) != 0
        && res.getInt(RES_NOTIF_TITLE) != 0
        && res.getInt(RES_DIALOG_TEXT) != 0) {
      Log.d(LOG_TAG, "Using NOTIFICATION mode.");
      return ReportingInteractionMode.NOTIFICATION;
    } else {
      Log.d(LOG_TAG, "Using SILENT mode.");
      return ReportingInteractionMode.SILENT;
    }
  }

  /**
   * Override this method to activate user notifications. Return a Bundle
   * containing :
   * <ul>
   * <li>{@link #RES_TOAST_TEXT} to activate the Toast notification mode</li>
   * <li>At least {@link #RES_NOTIF_TICKER_TEXT}, {@link #RES_NOTIF_TEXT},
   * {@link #RES_NOTIF_TITLE} and {@link #RES_DIALOG_TEXT} to activate status
   * bar notifications + dialog mode. You can additionally set
   * {@link #RES_DIALOG_COMMENT_PROMPT} to activate an input field for the
   * user to add a comment. Use {@link #RES_NOTIF_ICON},
   * {@link #RES_DIALOG_ICON}, {@link #RES_DIALOG_TITLE} or
   * {@link #RES_DIALOG_OK_TOAST} for further UI tweaks.</li>
   * </ul>
   *
   * @return A Bundle containing the resource Ids necessary to interact with
   *         the user.
   */
//  public Bundle getCrashResources() {
//    return null;
//  }

//  @Override
    public Bundle getCrashResources() {
        // Silent Mode
//        return null;

        // Toast mode
        return getToastCrashResources();

        // Notification mode with mandatory resources
//        return getMinNotificationCrashResources();

        // Notification mode with all resources
//        return getFullNotificationCrashResources();
    }

    private Bundle getToastCrashResources() {
        Bundle result = new Bundle();
        result.putInt(RES_TOAST_TEXT, R.string.crash_toast_text);
        return result;
    }

    private Bundle getFullNotificationCrashResources() {
        Bundle result = new Bundle();
        result.putInt(RES_NOTIF_ICON, android.R.drawable.stat_notify_error);
        result.putInt(RES_NOTIF_TICKER_TEXT, R.string.crash_notif_ticker_text);
        result.putInt(RES_NOTIF_TITLE, R.string.crash_notif_title);
        result.putInt(RES_NOTIF_TEXT, R.string.crash_notif_text);
        result.putInt(RES_DIALOG_ICON, android.R.drawable.ic_dialog_info);
        result.putInt(RES_DIALOG_TITLE, R.string.crash_dialog_title);
        result.putInt(RES_DIALOG_TEXT, R.string.crash_dialog_text);
        result.putInt(RES_DIALOG_COMMENT_PROMPT, R.string.crash_dialog_comment_prompt);
        result.putInt(RES_DIALOG_OK_TOAST, R.string.crash_dialog_ok_toast);
        return result;
    }

    private Bundle getMinNotificationCrashResources() {
        Bundle result = new Bundle();
        result.putInt(RES_NOTIF_TICKER_TEXT, R.string.crash_notif_ticker_text);
        result.putInt(RES_NOTIF_TITLE, R.string.crash_notif_title);
        result.putInt(RES_NOTIF_TEXT, R.string.crash_notif_text);
        result.putInt(RES_DIALOG_TEXT, R.string.crash_dialog_text);
        return result;
    }

  /*
  * (non-Javadoc)
  *
  * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#
  * onSharedPreferenceChanged(android.content.SharedPreferences,
  * java.lang.String)
  */

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                        String key) {
    if (PREF_DISABLE_ACRA.equals(key)) {
      Boolean disableAcra = false;
      try {
        disableAcra = sharedPreferences.getBoolean(key, false);
      } catch (Exception e) {
        // In case of a ClassCastException
      }
      if (disableAcra) {
        ErrorReporter.getInstance().disable();
      } else {
        initAcra();
      }
    }
  }

  /**
   * Override this method if you need to store "acra.disable" or "acra.enable"
   * in a different SharedPrefence than the application's default.
   *
   * @return The Shared Preferences where ACRA will check the value of the
   *         setting which disables/enables it's action.
   */
  public SharedPreferences getACRASharedPreferences() {
    return PreferenceManager.getDefaultSharedPreferences(this.mContext);
  }

}