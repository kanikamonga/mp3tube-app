package com.example.android.mp3tube;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by abcplusd-Mac1 on 27/02/18.
 */

public class MyWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {


        return false;

    }
}
