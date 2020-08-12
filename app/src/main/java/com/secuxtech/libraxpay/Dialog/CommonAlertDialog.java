package com.secuxtech.libraxpay.Dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.secuxtech.libraxpay.R;
import com.secuxtech.libraxpay.Utility.LogHandler;


/**
 * Created by maochuns.sun@gmail.com on 2020/5/20
 */
public class CommonAlertDialog {

    private AlertDialog mAlertDialog;
    private View mLoadView;

    public void showAlert(Context context, String title, String msg) {

        /*
        if (mAlertDialog != null && mAlertDialog.isShowing())) {
            LogHandler.Log("CommonAlertDialog shows already");
            return;
        }



        if (((Activity)context).isFinishing()){
            LogHandler.Log("show CommonAlertDialog with invalid context");
            return;
        }
 */

        //mAlertDialog = new AlertDialog.Builder(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen).create();
        mAlertDialog = new AlertDialog.Builder(context).create();
        mLoadView = LayoutInflater.from(context).inflate(R.layout.dialog_common_alert, null);
        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mAlertDialog.setView(mLoadView, 0, 0, 0, 0);
        mAlertDialog.setCanceledOnTouchOutside(false);


        TextView tvTitle = mLoadView.findViewById(R.id.textview_common_alert_title);
        TextView tvBody = mLoadView.findViewById(R.id.textview_common_alert_body);

        tvTitle.setText(title);
        tvBody.setText(msg);

        Button buttonCancel = mLoadView.findViewById(R.id.button_common_alert_cancel);
        buttonCancel.setVisibility(View.INVISIBLE);

        Button btnOk = mLoadView.findViewById(R.id.button_common_alert_ok);
        btnOk.setText("OK");

        try {
            mAlertDialog.show();
        }catch (Exception e){
            e.printStackTrace();
            LogHandler.Log("Show CommonAlertDialog exception");
        }
    }

    public void showAlert(Context context, String title, String msg, String cancelBtnTitle, String okBtnTitle) {

        //mAlertDialog = new AlertDialog.Builder(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen).create();
        mAlertDialog = new AlertDialog.Builder(context).create();
        mLoadView = LayoutInflater.from(context).inflate(R.layout.dialog_common_alert, null);
        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mAlertDialog.setView(mLoadView, 0, 0, 0, 0);
        mAlertDialog.setCanceledOnTouchOutside(false);


        TextView tvTitle = mLoadView.findViewById(R.id.textview_common_alert_title);
        TextView tvBody = mLoadView.findViewById(R.id.textview_common_alert_body);

        Button btnCancel = mLoadView.findViewById(R.id.button_common_alert_cancel);
        Button btnOk = mLoadView.findViewById(R.id.button_common_alert_ok);
        btnCancel.setVisibility(View.VISIBLE);

        btnCancel.setText(cancelBtnTitle);
        btnOk.setText(okBtnTitle);

        tvTitle.setText(title);
        tvBody.setText(msg);


        try {
            mAlertDialog.show();
        }catch (Exception e){
            e.printStackTrace();
            LogHandler.Log("Show CommonAlertDialog exception");
        }
    }

    public void dismiss(){
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }
}
