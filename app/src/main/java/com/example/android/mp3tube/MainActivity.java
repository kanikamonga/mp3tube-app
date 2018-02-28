package com.example.android.mp3tube;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Context context = this;
    private String url = "http://mp3tube.imabhi.in/";
    private WebView myWebView;

    private static final int PERMISSION_REQUEST_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.mp3tube.R.layout.activity_main);
        myWebView = (WebView) findViewById(com.example.android.mp3tube.R.id.webview);
        myWebView.loadUrl(url);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        isPermissionExist();
        downloadFile();
    }



    public void downloadFile() {
        myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                if (isPermissionExist()) {
                    startDownload(url, contentDisposition, mimeType);
                }
            }
        });
    }


    public void startDownload(String url, String contentDisposition, String mimeType) {
        String title = "";
        if (url.contains("mime=video")) {
            mimeType = "mp4";
            title = url.split("title=")[1];
        } else {
            title = URLUtil.guessFileName(url, contentDisposition, mimeType);
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url.concat(mimeType)));
        String cookies = CookieManager.getInstance().getCookie(url);
        request.setDescription("Downloading file...");


        request.setTitle(title);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        dm.enqueue(request);
        Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();

    }


    public boolean isPermissionExist() {
        // Check if the  permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is missing and must be requested.
            requestStoragePermission();
            return false;

        } else {
            return true;
        }
    }

    /**
     * Requests the {@link android.Manifest.permission} permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private void requestStoragePermission() {
        // Permission has not been granted and must be requested.
        // Request the permission
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_STORAGE);
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
            } else {
                Snackbar.make(myWebView, " permission was not granted.",
                        Snackbar.LENGTH_SHORT).show();

            }
            // END_INCLUDE(permission_result)
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}

