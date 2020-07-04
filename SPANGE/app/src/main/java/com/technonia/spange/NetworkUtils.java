package com.technonia.spange;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class NetworkUtils {

    protected static String sendRequestForNewDeviceID(String baseURL, String device_id, String token) {
        String urlStr = baseURL + "/spangeNotification?token=" + token + "&deviceID=" + device_id;

        Log.d("URL", urlStr);

        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // If secure connection
            if (urlStr.startsWith("https")) {
                try {
                    SSLContext sc;
                    sc = SSLContext.getInstance("TLS");
                    sc.init(null, null, new java.security.SecureRandom());
                    ((HttpsURLConnection)conn).setSSLSocketFactory(sc.getSocketFactory());
                } catch (Exception e) {
                    Log.d("SSL", "Failed to construct SSL object", e);
                }
            }

            int timeOut = 5000;

            // Set Timeout and method
            conn.setReadTimeout(timeOut);
            conn.setConnectTimeout(timeOut);

            // set request properties
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");

            conn.setRequestMethod("POST");  // set the request method

            // enable the IO
            conn.setDoInput(true);
            conn.setDoOutput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("fcm_token", token);

            Log.d("JSON", jsonParam.toString());

            conn.connect();

            OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(jsonParam.toString());
            writer.flush();
            writer.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG" , conn.getResponseMessage());

            InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;

            // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            String result_of_request = builder.toString();
            Log.d("Result_of_POST_request", result_of_request);

            conn.disconnect();

            return result_of_request;
        } catch (IOException e) {
            //TODO
            return e.getMessage();
        } catch (JSONException e) {
            //TODO
            return e.getMessage();
        }
    }
}
