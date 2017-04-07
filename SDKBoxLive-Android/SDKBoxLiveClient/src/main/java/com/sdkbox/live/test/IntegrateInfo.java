package com.sdkbox.live.test;

import com.google.gson.Gson;
import com.sdkbox.live.trace.AndroidTraceInfo;

import java.util.Map;

/**
 * Created by htl on 28/03/2017.
 */

public class IntegrateInfo extends AndroidTraceInfo {

    public Map<String, IntegratePluginInfo> plugins;

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this, IntegrateInfo.class);
    }

}
