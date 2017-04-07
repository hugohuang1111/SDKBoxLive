package com.sdkbox.live.test;

import android.app.Activity;

/**
 * Created by htl on 28/03/2017.
 */


public class BaseTest {

    public interface Result {
        public void onResult(String plugin, String module, String error);
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

    protected void onResult(String m, String err) {
        if (null != result) {
            result.onResult(plugin, m, err);
        }
    }

}
