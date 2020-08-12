package com.secuxtech.libraxpay.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;


import com.secuxtech.paymentkit.SecuXPaymentHistory;
import com.secuxtech.paymentkit.SecuXUserAccount;

import java.util.ArrayList;


import static android.content.Context.MODE_PRIVATE;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-20
 */
public class Setting {
    private static final Setting ourInstance = new Setting();
    public static Setting getInstance() {
        return ourInstance;
    }

    public SecuXUserAccount mAccount = null;
    public SecuXPaymentHistory mLastPaymentHis = null;

    public String mUserAccountName = "";
    public String mUserAccountPwd = "";
    public boolean mUserLogout = false;

    public boolean mEnableRefundFlag = true;
    public boolean mEnableRefillFlag = false;
    public boolean mShowLBRAccountOnlyFlag = true;

    public ArrayList<Pair<String, String>> mCoinTokenArray = new ArrayList<>();
    public boolean mTestModel = false;
    public boolean mTestSessionTimeout = false;


    public String mPaymentNFCInfo = "";

    public boolean mLogToFile = false;

    private Setting() {
        if (mShowLBRAccountOnlyFlag){
            mCoinTokenArray.add(new Pair<>("LBR", "LBR"));
        }
    }

    public void saveSettings(Context context){
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("SecuXEvPay", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserAccountName", mUserAccountName);
        editor.putString("UserAccountPwd", mUserAccountPwd);
        editor.apply();
    }

    public void loadSettings(Context context){
        SharedPreferences settings = context.getApplicationContext().getSharedPreferences("SecuXEvPay", MODE_PRIVATE);
        mUserAccountName = settings.getString("UserAccountName", "");
        mUserAccountPwd = settings.getString("UserAccountPwd", "");
    }

}
