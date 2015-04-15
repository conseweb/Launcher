package com.nd.launcherdev.app;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.nd.launcherdev.launcher.info.ApplicationInfo;
import com.nd.launcherdev.launcher.model.BaseLauncherSettings;
import com.nd.launcherdev.launcher.info.ApplicationInfo;
import com.nd.launcherdev.launcher.model.BaseLauncherSettings;

public class SerializableAppInfo implements Parcelable {
	public CharSequence title;
	public Intent intent;
	public long id;
	public int itemType;
	  
	public SerializableAppInfo(ApplicationInfo info) {
		title = info.title;
		intent = info.intent;
		id = info.id;
	    itemType = info.itemType;
	}

	public SerializableAppInfo(Parcel in) {
		readFromParcel(in);
	}

	

	@Override
	public String toString() {
		return "SerializableAppInfo(title=" + title.toString() + ", intent=" + intent.toString() + ")";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title.toString());
		dest.writeInt(itemType);
	    dest.writeLong(id);
		intent.writeToParcel(dest, flags);
	}

	public void readFromParcel(Parcel in) {
		title = in.readString();
		itemType = in.readInt();
	    id = in.readLong();
		intent = Intent.CREATOR.createFromParcel(in);
	}

	public static final Parcelable.Creator<SerializableAppInfo> CREATOR = new Parcelable.Creator<SerializableAppInfo>() {
		public SerializableAppInfo createFromParcel(Parcel in) {
			return new SerializableAppInfo(in);
		}

		public SerializableAppInfo[] newArray(int size) {
			return new SerializableAppInfo[size];
		}
	};

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof SerializableAppInfo))
			return false;

		SerializableAppInfo other = (SerializableAppInfo) o;
		if (intent.getComponent() != null) {
			return intent.getComponent().equals(other.intent.getComponent());
		}else {
            if(intent.getAction() != null && itemType == BaseLauncherSettings.Favorites.ITEM_TYPE_CUSTOM_INTENT) {
                return (itemType == other.itemType) && intent.getAction().equals(other.intent.getAction());
            }
        }

		return (id == other.id) && (itemType == other.itemType);
	}
}
