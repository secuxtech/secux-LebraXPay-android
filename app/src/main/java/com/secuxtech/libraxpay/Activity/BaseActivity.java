package com.secuxtech.libraxpay.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.secuxtech.libraxpay.Dialog.CommonAlertDialog;
import com.secuxtech.libraxpay.R;
import com.secuxtech.libraxpay.Utility.CommonProgressDialog;

import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "SecuXEvPay";

    protected boolean               mShowBackButton = true;
    protected boolean               mShowLogo = true;
    protected Context               mContext = this;
    protected CommonAlertDialog     mAlertDialog = new CommonAlertDialog();

    //@SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_base);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorBlack)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorBlack));

        /*
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.layout_secux_logo_imageview, null);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorTitle)));
            actionBar.setDisplayHomeAsUpEnabled(mShowBackButton);
            if (mShowLogo) {

                actionBar.setDisplayShowCustomEnabled(true);
                actionBar.setTitle("");
                actionBar.setCustomView(v);
            } else {
                actionBar.setDisplayShowCustomEnabled(false);
                actionBar.setTitle("");
                actionBar.hide();
            }
        }

         */



    }

    @Override
    protected void onResume(){
        super.onResume();

        if (!checkWifi()){
            showAlert("No network!", "Please check the phone's network setting");
            //LogHandler.Log("No network!");
        }
    }

    protected boolean checkWifi(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected void showMessageInMain(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showProgress(String info){
        CommonProgressDialog.showProgressDialog(mContext, info);
    }

    public void showProgressInMain(String info){
        final String msgInfo = info;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonProgressDialog.showProgressDialog(mContext, msgInfo);
            }
        });
    }

    public void hideProgressInMain(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonProgressDialog.dismiss();
            }
        });
    }

    public void showLoginWndInMain(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(mContext, "Login timeout! Please login again!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

                Intent newIntent = new Intent(mContext, LoginActivity.class);
                startActivity(newIntent);
                return;
            }
        });
    }

    public void checkBluetoothSetting(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast toast = Toast.makeText(mContext, "The phone DOES NOT support bluetooth! APP will terminate!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            finish();
            return;
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            Toast toast = Toast.makeText(mContext, "Please turn on Bluetooth!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    protected void showAlert(String title, String msg){
        mAlertDialog = new CommonAlertDialog();
        mAlertDialog.showAlert(mContext, title, msg);
    }

    protected void showAlertInMain(final String title, final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAlert(title, msg);
            }
        });
    }

    protected void showAlert(String title, String msg, String cancelTitle, String okTitle){
        mAlertDialog.showAlert(mContext, title, msg, cancelTitle, okTitle);
    }

    protected void showAlertInMain(final String title, final String msg, final String cancelTitle, final String okTitle){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAlert(title, msg, cancelTitle, okTitle);
            }
        });
    }

    public void onBackButtonClick(View v){
        super.onBackPressed();
    }

    public void onOKButtonClick(View v){
        mAlertDialog.dismiss();
    }

    public void onCancelButtonClick(View v){
        mAlertDialog.dismiss();
    }

    public String getStoreAppVersion(String packageName) throws IOException {
        String version = null;
        Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName + "&hl=en").timeout(5000).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").referrer("http://www.google.com").get();

        Elements infoDivs = doc.select("div.hAyfc");
        Iterator infoDivsIterator = infoDivs.iterator();

        Element contentDiv;
        Element titleDiv;
        Elements verionDivs;
        Iterator verionDivsIterator;
        String title;
        while(infoDivsIterator.hasNext()) {
            contentDiv = (Element)infoDivsIterator.next();
            titleDiv = contentDiv.select("div.BgcNfc").first();
            verionDivs = contentDiv.select("span.htlgb");
            verionDivsIterator = verionDivs.iterator();
            title = null;
            if (titleDiv != null) {
                title = titleDiv.ownText();
            }

            if (title != null && title.equals("Current Version")) {
                while(verionDivsIterator.hasNext()) {
                    Element verionDiv = (Element)verionDivsIterator.next();
                    version = verionDiv.ownText();
                    if (!version.isEmpty()) break;
                }
                break;
            }
        }
        return version;
    }
}
