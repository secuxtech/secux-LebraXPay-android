package com.secuxtech.libraxpay.Activity;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;


import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.util.Pair;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.secuxtech.libraxpay.Adapter.CoinAccountListAdapter;
import com.secuxtech.libraxpay.BuildConfig;
import com.secuxtech.libraxpay.Interface.AdapterItemClickListener;
import com.secuxtech.libraxpay.Model.CoinTokenAccount;
import com.secuxtech.libraxpay.Model.Setting;
import com.secuxtech.libraxpay.R;
import com.secuxtech.libraxpay.Utility.AccountUtil;
import com.secuxtech.libraxpay.Utility.CommonProgressDialog;

import com.secuxtech.libraxpay.Utility.biometric.BiometricCallback;
import com.secuxtech.libraxpay.Utility.biometric.BiometricManager;
import com.secuxtech.paymentkit.SecuXAccountManager;
import com.secuxtech.paymentkit.SecuXCoinAccount;
import com.secuxtech.paymentkit.SecuXCoinTokenBalance;
import com.secuxtech.paymentkit.SecuXPaymentHistory;
import com.secuxtech.paymentkit.SecuXPaymentManager;
import com.secuxtech.paymentkit.SecuXPaymentManagerCallback;
import com.secuxtech.paymentkit.SecuXServerRequestHandler;
import com.secuxtech.paymentkit.SecuXStoreInfo;


import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class PaymentDetailsActivity extends BaseActivity {

    public static final String PAYMENT_RESULT = "com.secux.libraxpay.PAYMENTRESULT";
    public static final String PAYMENT_ERROR = "com.secux.libraxpay.PAYMENTERROR";
    public static final String PAYMENT_AMOUNT = "com.secux.libraxpay.AMOUNT";
    public static final String PAYMENT_COINTYPE = "com.secux.libraxpay.COINTYPE";
    public static final String PAYMENT_TOKEN = "com.secux.libraxpay.TOKEN";
    public static final String PAYMENT_DEVID = "com.secux.libraxpay.DEVID";
    public static final String PAYMENT_DEVIDHASH = "com.secux.libraxpay.DEVIDHASH";
    public static final String PAYMENT_STORENAME = "com.secux.libraxpay.STORENAME";
    public static final String PAYMENT_DATE = "com.secux.libraxpay.DATE";
    public static final String PAYMENT_SHOWACCOUNTSEL = "com.secux.libraxpay.SHOWACCOUNTSEL";
    public static final String PAYMENT_NONCE = "com.secux.libraxpay.NONCE";

    public static final int REQUEST_PWD_PROMPT = 1;


    private Context mContext = this;
    private ProgressBar mProgressBar;

    private SecuXPaymentManager mPaymentManager = new SecuXPaymentManager();

    private String mPaymentInfo = ""; //"{\"amount\":\"11\", \"coinType\":\"DCT\", \"deviceID\":\"4ab10000726b\"}";
    private String mAmount = "";
    private String mType = "";
    private String mToken = "";
    private String mDevID = "";
    private String mDevIDhash = "";
    private String mNonce = "";

    private SecuXStoreInfo mStoreInfo = null;

    private SecuXCoinAccount mCoinAccount = null;
    private SecuXCoinTokenBalance mTokenBalance = null;

    //private Timer mMonitorPaymentTimer = new Timer();
    private boolean mAuthenicationScreenShow = false;

    private Dialog mAccountSelDialog;
    private boolean mShowAccountSel = false;

    private Button mButtonPay;

    private BiometricManager mBioManager = null;
    private int mAuthenticationRetryCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check

            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        Setting.getInstance().mLastPaymentHis = null;

        Intent intent = getIntent();
        mPaymentInfo = intent.getStringExtra(PaymentMainActivity.PAYMENT_INFO);
        mAmount = intent.getStringExtra(PAYMENT_AMOUNT);
        mType = intent.getStringExtra(PAYMENT_COINTYPE);
        mToken = intent.getStringExtra(PAYMENT_TOKEN);
        mDevID = intent.getStringExtra(PAYMENT_DEVID);
        mDevIDhash = intent.getStringExtra(PAYMENT_DEVIDHASH);
        mShowAccountSel = intent.getBooleanExtra(PAYMENT_SHOWACCOUNTSEL, false);
        mNonce = intent.getStringExtra(PAYMENT_NONCE);

        mCoinAccount = Setting.getInstance().mAccount.getCoinAccount(mType);
        mTokenBalance = mCoinAccount.getBalance(mToken);

        ImageView imageviewLogo = findViewById(R.id.imageView_account_coinlogo);
        imageviewLogo.setImageResource(AccountUtil.getCoinLogo(mType));

        TextView textviewName = findViewById(R.id.textView_account_name);
        textviewName.setText(Setting.getInstance().mAccount.getCoinAccount(mType).mAccountName);

        TextView textviewAmount = findViewById(R.id.editText_paymentinput_amount);
        if (Double.valueOf(mAmount) > 0.0){
            textviewAmount.setText(mAmount);
            textviewAmount.setFocusable(false);
        }else{
            textviewAmount.setFocusable(true);
            textviewName.requestFocus();
        }

        ImageView payinputLogo = findViewById(R.id.imageView_paymentinput_coinlogo);
        payinputLogo.setImageResource(AccountUtil.getCoinLogo(mType));

        TextView textviewPaymentType = findViewById(R.id.textView_paymentinput_coinname);
        textviewPaymentType.setText(mToken);

        mButtonPay = findViewById(R.id.button_pay);
        mButtonPay.setEnabled(false);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_load_storeinfo);
        mProgressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SecuXAccountManager accMgr = new SecuXAccountManager();

                //if (mShowAccountSel){
                //    accMgr.getAccountBalance(Setting.getInstance().mAccount);
                //}else{
                    accMgr.getAccountBalance(Setting.getInstance().mAccount, mType, mToken);
                //}

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textviewName = findViewById(R.id.textView_account_name);
                        textviewName.setText(Setting.getInstance().mAccount.getCoinAccount(mType).mAccountName);

                        TextView textviewBalance = findViewById(R.id.textView_account_balance);
                        textviewBalance.setText(String.format("%.2f", mTokenBalance.mFormattedBalance) + " " + mToken);

                        //TextView textviewUsdbalance = findViewById(R.id.textView_account_usdbalance);
                        //textviewUsdbalance.setText(String.format("$ %.2f", mTokenBalance.mUSDBalance));


                        CardView cardViewAccount = findViewById(R.id.cardView_account);
                        cardViewAccount.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onClickAccount(v);
                            }
                        });
                    }
                });

                //Must set the callback for the SecuXPaymentManager
                mPaymentManager.setSecuXPaymentManagerCallback(mPaymentMgrCallback);

                //Use SecuXPaymentManager to get store info.
                Pair<Pair<Integer, String>, SecuXStoreInfo> storeInfoRet = mPaymentManager.getStoreInfo(mDevIDhash);
                mStoreInfo = storeInfoRet.second;
                onGetStoreInfoDone(storeInfoRet.first.second);
            }
        }).start();

        EditText edittext = findViewById(R.id.editText_paymentinput_amount);
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    private void onGetStoreInfoDone(String error){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                mProgressBar.setVisibility(View.INVISIBLE);
                if (mStoreInfo != null){
                    //mStoreInfo = storeInfo;

                    TextView textviewStoreName = findViewById(R.id.textView_storename);
                    textviewStoreName.setText(mStoreInfo.mName);

                    ImageView imgviewStoreLogo = findViewById(R.id.imageView_storelogo);
                    imgviewStoreLogo.setVisibility(View.VISIBLE);

                    imgviewStoreLogo.setImageBitmap(mStoreInfo.mLogo);

                    Button buttonPay = findViewById(R.id.button_pay);
                    buttonPay.setEnabled(true);
                }else{
                    ImageView imgviewStoreLogo = findViewById(R.id.imageView_storelogo);
                    imgviewStoreLogo.setVisibility(View.VISIBLE);

                    imgviewStoreLogo.setImageResource(R.drawable.storename_unavailable);

                    //Toast toast = Toast.makeText(mContext, "Get store info. failed!", Toast.LENGTH_LONG);
                    //toast.setGravity(Gravity.CENTER,0,0);
                    //toast.show();

                    showAlert("Get store info. failed!", error);

                    mButtonPay.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorButtonDisabled));

                }
            }
        });
    }

    public void onPayButtonClick(View v){

        if (!this.checkWifi()){
            showAlert("No network!", "Please check the phone's network setting");
            return;
        }

        EditText edittextAmount = findViewById(R.id.editText_paymentinput_amount);
        String strAmount = edittextAmount.getText().toString();
        if (strAmount.length() == 0){

            showAlert("No payment amount!", "Payment abort!");
            return;
        }
        Double payAmount = Double.valueOf(strAmount);
        if (payAmount<=0 || payAmount > mTokenBalance.mFormattedBalance.doubleValue()){

            showAlert("Invalid payment amount!", "Payment abort!");
            return;
        }

        mAmount = strAmount;

        if (BuildConfig.DEBUG && Setting.getInstance().mTestModel){

            SecuXPaymentHistory payhistory = new SecuXPaymentHistory();
            Pair<Integer, String> hisret = mPaymentManager.getPaymentHistory(mToken, "b2a908614bb8484aa8864c6ac0ba709b", payhistory);
            if (hisret.first == SecuXServerRequestHandler.SecuXRequestOK){
                Setting.getInstance().mLastPaymentHis = payhistory;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    SimpleDateFormat simpleDateFormat =
                            new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                    Date date = Calendar.getInstance().getTime();
                    String dateStr = simpleDateFormat.format(date);

                    String amountStr = mAmount.toString() + " " + mToken;

                    Intent newIntent = new Intent(mContext, PaymentResultActivity.class);
                    newIntent.putExtra(PAYMENT_RESULT, true);
                    newIntent.putExtra(PAYMENT_STORENAME, mStoreInfo.mName);
                    newIntent.putExtra(PAYMENT_AMOUNT, amountStr);
                    newIntent.putExtra(PAYMENT_DATE, dateStr);

                    startActivity(newIntent);
                }
            });

            return;
        }

        /*
        mMonitorPaymentTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (CommonProgressDialog.isProgressVisible()){
                    mPaymentManager.cancelPayment();
                    CommonProgressDialog.dismiss();

                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        v.vibrate(500);
                    }

                    MediaPlayer mediaPlayer = new MediaPlayer();
                    AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.payfailed);
                    try {
                        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        afd.close();
                        mediaPlayer.prepare();
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();


                    String amountStr = mAmount + " " + mType;

                    SimpleDateFormat simpleDateFormat =
                            new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                    Date date = Calendar.getInstance().getTime();
                    String dateStr = simpleDateFormat.format(date);

                    Intent newIntent = new Intent(mContext, PaymentResultActivity.class);
                    newIntent.putExtra(PAYMENT_RESULT, false);
                    newIntent.putExtra(PAYMENT_STORENAME, mStoreName);
                    newIntent.putExtra(PAYMENT_AMOUNT, amountStr);
                    newIntent.putExtra(PAYMENT_DATE, dateStr);
                    startActivity(newIntent);
                }
            }
        }, 10000);
        */


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)

            showAlert("Please turn on Bluetooth!", "Payment abort!");
            return;
        }

        try{
            mBioManager = new BiometricManager.BiometricBuilder(this)
                    .setTitle("Pay to " + mStoreInfo.mName)
                    .setSubtitle("LibraXPay")
                    .setDescription("Allow payment with your biometric ID")
                    .setNegativeButtonText("Cancel")
                    .build();

            mBioManager.authenticate(mBiometricCallback);
        }catch (Exception e){
            Log.i(TAG, e.getMessage());
            doPayment();
        }
    }

    private void doPayment(){
        CommonProgressDialog.showProgressDialog(mContext);

        try {
            JSONObject payInfoJson = new JSONObject();
            payInfoJson.put("amount", mAmount);
            payInfoJson.put("coinType", mType);
            payInfoJson.put("token", mToken);
            payInfoJson.put("deviceID", mDevID);
            mPaymentInfo = payInfoJson.toString();

            if (mNonce.length() == 0) {
                mPaymentManager.doPayment(mContext, Setting.getInstance().mAccount, mStoreInfo.mInfo, mPaymentInfo);
            }else {
                mPaymentManager.doPayment(mNonce, mContext, Setting.getInstance().mAccount, mStoreInfo.mInfo, mPaymentInfo);
            }

        }catch (Exception e){

            showAlert("Generate payment data failed!", "Payment abort!");
            return;
        }
    }

    private BiometricCallback mBiometricCallback = new BiometricCallback() {
        @Override
        public void onSdkVersionNotSupported() {
            Log.i(TAG, "onSdkVersionNotSupported");
            showAuthenticationScreen();
        }

        @Override
        public void onBiometricAuthenticationNotSupported() {
            Log.i(TAG, "onBiometricAuthenticationNotSupported");
            showAuthenticationScreen();
        }

        @Override
        public void onBiometricAuthenticationNotAvailable() {
            Log.i(TAG, "onBiometricAuthenticationNotAvailable");
            showAuthenticationScreen();
        }

        @Override
        public void onBiometricAuthenticationPermissionNotGranted() {
            Log.i(TAG, "onBiometricAuthenticationPermissionNotGranted");
            showAuthenticationScreen();
        }

        @Override
        public void onBiometricAuthenticationInternalError(String error) {
            Log.i(TAG, "onBiometricAuthenticationInternalError");
            showAuthenticationScreen();
        }

        @Override
        public void onAuthenticationFailed() {
            Log.i(TAG, "onAuthenticationFailed");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mBioManager!=null){
                        mBioManager.dismissDialog();
                    }
                }
            });

            if (mAuthenticationRetryCount++ < 1){
                mBioManager.authenticate(mBiometricCallback);
            }else {
                showAuthenticationScreen();
            }
        }

        @Override
        public void onAuthenticationCancelled() {
            Log.i(TAG, "onAuthenticationCancelled");
        }

        @Override
        public void onAuthenticationSuccessful() {
            Log.i(TAG, "onAuthenticationSuccessful");
            doPayment();
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            Log.i(TAG, "onAuthenticationHelp");
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            Log.i(TAG, "onAuthenticationError " + errorCode + " " + errString);

            if (errorCode!=5) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mBioManager != null) {
                            mBioManager.dismissDialog();
                        }
                    }
                });

                if (mAuthenticationRetryCount++ < 3) {
                    mBioManager.authenticate(mBiometricCallback);
                } else {
                    showAuthenticationScreen();
                }
            }
        }
    };

    private void showAuthenticationScreen(){
        if (mAuthenicationScreenShow)
            return;

        KeyguardManager km = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        // get the intent to prompt the user
        Intent intent = km.createConfirmDeviceCredentialIntent("SecuX EvPay", "Enter your password to pay");
        // launch the intent
        if (intent != null) {
            startActivityForResult(intent, REQUEST_PWD_PROMPT);
            mAuthenicationScreenShow = true;
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(resultCode, resultCode, data);
        // see if this is being called from our password request..?
        if (requestCode == REQUEST_PWD_PROMPT) {
            // ..it is. Did the user get the password right?
            if (resultCode == RESULT_OK) {
                // they got it right
                doPayment();
            } else {
                // they got it wrong/cancelled
            }
        }
        mAuthenicationScreenShow = false;
    }

    public void onClickAccount(View v){
        if (!mShowAccountSel){
            return;
        }
        Log.i(TAG, "click the account");
        showDialog(this);
    }

    public void showDialog(Activity activity){

        mAccountSelDialog = new Dialog(activity);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mAccountSelDialog.setCancelable(true);
        mAccountSelDialog.setContentView(R.layout.dialog_account_list_selection_layout);

        AdapterItemClickListener mItemClickListener = new AdapterItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CoinTokenAccount account = AccountUtil.getCoinTokenAccounts().get(position);
                Log.i(TAG, account.mAccountName);

                mType = account.mCoinType;
                mToken = account.mToken;

                ImageView imageviewLogo = findViewById(R.id.imageView_account_coinlogo);
                imageviewLogo.setImageResource(AccountUtil.getCoinLogo(account.mCoinType));

                TextView textviewName = findViewById(R.id.textView_account_name);
                textviewName.setText(account.mAccountName);

                TextView textviewBalance = findViewById(R.id.textView_account_balance);
                textviewBalance.setText(String.format("%.2f", mTokenBalance.mFormattedBalance) + " " + mToken);

                //TextView textviewUsdbalance = findViewById(R.id.textView_account_usdbalance);
                //textviewUsdbalance.setText(String.format("$ %.2f", mTokenBalance.mUSDBalance));

                ImageView payinputLogo = findViewById(R.id.imageView_paymentinput_coinlogo);
                payinputLogo.setImageResource(AccountUtil.getCoinLogo(account.mCoinType));

                TextView textviewPaymentType = findViewById(R.id.textView_paymentinput_coinname);
                textviewPaymentType.setText(account.mCoinType);

                mAccountSelDialog.dismiss();

            }
        };

        RecyclerView recyclerView = mAccountSelDialog.findViewById(R.id.recyclerView_accountsel_dialog);
        CoinAccountListAdapter adapterRe = new CoinAccountListAdapter(PaymentDetailsActivity.this, AccountUtil.getCoinTokenAccounts(), mItemClickListener);
        recyclerView.setAdapter(adapterRe);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        mAccountSelDialog.show();

    }

    //Callback for SecuXPaymentManager
    private SecuXPaymentManagerCallback mPaymentMgrCallback = new SecuXPaymentManagerCallback() {

        //Called when payment is completed. Returns payment result and error message.
        @Override
        public void paymentDone(final boolean ret, final String transactionCode, final String errorMsg) {
            if (ret){
                SecuXPaymentHistory payhistory = new SecuXPaymentHistory();
                Pair<Integer, String> hisret = mPaymentManager.getPaymentHistory(mToken, transactionCode, payhistory);
                if (hisret.first == SecuXServerRequestHandler.SecuXRequestOK){
                    Setting.getInstance().mLastPaymentHis = payhistory;
                }else{
                    Log.e(TAG, "Get transaction history from " + transactionCode + " failed!");
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mMonitorPaymentTimer.cancel();
                    CommonProgressDialog.dismiss();

                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        v.vibrate(500);
                    }

                    MediaPlayer mediaPlayer = new MediaPlayer();
                    AssetFileDescriptor afd;

                    SimpleDateFormat simpleDateFormat =
                            new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                    Date date = Calendar.getInstance().getTime();
                    String dateStr = simpleDateFormat.format(date);
                    if (ret){
                        //Toast toast = Toast.makeText(mContext, "Payment successful!", Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER,0,0);
                        //toast.show();

                        //Double usdAmount = Wallet.getInstance().getUSDValue(Double.valueOf(mAmount), mAccount.mCoinType);

                        //PaymentHistoryModel payment = new PaymentHistoryModel(mAccount, mStoreName, dateStr, String.format("%.2f", usdAmount), mAmount);
                        //Wallet.getInstance().addPaymentHistoryItem(payment);


                        afd = getResources().openRawResourceFd(R.raw.paysuccess);


                    }else{

                        //Toast toast = Toast.makeText(mContext, "Payment failed! Error: " + message, Toast.LENGTH_LONG);
                        //toast.setGravity(Gravity.CENTER,0,0);
                        //toast.show();


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

                    String amountStr = mAmount.toString() + " " + mToken;
                    String message = errorMsg;
                    if (message.contains("Scan timeout")){
                        message = "No payment device!";
                    }

                    Intent newIntent = new Intent(mContext, PaymentResultActivity.class);
                    newIntent.putExtra(PAYMENT_RESULT, ret);
                    newIntent.putExtra(PAYMENT_ERROR, message);
                    newIntent.putExtra(PAYMENT_STORENAME, mStoreInfo.mName);
                    newIntent.putExtra(PAYMENT_AMOUNT, amountStr);
                    newIntent.putExtra(PAYMENT_DATE, dateStr);

                    startActivity(newIntent);

                    finish();
                }
            });

        }

        //Called when payment status is changed. Payment status are: "Device connecting...", "DCT transferring..." and "Device verifying..."
        @Override
        public void updatePaymentStatus(final String status){
            Log.i("secux-paymentkit-exp", "Update payment status:" + SystemClock.uptimeMillis() + " " + status);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommonProgressDialog.setProgressTip(status);
                }
            });
        }


        @Override
        public void userAccountUnauthorized(){
            showMessageInMain("User account authorization timeout! Please login again");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent newIntent = new Intent(mContext, LoginActivity.class);
                    startActivity(newIntent);
                }
            });
        }

    };

}
