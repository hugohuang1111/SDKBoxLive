package com.sdkbox.live.net;

import com.sdkbox.live.utils.Log;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by htl on 07/03/2017.
 */

public class NetReq {

    private static String host = "http://120.55.89.27:5001/sdkboxlive/data";

    static public void req(final String json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                reqReal(json);
            }
        }).start();
    }

    static private int reqReal(String json) {
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

            rst = connection.getResponseCode();

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
