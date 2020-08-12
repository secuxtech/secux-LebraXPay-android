package com.secuxtech.libraxpay.Activity;



import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.secuxtech.libraxpay.R;
import com.secuxtech.libraxpay.Utility.CommonProgressDialog;

public class TokenTransferDetailsActivity extends BaseActivity {

    public final static String TRANSACTION_HISTORY_DETAIL_URL = "com.secuxtech.libraxpay.TRANSHISDETAILURL";

    private String mDetailUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_transfer_details);

        Intent intent = getIntent();
        mDetailUrl = intent.getStringExtra(TRANSACTION_HISTORY_DETAIL_URL);

        CommonProgressDialog.showProgressDialog(mContext, "Loading...");
        final WebView detailWebView = findViewById(R.id.webView_transfer_details);
        //detailWebView.getSettings().setBuiltInZoomControls(true);
        detailWebView.getSettings().setJavaScriptEnabled(true);
        detailWebView.getSettings().setLoadWithOverviewMode(true);
        detailWebView.getSettings().setUseWideViewPort(true);
        detailWebView.setInitialScale(1);
        detailWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                CommonProgressDialog.dismiss();
                Log.i(TAG, "onPageFinished");
            }
        });

        detailWebView.loadUrl(mDetailUrl);


    }
}
