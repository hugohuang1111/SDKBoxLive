package com.sdkbox.live.app;

import android.app.Application;

import com.sdkbox.live.SDKBox;

/**
 * Created by hugo on 09/03/2017.
 */

public class SDKBoxApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKBox.start(getApplicationContext());
    }
}
