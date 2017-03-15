package com.sdkbox.live.bridge;

import android.webkit.WebView;

import com.sdkbox.live.trace.Trace;
import com.sdkbox.live.utils.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class InMobiBridge {

    private static String TAG = "InMobiBridge";
    private static boolean enable = true;

    public static URLConnection URL_openConnection(URL url) throws IOException {
        Log.d(TAG, "URL_openConnection:" + url);
        if (!enable) {
            throw new IOException("Network is denied by NetBridge");
        }
        Trace.t(url);

        return url.openConnection();
    }

    public static void HttpURLConnect_connect(URLConnection n) throws IOException {
        Log.d(TAG, "HttpURLConnect_connect");
        if (!enable) {
            throw new IOException("Network is denied by NetBridge");
        }

        n.connect();
    }

    public static int HttpURLConnect_getResponseCode(HttpURLConnection n) throws IOException {
        Log.d(TAG, "HttpURLConnect_getResponseCode");
        if (!enable) {
            throw new IOException("Network is denied by NetBridge");
        }

        return n.getResponseCode();
    }

    public static int HttpURLConnect_getResponseCode(HttpsURLConnection n) throws IOException {
        Log.d(TAG, "HttpURLConnect_getResponseCode");
        if (!enable) {
            throw new IOException("Network is denied by NetBridge");
        }

        return n.getResponseCode();
    }

    public static void WebView_loadUrl(WebView targetInstance, String url) {
        Log.d(TAG, "HttpURLConnect_connect");
        if (!enable) {
            return;
        }
        targetInstance.loadUrl(url);
    }

    public static void WebView_loadUrl(WebView targetInstance, String url, Map<String, String> additionalHttpHeaders) {
        Log.d(TAG, "HttpURLConnect_connect");
        if (!enable) {
            return;
        }
        targetInstance.loadUrl(url, additionalHttpHeaders);
    }

}
