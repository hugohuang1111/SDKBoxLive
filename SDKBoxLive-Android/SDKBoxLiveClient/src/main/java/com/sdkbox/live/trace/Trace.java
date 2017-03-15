package com.sdkbox.live.trace;

import com.sdkbox.live.SDKBox;
import com.sdkbox.live.net.NetReq;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by htl on 07/03/2017.
 */

public class Trace {

    static public void t(URL url) {
        AndroidTraceInfo info = new AndroidTraceInfo();
        info.reqUrl = url.getHost() + url.getPath();
        String j = info.toString();
        if (0 == j.length()) {
            return;
        }
        NetReq.req(j);
    }
}
