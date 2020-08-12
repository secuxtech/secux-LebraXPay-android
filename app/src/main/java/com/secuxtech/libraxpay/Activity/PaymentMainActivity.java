package com.secuxtech.libraxpay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Pair;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.secuxtech.libraxpay.BuildConfig;
import com.secuxtech.libraxpay.Model.Setting;
import com.secuxtech.libraxpay.R;
import com.secuxtech.libraxpay.Utility.CommonProgressDialog;
import com.secuxtech.paymentkit.SecuXCoinAccount;
import com.secuxtech.paymentkit.SecuXPaymentManager;
import com.secuxtech.paymentkit.SecuXServerRequestHandler;

import org.json.JSONObject;

public class PaymentMainActivity extends BaseActivity {

    private final Context mContext = this;
    private IntentIntegrator mScanIntegrator;

    private NfcAdapter      mNfcAdapter;
    //private PendingIntent   mPendingIntent = null;
    //private boolean         mProcessNFCTag = false;

    SecuXPaymentManager mPaymentManager = new SecuXPaymentManager();

    public static final String PAYMENT_INFO = "com.secux.libraxpay.PAYMENTINFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mShowBackButton = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check

            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        BottomNavigationView navigationView = findViewById(R.id.navigation_main);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        MenuItem menuItem = navigationView.getMenu().getItem(1).setChecked(true);

        if (BuildConfig.DEBUG && Setting.getInstance().mTestModel){
            return;
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (null == mNfcAdapter) {
            Toast toast = Toast.makeText(mContext, "No NFC support!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            //finish();
            //return;
        }else {

            if (!mNfcAdapter.isEnabled()) {
                Toast toast = Toast.makeText(mContext, "Please turn on NFC!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //finish();
                //return;
            }
        }

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

        } else {
            // Bluetooth is enabled
            if (Setting.getInstance().mPaymentNFCInfo.length()>0){
                handlePaymentInfo(Setting.getInstance().mPaymentNFCInfo);
            }else {
                processIntent(getIntent());
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        /*
        if (mPendingIntent == null) {
            mPendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        }

        if (BuildConfig.DEBUG && Setting.getInstance().mTestModel) {
            return;
        }

        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }

         */
    }

    public void onPause() {
        super.onPause();
        //if (mNfcAdapter != null) {
        //    mNfcAdapter.disableForegroundDispatch(this);
        //}
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            processIntent(intent);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_main_accounts:
                    Intent newIntent = new Intent(mContext, CoinAccountListActivity.class);
                    startActivity(newIntent);
                    return true;

                case R.id.navigation_main_payment:
                    return true;

                case R.id.navigation_main_userinfo:
                    Intent userInfoIntent = new Intent(mContext, UserInfoActivity.class);
                    startActivity(userInfoIntent);
                    return true;
            }
            return false;
        }

    };

    private void processIntent(final Intent intent) {
        Log.i(TAG, "processIntent");
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag==null){
            Log.i(TAG, "Empty tag process abort!");
            return;
        }

        //mProcessNFCTag = true;
        Ndef ndef = Ndef.get(tag);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < rawMessages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
                //NdefRecord[] record = messages[i].getRecords();

                String amount="", devid="", cointype="", token="";
                for (final NdefRecord record : messages[i].getRecords()) {
                    byte[] payload = record.getPayload();
                    String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                    int languageCodeLength = payload[0] & 0077;

                    try {
                        String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
                        String text = new String(payload, languageCodeLength + 1,
                                payload.length - languageCodeLength - 1, textEncoding);

                        //{"amount":"11.5", "coinType":"DCT", "token":"SPC","deviceIDhash":"04793D374185C2167A420D250FFF93F05156350C"}

                        Log.i(TAG, text);

                        if (text.contains("{") && text.contains("}")) {
                            showProgressInMain("Parsing...");
                            Setting.getInstance().mPaymentNFCInfo = text;
                            handlePaymentInfo(text);
                            hideProgressInMain();
                            return;
                        }
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }

        /*
        try {
            ndef.close();
            ndef.connect();
            NdefMessage messages = ndef.getNdefMessage();

            String amount="", devid="", cointype="", token="";
            for (final NdefRecord record : messages.getRecords()) {
                byte[] payload = record.getPayload();
                String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                int languageCodeLength = payload[0] & 0077;
                String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
                String text = new String(payload, languageCodeLength + 1,
                        payload.length - languageCodeLength - 1, textEncoding);

                //{"amount":"11.5", "coinType":"DCT", "token":"SPC","deviceIDhash":"04793D374185C2167A420D250FFF93F05156350C"}

                Log.i(TAG, text);

                if (text.contains("{") && text.contains("}")) {
                    showProgressInMain("Parsing...");
                    Setting.getInstance().mPaymentNFCInfo = text;
                    handlePaymentInfo(text);
                    hideProgressInMain();
                    return;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

            //mProcessNFCTag = false;

        } finally {
            try {
                ndef.close();
            } catch (Exception e) {
                Log.e(TAG, "close ndef failed! " + e.getLocalizedMessage());
            }
        }


         */
    }

    public void onScanQRCodeButtonClick(View v)
    {
        if (!this.checkWifi()){
            showAlert("No network!", "Please check the phone's network setting");
            return;
        }

        if (BuildConfig.DEBUG && Setting.getInstance().mTestModel) {
            handlePaymentInfo("{\"amount\":\"7\", \"coinType\":\"DCT:SPC\",\"deviceIDhash\":\"f962639145992d7a710d33dcca503575eb85d759\"}");
            return;
        }

        mScanIntegrator = new IntentIntegrator(PaymentMainActivity.this);
        mScanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        mScanIntegrator.setPrompt("Start scan ...");
        mScanIntegrator.setTimeout(30000);
        mScanIntegrator.setCaptureActivity(ScanQRCodeActivity.class);
        mScanIntegrator.initiateScan();
    }

    public void onHistoryButtonClick(View v){
        if (!this.checkWifi()){
            showAlert("No network!", "Please check the phone's network setting");
            return;
        }

        if (Setting.getInstance().mTestSessionTimeout){
            showLoginWndInMain();
            return;
        }

        Intent newIntent = new Intent(mContext, PaymentHistoryActivity.class);
        startActivity(newIntent);
    }

    public void handleRefund(final String payinfo, String devID){

        try {
            JSONObject infoJson = new JSONObject(payinfo);
            String amount = infoJson.getString("refund");
            int nAmount = Integer.valueOf(amount);
            String devIDHash = infoJson.getString("deviceIDhash");
            String coinToken = infoJson.getString("coinType");
            String[] coinTokenArr = coinToken.split(":");
            String coin = coinTokenArr[0];
            String token = coinTokenArr[1];



            if (coin.length() > 0 && token.length() > 0 && nAmount > 0 && devIDHash.length() > 0 ) {
                Intent newIntent = new Intent(mContext, RefundActivity.class);
                newIntent.putExtra(RefundActivity.REFUND_AMOUNT, nAmount);
                newIntent.putExtra(RefundActivity.REFUND_TOKEN, token);
                newIntent.putExtra(RefundActivity.REFUND_COIN, coin);
                newIntent.putExtra(RefundActivity.REFUND_DEVIDHASH, devIDHash);
                newIntent.putExtra(RefundActivity.REFUND_DEVID, devID);
                startActivity(newIntent);
                return;
            }


        }catch (Exception e){
            e.printStackTrace();

        }

        showAlertInMain("Invalid refund info!", "");


    }

    public void handleRefill(final String payinfo, String devID){
        try {
            JSONObject infoJson = new JSONObject(payinfo);
            String amount = infoJson.getString("refill");
            int nAmount = Integer.valueOf(amount);
            String devIDHash = infoJson.getString("deviceIDhash");
            String coinToken = infoJson.getString("coinType");
            String[] coinTokenArr = coinToken.split(":");
            String coin = coinTokenArr[0];
            String token = coinTokenArr[1];


            if (coin.length() > 0 && token.length() > 0 && nAmount > 0 && devIDHash.length() > 0 ) {
                Intent newIntent = new Intent(mContext, RefillActivity.class);
                newIntent.putExtra(RefillActivity.REFILL_AMOUNT, nAmount);
                newIntent.putExtra(RefillActivity.REFILL_TOKEN, token);
                newIntent.putExtra(RefillActivity.REFILL_COIN, coin);
                newIntent.putExtra(RefillActivity.REFILL_DEVIDHASH, devIDHash);
                newIntent.putExtra(RefillActivity.REFILL_DEVID, devID);
                startActivity(newIntent);
                return;
            }


        }catch (Exception e){
            e.printStackTrace();

        }

        showAlertInMain("Invalid refund info!", "");
    }

    public void handlePaymentInfo(final String payinfo){
        boolean refundFlag=false, refillFlag=false;
        String nonce = "";
        try {
            JSONObject infoJson = new JSONObject(payinfo);
            String refundAmount = infoJson.optString("refund");
            String refillAmount = infoJson.optString("refill");
            String devIDHash = infoJson.getString("deviceIDhash");
            nonce = infoJson.getString("nonce");

            if (refundAmount.length() > 0){
                //handleRefund(payinfo);
                //return;
                refundFlag = true;

                if (!Setting.getInstance().mEnableRefundFlag){
                    showMessageInMain("Invalid QRCode!");
                    return;
                }

            }else if (refillAmount.length() > 0){
                //handleRefill(payinfo);
                //return;
                refillFlag = true;

                if (!Setting.getInstance().mEnableRefillFlag){
                    showMessageInMain("Invalid QRCode!");
                    return;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            //return;

        }

        final boolean refundOpt = refundFlag, refillOpt = refillFlag;
        final String payNonce = nonce;
        showProgressInMain("");
        new Thread(new Runnable() {
            @Override
            public void run() {

                Pair<Integer, String> ret = mPaymentManager.getDeviceInfo(payinfo);
                hideProgressInMain();
                if (ret.first== SecuXServerRequestHandler.SecuXRequestUnauthorized){
                    showLoginWndInMain();
                    return;

                }else if (ret.first==SecuXServerRequestHandler.SecuXRequestFailed){

                    if (ret.second.contains("No token")){
                        showLoginWndInMain();
                        return;
                    }

                    if (refillOpt)
                        showMessageInMain("Invalid refill information!");
                    else if (refundOpt)
                        showMessageInMain("Invalid refund information!");
                    else
                        showMessageInMain("Invalid payment information!");
                    Setting.getInstance().mPaymentNFCInfo = "";
                    return;
                }


                Setting.getInstance().mPaymentNFCInfo = "";

                try{
                    String amount = "0", coinType = "DCT", token = "SPC";
                    final String payInfoReply = ret.second;
                    final boolean showAccountSelection;

                    JSONObject payinfoJson = new JSONObject(payInfoReply);
                    if (payinfoJson.has("amount") && payinfoJson.getString("amount").compareTo("null")!=0) {
                        amount = payinfoJson.getString("amount");
                    }

                    String coinTypeInfo = "";
                    if (payinfoJson.has("coinType") && payinfoJson.getString("coinType").compareTo("null")!=0) {
                        coinTypeInfo = payinfoJson.getString("coinType");

                        if (coinTypeInfo.contains(":")){
                            coinType = coinTypeInfo.substring(0, coinTypeInfo.indexOf(':'));
                        }else{
                            coinType = coinTypeInfo;
                        }

                        showAccountSelection = false;
                    }else{
                        showAccountSelection = true;
                    }

                    if (Setting.getInstance().mShowLBRAccountOnlyFlag && coinType.compareToIgnoreCase("LBR")!=0){
                        showMessageInMain("Invalid coin type!");
                        return;
                    }

                    if (payinfoJson.has("token") && payinfoJson.getString("token").compareTo("null")!=0) {
                        token = payinfoJson.getString("token");
                    }else if (coinTypeInfo.contains(":")){
                        token = coinTypeInfo.substring(coinTypeInfo.indexOf(':')+1);
                    }

                    final String devID = payinfoJson.getString("deviceID");
                    final String devIDHash = payinfoJson.getString("deviceIDhash");

                    SecuXCoinAccount coinAcc = Setting.getInstance().mAccount.getCoinAccount(coinType);

                    if (devID.length()==0){
                        showMessageInMain("Unsupported device!");
                        return;
                    }

                    if (coinAcc == null){
                        showMessageInMain("Unsupported Coin Type!");
                        return;
                    }else if (coinAcc.getBalance(token)==null){
                        showMessageInMain("Unsupported Token Type!");
                        return;
                    }


                    if (refillOpt){
                        handleRefill(payinfo, devID);
                        return;
                    }else if (refundOpt){
                        handleRefund(payinfo, devID);
                        return;
                    }


                    final String payAmount = amount;
                    final String payCoinType = coinType;
                    final String payToken = token;


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getApplicationContext(),"Scan result: "+scanContent, Toast.LENGTH_LONG).show();
                            Intent newIntent = new Intent(mContext, PaymentDetailsActivity.class);
                            newIntent.putExtra(PAYMENT_INFO, payInfoReply);
                            newIntent.putExtra(PaymentDetailsActivity.PAYMENT_AMOUNT, payAmount);
                            newIntent.putExtra(PaymentDetailsActivity.PAYMENT_COINTYPE, payCoinType);
                            newIntent.putExtra(PaymentDetailsActivity.PAYMENT_TOKEN, payToken);
                            newIntent.putExtra(PaymentDetailsActivity.PAYMENT_DEVID, devID);
                            newIntent.putExtra(PaymentDetailsActivity.PAYMENT_DEVIDHASH, devIDHash);
                            newIntent.putExtra(PaymentDetailsActivity.PAYMENT_SHOWACCOUNTSEL, showAccountSelection);
                            newIntent.putExtra(PaymentDetailsActivity.PAYMENT_NONCE, payNonce);
                            startActivity(newIntent);
                        }
                    });
                }catch (Exception e){
                    showMessageInMain("Invalid payment information!");
                    return;
                }


            }
        }).start();

    }

    //Callback when scan done
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null && scanningResult.getContents() != null)
        {
            final String scanContent = scanningResult.getContents();
            if (scanContent.length() > 0)
            {
                showProgressInMain("Parsing...");
                handlePaymentInfo(scanContent);
                hideProgressInMain();

                return;
            }


                /*
                String amount;
                @SecuXCoinType.CoinType String coinType;
                try{
                    JSONObject payinfoJson = new JSONObject(scanContent);
                    amount = payinfoJson.getString("amount");
                    coinType = payinfoJson.getString("coinType");
                    String devid = payinfoJson.getString("deviceID");

                    if (Wallet.getInstance().getAccount(coinType) == null){
                        Toast toast = Toast.makeText(mContext, "Unsupported Coin Type!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        return;
                    }

                }catch (Exception e){
                    Toast toast = Toast.makeText(mContext, "Invalid QRCode!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    return;
                }


                //Toast.makeText(getApplicationContext(),"Scan result: "+scanContent, Toast.LENGTH_LONG).show();
                Intent newIntent = new Intent(this, PaymentDetailsActivity.class);
                newIntent.putExtra(PAYMENT_INFO, scanContent);
                newIntent.putExtra(PaymentDetailsActivity.PAYMENT_AMOUNT, amount);
                newIntent.putExtra(PaymentDetailsActivity.PAYMENT_COINTYPE, coinType);
                startActivity(newIntent);
                return;

                 */

                /*
                String amountStr = "10 IFC";

                Intent newIntent = new Intent(mContext, PaymentResultActivity.class);
                newIntent.putExtra(PaymentDetailsActivity.PAYMENT_RESULT, false);
                newIntent.putExtra(PaymentDetailsActivity.PAYMENT_STORENAME, "My test Store");
                newIntent.putExtra(PaymentDetailsActivity.PAYMENT_AMOUNT, amountStr);
                startActivity(newIntent);

                 */


        }

        super.onActivityResult(requestCode, resultCode, intent);
        Toast.makeText(getApplicationContext(),"Scan failed!!",Toast.LENGTH_LONG).show();

    }
}
