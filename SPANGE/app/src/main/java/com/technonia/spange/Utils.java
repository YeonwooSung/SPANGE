package com.technonia.spange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;

public class Utils {
    private static String mApiKey = "[YOUR_API_KEY]";
    private static String mServerKey = "[YOUR SERVER KEY]";
    private static String mMemberKey = "";

    public static final double DEFAULT_LATITUDE = 37.401782989502;
    public static final double DEFAULT_LONGITUDE = 126.7320098877;

    static String getUrlForRecentLocation() {
        String base_url = "http://cms.catchloc.com/api.get.member.location.last.php";
        return buildBaseURL(base_url) + getQueryStringForTimestampAndCertKey();
    }

    static String getUrlForRouteData(Date fromDate, Date toDate) {
        String base_url = "http://cms.catchloc.com/api.get.member.location.list.php";
        long fromDateVal = fromDate.getTime();
        long toDateVal = toDate.getTime();

        return buildBaseURL(base_url) + getQueryStringForTimestampAndCertKey() + "&from_date=" + fromDateVal + "&to_date=" + toDateVal;
    }

    static String getWebViewURL() {
        String base_url = "http://cms.catchloc.com/api.view.member.location.php";
        return buildBaseURL(base_url) + getQueryStringForTimestampAndCertKey();
    }

    private static String buildBaseURL(String base_url) {
        return base_url + "?api_key=" + mApiKey + "&member_key=" + mMemberKey;
    }

    private static String getQueryStringForTimestampAndCertKey() {
        long timestamp = getTimestamp();
        String certKey = getAPICertKey(timestamp, mApiKey, mServerKey);
        return "&timestamp=" + timestamp + "&cert_key=" + certKey;
    }

    private static String getAPICertKey(long timestamp, String api_key, String server_key) {
        String hash_in = timestamp + "|" + api_key + "|" + server_key;
        String result = "";
        byte[] input = hash_in.getBytes();

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(input, 0, input.length);
            result = new BigInteger(1, messageDigest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static long getTimestamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.getTime();
    }

    public static JSONArray parseResponseArray(String res) {
        if (res == null || res.startsWith("Error") || res.contains("Exception"))
            return null;

        String s = res.trim();
        if (s.startsWith("[") && s.endsWith("]")) {
            try {
                return new JSONArray(s);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    public static JSONObject parseResponse(String res) {
        if (res == null || res.startsWith("Error") || res.contains("Exception"))
            return null;

        String s = res.trim();
        if (s.startsWith("{") && s.endsWith("}")) {
            try {
                return new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }
}
