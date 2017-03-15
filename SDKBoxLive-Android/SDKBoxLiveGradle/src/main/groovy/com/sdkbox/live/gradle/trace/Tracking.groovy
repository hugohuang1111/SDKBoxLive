package com.sdkbox.live.gradle.trace

import com.sdkbox.live.gradle.utils.Log
import groovy.json.JsonOutput

public class Tracking {

    def appID = ''
    def appSec = ''
    def platform = 'android'
    def phase = 'compile'

    private static String host = "http://120.55.89.27:5001/sdkboxlive/data"
    private static Tracking gInstance;

    public static Tracking getInstance() {
        if (!gInstance) {
            gInstance = new Tracking()
        }

        return gInstance
    }

    private void fillBaseInfo(traceInfo) {
        traceInfo.put('appID', appID)
        traceInfo.put('appSec', appSec)
        traceInfo.put('platform', platform)
        traceInfo.put('phase', phase)
    }

    static private boolean checkTraceInfo(traceInfo) {
        if (!traceInfo.appID || !traceInfo.appSec || !traceInfo.platform) {
            return false
        }
        return true
    }

    public void trace(traceInfo) {
        fillBaseInfo(traceInfo)
        if (!checkTraceInfo(traceInfo)) {
            Log.debug('TraceInfo require key is not all setted')
            return
        }
        def json = JsonOutput.toJson(traceInfo)
        sendPostRequest(host, json)
    }

    def static int sendPostRequest(host, paramString) {
        URL url;
        HttpURLConnection connection = null;
        int rst = 0
        Log.debug("sendPostRequest: ${paramString}")
        try {
            //Create connection
            url = new URL(host);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(paramString.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            wr.writeBytes (paramString);
            wr.flush ();
            wr.close ();

            rst = connection.getResponseCode()

            //Get Response
            /*
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
            */
        } catch (Exception e) {
            Log.debug(e)
            rst = 1;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }

        Log.debug("sendPostRequest resp code: ${rst}")
        return rst
    }

}
