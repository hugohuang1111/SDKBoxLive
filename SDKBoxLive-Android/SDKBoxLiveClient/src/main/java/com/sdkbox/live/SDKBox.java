package com.sdkbox.live;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.sdkbox.live.net.NetReq;
import com.sdkbox.live.test.IntegrateTest;
import com.sdkbox.live.test.ReqParams;
import com.sdkbox.live.utils.Log;
import com.sdkbox.live.utils.Utils;

/**
 * Created by hugo on 09/03/2017.
 */

public class SDKBox {

    private static String appID;
    private static String appSec;
    private static String packageName;
    private static Activity activity;
    private static IntegrateTest test;

    static public void start(Context ctx) {
        appID = Utils.readMetaData(ctx, "SDKBoxAppID");
        appSec = Utils.readMetaData(ctx, "SDKBoxAppSec");

        Log.d("SDKBox", "AppID:" + appID);
        Log.d("SDKBox", "AppSec:" + appSec);

        Application app = (Application)ctx;
        packageName = app.getPackageName();
        registerActivityLifecycleCallbacks(app);
        Utils.run(1 * 1000, new Runnable() {
            @Override
            public void run() {
                if (null == SDKBox.activity) {
                    return;
                }

                ReqParams params = new ReqParams();
                params.phase = "runtime";
                params.packageName = packageName;

                NetReq.httpGet(NetReq.HOST_LIVE_DEBUG, params.toString(), new NetReq.IResult() {
                    @Override
                    public void onResponse(String resp) {
                        if (null == resp) {
                            return;
                        }
                        SDKBox.test = new IntegrateTest(SDKBox.activity);
                        String[] strArr = new String[1];
                        strArr[0] = "InMobi";
                        SDKBox.test.test(strArr, resp);
                    }
                });
            }
        });
    }

    static public String getAppID() {
        return appID;
    }

    static public String getAppSec() {
        return appSec;
    }

    static public Activity getCurrentActivity() {
        return activity;
    }

    static private void registerActivityLifecycleCallbacks(Application app) {
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {}

            @Override
            public void onActivityStarted(Activity activity) {}

            @Override
            public void onActivityResumed(Activity activity) {
                SDKBox.activity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                SDKBox.activity = null;
            }

            @Override
            public void onActivityStopped(Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

            @Override
            public void onActivityDestroyed(Activity activity) {}
        });
    }
}
