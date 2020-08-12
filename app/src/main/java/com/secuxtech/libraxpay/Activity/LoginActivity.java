package com.secuxtech.libraxpay.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.PendingIntent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;

import com.secuxtech.libraxpay.Fragment.LoginFragment;
import com.secuxtech.libraxpay.R;


public class LoginActivity extends BaseActivity {

    private PendingIntent mPendingIntent = null;
    private NfcAdapter nfcAdapter = null;

    private LoginFragment mLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mShowBackButton = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = this.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }



        FragmentManager fm = getSupportFragmentManager();
        //mLoginFragment = fm.findFragmentByTag("fragment_login");
        //if (mLoginFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            mLoginFragment =new LoginFragment();
            mLoginFragment.setAlertDialog(mAlertDialog);
            //ft.add(android.R.id.content,mLoginFragment,"fragment_login");
            ft.add(R.id.llayout_login,mLoginFragment,"fragment_login");
            ft.commit();
        //}

    }

}
