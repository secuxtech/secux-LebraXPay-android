package com.secuxtech.libraxpay.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.secuxtech.libraxpay.Model.Setting;
import com.secuxtech.libraxpay.R;
import com.secuxtech.paymentkit.SecuXPaymentHistory;

import org.w3c.dom.Text;

public class ReceiptActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        SecuXPaymentHistory history = Setting.getInstance().mLastPaymentHis;
        if (history==null){
            return;
        }

        TextView tvTransCode = findViewById(R.id.textview_receipt_transcode);
        tvTransCode.setText(history.mTransactionCode);

        TextView tvStoreName = findViewById(R.id.textview_receipt_storename);
        tvStoreName.setText(history.mStoreName);

        TextView tvTel = findViewById(R.id.textview_receipt_storetel);
        tvTel.setText(history.mStoreTel);

        TextView tvAddr = findViewById(R.id.textview_receipt_storeaddr);
        tvAddr.setText(history.mStoreAddress);

        TextView tvEmail = findViewById(R.id.textview_receipt_useremail);
        tvEmail.setText(history.mUserAccountName);

        String timestamp = history.mTransactionTime;
        TextView tvDate = findViewById(R.id.textview_receipt_date);
        tvDate.setText(timestamp.substring(0, timestamp.indexOf(" ")));

        TextView tvTime = findViewById(R.id.textview_receipt_time);
        tvTime.setText(timestamp.substring(timestamp.indexOf(" ")+1));

        TextView tvAmount = findViewById(R.id.textview_receipt_amount);
        tvAmount.setText(history.mAmount.toString());

        TextView tvToken = findViewById(R.id.textview_receipt_token);
        tvToken.setText(history.mToken);
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
    }

    public void onHisDetailButtonClick(View v){
        Intent newIntent = new Intent(mContext, TokenTransferDetailsActivity.class);
        newIntent.putExtra(TokenTransferDetailsActivity.TRANSACTION_HISTORY_DETAIL_URL, Setting.getInstance().mLastPaymentHis.mDetailsUrl);
        startActivity(newIntent);
    }
}
