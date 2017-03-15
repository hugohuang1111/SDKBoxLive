package com.sdkbox.live.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by hugo on 09/03/2017.
 */

public class Utils {

    static public boolean isNull(String s) {
        if (null == s) {
            return true;
        }
        if (0 == s.length()) {
            return true;
        }
        return false;
    }

    static public String readMetaData(Context ctx, String tag) {
        String value = null;
        try {
            ApplicationInfo appInfo = ctx.getPackageManager()
                    .getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(tag, "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return value;
    }

}
