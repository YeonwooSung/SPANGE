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
import java.net.URLConnection;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class NetworkUtils {

    static String sendRequestForNewDeviceID(String baseURL, String device_id, String token) {
        String urlStr = baseURL + "/spangeNotification?token=" + token + "&deviceID=" + device_id;
        Log.d("URL", urlStr);
        return sendPOSTRequest(urlStr, token, null, device_id);
    }

    static String sendRequestForAcceptUser(String baseURL, String user_id, String device_id) {
        String urlStr = baseURL + "/spangeNotification/acceptUser?userID=" + user_id + "&deviceID" + device_id;
        Log.d("URL", urlStr);
        return sendPOSTRequest(urlStr, null, user_id, device_id);
    }

    static String sendRequestToUpdateToken(String baseURL, String previousToken, String newToken) {
        String urlStr = baseURL + "/spangeNotification/updateToken?previousToken=" + previousToken + "&newToken=" + newToken;
        Log.d("URL", urlStr);
        return sendPOSTRequest(urlStr, newToken, null, null);
    }

    static String sendRequestToRegisterDevice(String baseURL, String user_id, String device_id) {
        String urlStr = baseURL + "/spangeNotification/registerDevice?userID=" + user_id + "&deviceID=" + device_id;
        Log.d("URL", urlStr);
        return sendPOSTRequest(urlStr, null, user_id, device_id);
    }

    static String sendRequestForRegisterUser(String baseURL, String user_id, String token) {
        String urlStr = baseURL + "/spangeNotification/registerUser?userID=" + user_id + "&token=" + token;
        Log.d("URL", urlStr);
        return sendPOSTRequest(urlStr, token, user_id, null);
    }

    private static String sendPOSTRequest(String urlStr, String token, String user_id, String device_id) {
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

            // check if null
            if (token != null) jsonParam.put("fcm_token", token);
            if (user_id != null) jsonParam.put("user_id", user_id);
            if (device_id != null) jsonParam.put("device_id", device_id);

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
        } catch (IOException | JSONException e) {
            return e.getMessage();
        }
    }


    static String sendRequestToGetRecentLocation() {
        String urlStr = Utils.getUrlForRecentLocation();
        Log.d("URL", urlStr);
        return sendGETRequest(urlStr);
    }

    static String sendRequestToGetRoute(Date fromDate, Date toDate) {
        String urlStr = Utils.getUrlForRouteData(fromDate, toDate);
        Log.d("URL", urlStr);
        return sendGETRequest(urlStr);
    }

    private static String sendGETRequest(String urlStr) {
        String result = null;
        int resCode;
        try {
            URL url = new URL(urlStr);
            URLConnection urlConn = url.openConnection();

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

            //HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            conn.connect();
            resCode = conn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                result = sb.toString();
            } else {
                result = "Error - " + resCode;
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
