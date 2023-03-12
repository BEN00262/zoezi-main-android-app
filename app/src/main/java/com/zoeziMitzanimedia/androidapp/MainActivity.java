package com.zoeziMitzanimedia.androidapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
//import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;
//import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.mikhaellopez.ratebottomsheet.AskRateBottomSheet;
import com.mikhaellopez.ratebottomsheet.RateBottomSheet;
import com.mikhaellopez.ratebottomsheet.RateBottomSheetManager;
import com.zoeziMitzanimedia.androidapp.OTPVerification.OTPMainActivity;
import com.zoeziMitzanimedia.androidapp.databinding.ActivityMainBinding;

//import okhttp3.OkHttpClient;


// REFACTOR THIS CODE LATER THE DESIGN IS NOT APPEALING
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    WebView zoezi_page;
    SpinKitView progressBar;
    Sprite doubleBounce;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout linearLayout;
    int MY_UPDATE_REQUEST_CODE = 30;

    ZoeziDownloadInterface zoeziDownloadInterface;
    String blobURL = "";
    int StorageRequestCode = 200;
    ZoeziPreferenceManager zoeziPreferenceManager;
    String previousURL = "";
    CheckInternet checkInternet = new CheckInternet();

//    private final OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

//    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        zoezi_page = binding.loadPage;
        progressBar = binding.spinKit;
        swipeRefreshLayout = binding.swipeRefreshLayout;

        zoeziPreferenceManager = new ZoeziPreferenceManager(this);
        previousURL = zoeziPreferenceManager.getPreviousURL();

        linearLayout =  binding.noInternetParent;

        doubleBounce = new CubeGrid();

        startWebView();

        swipeRefreshLayout.setOnRefreshListener(() -> zoezi_page.reload());

        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this.getApplicationContext());

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            MY_UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
//                    show the toast informing of failed update
                    Toast.makeText(this.getApplicationContext(), "Failed to download update", Toast.LENGTH_LONG);
                }
            }
        });
    }

    @SuppressLint({"ObsoleteSdkInt", "SetJavaScriptEnabled"})
    private void startWebView() {
        if (!DetectInternetConnection.checkInternetConnection(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), NoInternet.class));
            overridePendingTransition(R.anim.right, R.anim.left);
            finish();
            return;
        }

        zoeziDownloadInterface = new ZoeziDownloadInterface(MainActivity.this);

        WebSettings webSettings = zoezi_page.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // set another user agent of ours then
        webSettings.setUserAgentString("Zoezi WebView Android Agent v2");

        // dynamic content display
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // hiding the scrollbar
        zoezi_page.setVerticalScrollBarEnabled(false);

        // disable zooming functionality in the app
        webSettings.setSupportZoom(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);

        webSettings.setLoadsImagesAutomatically(true);

        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        zoezi_page.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        zoezi_page.setScrollbarFadingEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            zoezi_page.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            zoezi_page.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        // downloads files to the local storage
        zoezi_page.setDownloadListener((s, s1, s2, s3, l) -> {
            // first check if their is external_file read permissions before proceeding
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                blobURL = s;
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, StorageRequestCode);
            } else {
                zoezi_page.loadUrl(zoeziDownloadInterface.getBase64StringFromBlobUrl(s));
            }
        });

        zoezi_page.addJavascriptInterface(zoeziDownloadInterface, "Android");

        // work with this here and improve this
        zoezi_page.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // x-zoezi-version

                if (BaseURLs.baseURL.equals(Uri.parse(url).getHost())) {
                    int verified = zoeziPreferenceManager.isVerified();
                    if (BaseURLs.afterLoginURL.equals(url) && (verified == ZoeziStatus.NOT_VERIFIED.ordinal() || verified == ZoeziStatus.INITIAL.ordinal())) {
                        zoeziPreferenceManager.verify();
                    } else if (BaseURLs.verificationURL.equals(url)) {
                        zoeziPreferenceManager.setVerification();


                        startActivity(new Intent(getApplicationContext(), OTPMainActivity.class));
                        overridePendingTransition(R.anim.right,R.anim.left);
                        finish();

                    } else if (BaseURLs.loginURL.equals(url)) {
                        if (!previousURL.isEmpty() && previousURL.equals(BaseURLs.verificationURL) && zoeziPreferenceManager.isVerified() != ZoeziStatus.VERIFIED.ordinal()) {
                            zoeziPreferenceManager.verify();
                        }
                    }
                    previousURL = url;
                    return false;
                }

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                // start showing the rotation icon
                swipeRefreshLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminateDrawable(doubleBounce);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                 return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        int verified = zoeziPreferenceManager.isVerified();
        if (ZoeziStatus.INITIAL.ordinal() == verified || ZoeziStatus.VERIFIED.ordinal() == verified) {
            zoezi_page.loadUrl(BaseURLs.loginURL);
        } else {
            zoezi_page.loadUrl(BaseURLs.verificationURL);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
//                log("Update flow failed! Result code: " + resultCode);
                Toast.makeText(this.getApplicationContext(), "Failed to download update", Toast.LENGTH_LONG);
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        RateBottomSheetManager rateBottomSheetManager = new RateBottomSheetManager(this);
        rateBottomSheetManager
                .setInstallDays(3)
                .setLaunchTimes(5)
                .setRemindInterval(2)
                .setShowAskBottomSheet(true)
                .setShowLaterButton(true)
                .setShowCloseButtonIcon(true)
                .monitor();

        RateBottomSheet.Companion.showRateBottomSheetIfMeetsConditions(this,  new AskRateBottomSheet.ActionListener() {
            @Override
            public void onDislikeClickListener() {

            }

            @Override
            public void onRateClickListener() {

            }

            @Override
            public void onNoClickListener() {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == StorageRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                zoezi_page.loadUrl(zoeziDownloadInterface.getBase64StringFromBlobUrl(blobURL));
            } else {
                Snackbar.make(findViewById(R.id.base_layout), "Write Storage Permission Required to download files", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && zoezi_page.canGoBack()) {
            zoezi_page.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // create the internet detection stuff here
    private class CheckInternet extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DetectInternetConnection.checkInternetConnection(getApplicationContext())) {
                linearLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                swipeRefreshLayout.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                zoezi_page.setNetworkAvailable(false);
            }
        }
    }

//    ;

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(checkInternet, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(checkInternet);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        zoezi_page.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        zoezi_page.restoreState(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!previousURL.isEmpty()) {
            zoeziPreferenceManager.setPreviousURL(previousURL);
        }
        zoezi_page.clearHistory();
        zoezi_page.removeAllViews();
        zoezi_page.destroy();
    }
}