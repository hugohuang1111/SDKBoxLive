package com.sdkbox.live.trace;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.internal.Streams;
import com.sdkbox.live.SDKBox;
import com.sdkbox.live.utils.Utils;

import java.lang.reflect.Type;

/**
 * Created by htl on 07/03/2017.
 */

public class TraceInfo {

    public String appID;

    public String appSec;

    public String platform;

    public String phase;

    public String[] deps;

    public String reqUrl;

    public TraceInfo() {
        appID = SDKBox.getAppID();
        appSec = SDKBox.getAppSec();
        phase = "runtime";
    }

    public String toString(Type t) {
        if (Utils.isNull(appID) || Utils.isNull(appSec) || Utils.isNull(platform)) {
            return "";
        }

        Gson gson = new Gson();
        return gson.toJson(this, t);
    }

}
