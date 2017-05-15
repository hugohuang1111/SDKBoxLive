package com.sdkbox.live.net;

import com.sdkbox.live.utils.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by htl on 07/03/2017.
 */

public class NetReq {

    public interface IResult {
        void onResponse(String resp);
    }

    public static String HOST_LIVE_NETWORK = "http://120.55.89.27:5001/sdkboxlive/network";
    public static String HOST_LIVE_TESTING = "http://120.55.89.27:5001/sdkboxlive/testing";
    public static String HOST_LIVE_DEBUG = "http://120.55.89.27:5001/sdkboxlive/opts/testing";

    static public void httpGet(final String host, final String param, final IResult Iresult) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                httpGetReal(host, param, Iresult);
            }
        }).start();
    }

    static public void httpPost(final String host, final String param, final IResult Iresult) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                httpPostReal(host, param, Iresult);
            }
        }).start();
    }

    static private int httpGetReal(String host, String param, IResult iresult) {
        int rst = 0;
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            URL realUrl = null;
            if (null == param) {
                realUrl = new URL(host);
            } else {
                realUrl = new URL(host + "?" + param);
            }
            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();

            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
//            connection.setRequestProperty("user-agent",
//                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            connection.connect();
            rst = connection.getResponseCode();

            /*
            Map<String, List<String>> map = connection.getHeaderFields();
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            */

            if (null != iresult) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                    result.append('\r');
                }

                iresult.onResponse(result.toString());
            }
        } catch (Exception e) {
            Log.d("NetReq", e.toString());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                Log.d("NetReq", e2.toString());
            }
        }

        return rst;
    }

    static private int httpPostReal(String host, String json, IResult iresult) {
        URL url;
        HttpURLConnection connection = null;
        int rst = 0;

        Log.d("SDKBox", "NetReq req json: " + json);
        try {
            //Create connection
            url = new URL(host);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            if (null != json) {
                connection.setRequestProperty("Content-Length", "" +
                        Integer.toString(json.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches (false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream ());
                wr.writeBytes (json);
                wr.flush ();
                wr.close ();
            }

            rst = connection.getResponseCode();

            //Get Response
            if (null != iresult) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    Log.d("SDKBox", "line:" + line);
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                iresult.onResponse(response.toString());
            }
        } catch (Exception e) {
            Log.d("NetReq", e.toString());
            rst = 1;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }

        Log.d("SDKBox", "NetReq req resp code: " + rst);
        return rst;
    }
}
