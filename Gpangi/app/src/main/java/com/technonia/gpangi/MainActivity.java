package com.technonia.gpangi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private WebSettings mWebSettings;
    private ProgressBar mProgressBar;

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

        String url = "http://cms.catchloc.com/";

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mWebView = (WebView)findViewById(R.id.catchloc_webview);
        mWebView.setWebViewClient(new WebViewClient());
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);

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
                mWebView.reload();
            }
        });

        Button btn_stop_location = (Button)findViewById(R.id.btn_setting);
        btn_stop_location.setClickable(true);
        btn_stop_location.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //TODO navigate to settings
                Intent navigation_intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(navigation_intent);
            }
        });
    }
}
