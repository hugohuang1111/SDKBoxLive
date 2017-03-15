package com.sdkbox.live;

import android.content.Context;

import com.sdkbox.live.trace.AndroidTraceInfo;
import com.sdkbox.live.trace.Trace;
import com.sdkbox.live.utils.Log;
import com.sdkbox.live.utils.Utils;

/**
 * Created by hugo on 09/03/2017.
 */

public class SDKBox {

    private static String appID;
    private static String appSec;

    static public void start(Context ctx) {
        appID = Utils.readMetaData(ctx, "SDKBoxAppID");
        appSec = Utils.readMetaData(ctx, "SDKBoxAppSec");

        Log.d("SDKBox", "AppID:" + appID);
        Log.d("SDKBox", "AppSec:" + appSec);

    }

    static public String getAppID() {
        return appID;
    }

    static public String getAppSec() {
        return appSec;
    }
}
