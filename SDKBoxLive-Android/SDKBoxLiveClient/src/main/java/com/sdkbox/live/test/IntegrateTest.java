package com.sdkbox.live.test;

import android.app.Activity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sdkbox.live.net.NetReq;
import com.sdkbox.live.utils.Log;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by htl on 28/03/2017.
 */

public class IntegrateTest {

    private Activity activity;
    private IntegrateInfo result;
    private boolean needUpload;
    private boolean waiting;
    private BaseTest test;

    public IntegrateTest(Activity act) {
        activity = act;
        result = new IntegrateInfo();
        waiting = true;
    }

    public void test(String[] plugins, String resultJson) {
        String cfgJson = getJsonString(resultJson, "test");
        for (String plugin : plugins) {
            runTestCase(plugin, getJsonString(cfgJson, plugin));
        }
        uploadResultDelay();
    }

    private String getJsonString(String cfgJson, String plugin) {
        if (null == cfgJson || null == plugin) {
            return null;
        }
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(cfgJson).getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            if (plugin.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue().toString();
            }
        }

        return null;
    }

    private void runTestCase(final String plugin, String cfg) {
        String name = "com.sdkbox.live.test." + plugin + "Test";

        try {
            Class<?> cls = Class.forName(name);
            Constructor con = cls.getConstructor(Activity.class, BaseTest.Result.class);
            test = (BaseTest) con.newInstance(activity, new BaseTest.Result() {

                @Override
                public void onResult(String plugin, String module, String error) {
                    addResult(plugin, module, error);
                }

            });
            test.run(cfg);
        } catch (Exception e) {
            Log.d("IntegrateTest", e.toString());
        }
    }

    private void addResult(String plugin, String module, String error) {
        try {
            needUpload = true;
            if (null == result.plugins) {
                result.plugins = new HashMap<String, IntegratePluginInfo>();
            }
            IntegratePluginInfo p = result.plugins.get(plugin);
            if (null == p) {
                p = new IntegratePluginInfo();
                p.mods = new HashMap<String, IntegrateModInfo>();
                p.plugin = plugin;
            }
            IntegrateModInfo m = p.mods.get(module);
            if (null == m) {
                m = new IntegrateModInfo();
            }
            m.name = module;
            m.error = error;
            p.mods.put(module, m);
            result.plugins.put(plugin, p);

            uploadResult();
        } catch (Exception e) {
        }
    }

    private void uploadResult() {
        if (waiting) {
            return;
        }
        if (!needUpload) {
            return;
        }
        needUpload = false;
        NetReq.httpPost(NetReq.HOST_LIVE_TESTING, result.toString(), null);
    }

    private void uploadResultDelay() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                waiting = false;
                uploadResult();
            }
        }, 10 * 1000); //10 seconds
    }

}
