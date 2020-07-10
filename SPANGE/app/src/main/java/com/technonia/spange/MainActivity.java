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

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private static String userID = "app_test";

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

        String url = Utils.getWebViewURL();
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

    private void setUpButtons() {
        Button btn_start_location = (Button)findViewById(R.id.btn_refresh);
        btn_start_location.setClickable(true);
        btn_start_location.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //mWebView.reload();

                Intent navigation = new Intent(MainActivity.this, RealtimeMap.class);
                startActivity(navigation);
            }
        });

        Button btn_stop_location = (Button)findViewById(R.id.btn_setting);
        btn_stop_location.setClickable(true);
        btn_stop_location.setOnClickListener(new Button.OnClickListener() {
            private String user_id = userID;
            public void onClick(View v) {
                // navigate to settings
                Intent navigation_intent = new Intent(MainActivity.this, SettingsActivity.class);
                navigation_intent.putExtra(getString(R.string.extra_str_key_user_id), user_id);
                startActivity(navigation_intent);
            }
        });
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
