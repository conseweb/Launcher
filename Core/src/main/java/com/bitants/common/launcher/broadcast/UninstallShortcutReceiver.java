/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bitants.common.launcher.broadcast;

import java.net.URISyntaxException;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.widget.Toast;

import com.bitants.common.launcher.BaseLauncher;
import com.bitants.common.launcher.model.BaseLauncherSettings;
import com.bitants.common.R;

public class UninstallShortcutReceiver extends HiBroadcastStaticReceiver {
	private static final String ACTION_UNINSTALL_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";

	@Override
	public void onReceiveHandler(Context context, Intent data) {
		if (!ACTION_UNINSTALL_SHORTCUT.equals(data.getAction())) {
			return;
		}

		Parcelable p = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
		if (p == null || !(p instanceof Intent))
			return;

		Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
		String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
		boolean duplicate = data.getBooleanExtra(BaseLauncher.EXTRA_SHORTCUT_DUPLICATE, true);

		if (intent != null && name != null) {
			final ContentResolver cr = context.getContentResolver();
			Cursor c = cr.query(BaseLauncherSettings.Favorites.getContentUri(), new String[] { BaseLauncherSettings.Favorites._ID,
					BaseLauncherSettings.Favorites.INTENT }, BaseLauncherSettings.Favorites.TITLE + "=?", new String[] { name }, null);

			final int intentIndex = c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites.INTENT);
			final int idIndex = c.getColumnIndexOrThrow(BaseLauncherSettings.Favorites._ID);

			boolean changed = false;

			try {
				while (c.moveToNext()) {
					try {
						String intentStr = c.getString(intentIndex);
						if (null == intentStr)
							continue;
						if (intent.filterEquals(Intent.parseUri(c.getString(intentIndex), 0))) {
							final long id = c.getLong(idIndex);
							final Uri uri = BaseLauncherSettings.Favorites.getContentUri(id, false);
							cr.delete(uri, null, null);
							changed = true;
							if (!duplicate) {
								break;
							}
						}
					} catch (URISyntaxException e) {
						// Ignore
					}
				}
			} finally {
				c.close();
			}

			if (changed) {
				cr.notifyChange(BaseLauncherSettings.Favorites.getContentUri(), null);
				Toast.makeText(context, context.getString(R.string.shortcut_uninstalled, name), Toast.LENGTH_SHORT).show();
			}
		}
	}
}
