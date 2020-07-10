package com.technonia.spange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    private final String base_url = "http://cms.catchloc.com/api.view.member.location.php";
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private static String mApiKey = "[YOUR_API_KEY]";
    private static String mServerKey = "[YOUR SERVER KEY]";
    private static String mMemberKey =  "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build());

        Window win = getWindow();
        win.requestFeature(Window.FEATURE_NO_TITLE);
        //win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progressBar);

        // setup WebView
        mWebView = (WebView)findViewById(R.id.catchloc_webview);
        mWebView.setWebViewClient(new WebViewClient());
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDomStorageEnabled(true);

        // setup for CookieManager
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(mWebView, true);

        String url = getWebViewURL();
        mWebView.loadUrl(url);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    mProgressBar.setVisibility(ProgressBar.VISIBLE);
                } else if (progress == 100) {
                    mProgressBar.setVisibility(ProgressBar.GONE);
                }
                mProgressBar.setProgress(progress);
            }
        });

        // set up event handlers for the buttons
        setUpButtons();
    }

    private String getWebViewURL() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long timestampVal = timestamp.getTime();
        String certKey = getAPICertKey(timestampVal, mApiKey, mServerKey);
        String url = base_url + "?api_key=" + mApiKey + "&member_key=" + mMemberKey + "&timestamp=" + timestampVal + "&cert_key=" + certKey;

        return url;
    }

    private void setUpButtons() {
        Button btn_start_location = (Button)findViewById(R.id.btn_refresh);
        btn_start_location.setClickable(true);
        btn_start_location.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mWebView.reload();
            }
        });

        Button btn_stop_location = (Button)findViewById(R.id.btn_setting);
        btn_stop_location.setClickable(true);
        btn_stop_location.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            //TODO getCookie()
            String user_id = "app_test";

            // navigate to settings
            Intent navigation_intent = new Intent(MainActivity.this, SettingsActivity.class);
            navigation_intent.putExtra(getString(R.string.extra_str_key_user_id), user_id);
            startActivity(navigation_intent);
            }
        });
    }

    public static String getAPICertKey(long timestamp, String api_key, String server_key) {
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


    /**
     * Get specific cookie data.
     * Referred to:
     *          https://stackoverflow.com/questions/17654631/android-webview-read-cookies/20241864
     * @param siteName The name of the website
     * @param cookieName The name of the target cookie
     * @return If cookie exists, returns the cookie value. Otherwise, returns null.
     */
    private String getCookie(String siteName, String cookieName) {
        AtomicReference<String> cookieValue = new AtomicReference<String>();

        CookieManager cookieManager = CookieManager.getInstance();
        String cookie_str = cookieManager.getCookie(siteName);

        if(cookie_str != null){
            String[] cookies = cookie_str.split(";");
            for (String cookie : cookies ){
                if(cookie.contains(cookieName))
                    cookieValue.set(cookie.replace(cookieName, "").replace("=", ""));
            }
        }
        return cookieValue.get();
    }
}
