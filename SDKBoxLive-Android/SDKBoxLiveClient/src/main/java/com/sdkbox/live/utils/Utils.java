package com.sdkbox.live.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hugo on 09/03/2017.
 */

public class Utils {

    static public boolean isNull(String s) {
        return null == s || 0 == s.length();
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

    static public void run(int millisecond, final Runnable r) {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                r.run();
            }
        }, millisecond);
    }

}
