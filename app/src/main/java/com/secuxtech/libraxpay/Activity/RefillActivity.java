package com.secuxtech.libraxpay.Activity;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.secuxtech.libraxpay.R;
import com.secuxtech.libraxpay.Utility.AccountUtil;
import com.secuxtech.paymentkit.SecuXPaymentManager;
import com.secuxtech.paymentkit.SecuXServerRequestHandler;
import com.secuxtech.paymentkit.SecuXStoreInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RefillActivity extends BaseActivity {

    public static final String REFILL_RESULT = "com.secux.libraxpay.REFILLRESULT";
    public static final String REFILL_STORENAME = "com.secux.libraxpay.REFILLSTORENAME";
    public static final String REFILL_DATE = "com.secux.libraxpay.REFILLDATE";
    public static final String REFILL_ERROR = "com.secux.libraxpay.REFILLERROR";

    public static final String REFILL_AMOUNT = "com.secux.libraxpay.AMOUNT";
    public static final String REFILL_COIN = "com.secux.libraxpay.COINT";
    public static final String REFILL_TOKEN = "com.secux.libraxpay.TOKEN";
    public static final String REFILL_DEVIDHASH = "com.secux.libraxpay.DEVIDHASH";
    public static final String REFILL_DEVID = "com.secux.libraxpay.DEVID";

    private Integer mAmount = 0;
    private String mCoin = "";
    private String mToken = "";
    private String mHashDevID = "";
    private String mDevID = "";

    private String mStoreName = "N/A";
    private SecuXPaymentManager mPaymentManager = new SecuXPaymentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refill);

        Intent intent = getIntent();
        mAmount = intent.getIntExtra(REFILL_AMOUNT, 0);
        mCoin = intent.getStringExtra(REFILL_COIN);
        mToken = intent.getStringExtra(REFILL_TOKEN);
        mHashDevID = intent.getStringExtra(REFILL_DEVIDHASH);
        mDevID = intent.getStringExtra(REFILL_DEVID);

        TextView textviewAmount = findViewById(R.id.editText_paymentinput_amount);
        textviewAmount.setText("" + mAmount);
        textviewAmount.setFocusable(false);

        ImageView payinputLogo = findViewById(R.id.imageView_paymentinput_coinlogo);
        payinputLogo.setImageResource(AccountUtil.getCoinLogo(mCoin));

        TextView textviewPaymentType = findViewById(R.id.textView_paymentinput_coinname);
        textviewPaymentType.setText(mToken);


        new Thread(new Runnable() {
            @Override
            public void run() {
                Pair<Pair<Integer, String>, SecuXStoreInfo> storeInfoRet = mPaymentManager.getStoreInfo(mHashDevID);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBar progressbar = findViewById(R.id.progressBar_get_store_info);
                        progressbar.setVisibility(View.INVISIBLE);

                        if (storeInfoRet.second!=null){
                            LinearLayout layoutStore = findViewById(R.id.cardView_store);
                            TextView tvStoreName = findViewById(R.id.textView_selected_store_name);
                            ImageView ivStoreLogo = findViewById(R.id.imageView_selected_store_logo);
                            tvStoreName.setText(storeInfoRet.second.mName);
                            ivStoreLogo.setImageBitmap(storeInfoRet.second.mLogo);
                            layoutStore.setVisibility(View.VISIBLE);

                            mStoreName = storeInfoRet.second.mName;
                        }else{
                            TextView tvNoStore = findViewById(R.id.textView_refund_no_store);
                            tvNoStore.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        }).start();
    }

    public void onRefillButtonClick(View v){
        if (!this.checkWifi()){
            showAlert("No network!", "Please check the phone's network setting");
            return;
        }

        showProgress("Refill...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Pair<Integer, String>  ret = mPaymentManager.doRefill(mContext, mDevID, mHashDevID);
                hideProgressInMain();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(500);
                        }

                        MediaPlayer mediaPlayer = new MediaPlayer();
                        AssetFileDescriptor afd;

                        boolean refundRet = ret.first == SecuXServerRequestHandler.SecuXRequestOK;
                        if (refundRet){
                            afd = getResources().openRawResourceFd(R.raw.paysuccess);
                        }else{
                            afd = getResources().openRawResourceFd(R.raw.payfailed);
                        }

                        try {
                            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            afd.close();
                            mediaPlayer.prepare();
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();


                        SimpleDateFormat simpleDateFormat =
                                new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                        Date date = Calendar.getInstance().getTime();
                        String dateStr = simpleDateFormat.format(date);

                        String amountStr = mAmount.toString() + " " + mToken;

                        Intent newIntent = new Intent(mContext, RefillResultActivity.class);
                        newIntent.putExtra(REFILL_RESULT, refundRet);
                        newIntent.putExtra(REFILL_STORENAME, mStoreName);
                        newIntent.putExtra(REFILL_AMOUNT, amountStr);
                        newIntent.putExtra(REFILL_DATE, dateStr);
                        newIntent.putExtra(REFILL_ERROR, ret.second);
                        newIntent.putExtra(REFILL_TOKEN, mToken);
                        newIntent.putExtra(REFILL_COIN, mCoin);
                        startActivity(newIntent);

                        finish();
                    }
                });

            }
        }).start();
    }
}
