package com.secuxtech.libraxpay.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.secuxtech.libraxpay.Model.CoinTokenAccount;
import com.secuxtech.libraxpay.Model.Setting;
import com.secuxtech.libraxpay.R;
import com.secuxtech.libraxpay.Utility.AccountUtil;

import org.json.JSONObject;

import java.math.BigDecimal;


public class ReceiveActivity extends BaseActivity {

    static CoinTokenAccount mAccount;
    ImageView mImageViewQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        mImageViewQRCode = findViewById(R.id.imageView_account_qrcode);


        ImageView imageviewLogo = findViewById(R.id.imageView_account_coinlogo);
        imageviewLogo.setImageResource(AccountUtil.getCoinLogo(mAccount.mCoinType));

        TextView textviewName = findViewById(R.id.textView_account_name);
        textviewName.setText(mAccount.mAccountName);

        TextView textviewBalance = findViewById(R.id.textView_account_balance);
        textviewBalance.setText(mAccount.mBalance.mFormattedBalance.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " " + mAccount.mToken);

    }

    public void onResume() {
        super.onResume();

        generateQRCodeImage();
    }


    private void generateQRCodeImage(){
        //{"receiver":"maochuntest1@secuxtech.com", "coin":"DCT", "token":"SPC", "account":""}
        try{
            JSONObject accInfoJson = new JSONObject();
            accInfoJson.put("account", mAccount.mAccountName);
            accInfoJson.put("coin", mAccount.mCoinType);
            accInfoJson.put("token", mAccount.mToken);
            accInfoJson.put("receiver", Setting.getInstance().mAccount.mAccountName);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(accInfoJson.toString(), BarcodeFormat.QR_CODE,350,350);
            mImageViewQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
