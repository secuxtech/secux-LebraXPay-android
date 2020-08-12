package com.secuxtech.libraxpay.Activity;


import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import android.util.Pair;

import com.secuxtech.libraxpay.Model.Setting;
import com.secuxtech.libraxpay.R;
import com.secuxtech.libraxpay.Utility.CommonProgressDialog;
import com.secuxtech.paymentkit.SecuXAccountManager;
import com.secuxtech.paymentkit.SecuXServerRequestHandler;

public class ChangePasswordActivity extends BaseActivity {

    private EditText mEdittextOldPwd;
    private EditText mEdittextPwd;
    private EditText mEdittextConfirmPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mEdittextOldPwd = findViewById(R.id.editText_changepwd_old);
        mEdittextPwd = findViewById(R.id.editText_changepwd_new);
        mEdittextConfirmPwd = findViewById(R.id.editText_changepwd_confirmnew);
    }

    public void onChangeButtonClick(View v){
        if (!this.checkWifi()){
            showAlert("No network!", "Please check the phone's network setting");
            return;
        }

        if (mEdittextOldPwd.getText().length()<6){

            showAlert("Change password failed!", "Invalid old password!");
            return;
        }

        if (mEdittextPwd.getText().length()<6){

            showAlert("Change password failed!", "Invalid new password length! Password must have 6~18 characteristics.");
            return;
        }

        if (mEdittextPwd.getText().toString().compareTo(mEdittextConfirmPwd.getText().toString())!=0){

            showAlert("Change password failed!", "New password DOES NOT match!");
            return;
        }

        CommonProgressDialog.showProgressDialog(this, "In Progress ...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SecuXAccountManager accMgr = new SecuXAccountManager();
                final Pair<Integer, String> ret = accMgr.changePassword(mEdittextOldPwd.getText().toString(), mEdittextPwd.getText().toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonProgressDialog.dismiss();

                        if (ret.first == SecuXServerRequestHandler.SecuXRequestOK){
                            showAlert("Password changed!", "");
                            Setting.getInstance().mAccount.mPassword = mEdittextPwd.getText().toString();
                            finish();

                        }else{

                            showAlert("Change password failed!", ret.second);
                        }
                    }
                });


            }
        }).start();
    }


}
