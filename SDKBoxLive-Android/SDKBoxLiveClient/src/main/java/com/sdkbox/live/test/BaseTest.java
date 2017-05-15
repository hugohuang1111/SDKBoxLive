package com.sdkbox.live.test;

import android.app.Activity;

/**
 * Created by htl on 28/03/2017.
 */


public class BaseTest {

    public interface Result {
        public void onResult(String plugin, String module, int err, String desc);
    }

    protected Activity activity;
    private String plugin;
    private Result result;

    BaseTest(Activity act, String p, Result r) {
        activity = act;
        plugin = p;
        result = r;
    }

    public void run(String config) {
    }

    protected void onResult(String m, int err, String desc) {
        if (null != result) {
            result.onResult(plugin, m, err, desc);
        }
    }

}
