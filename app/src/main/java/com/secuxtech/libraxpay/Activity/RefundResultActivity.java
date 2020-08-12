package com.secuxtech.libraxpay.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.secuxtech.libraxpay.Model.Setting;
import com.secuxtech.libraxpay.R;

public class RefundResultActivity extends BaseActivity  {

    private Context mContext = this;
    private boolean mResult = false;

    private String mCoin = "";
    private String mToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        TextView textViewTitle = findViewById(R.id.textView_payment_result_title);
        textViewTitle.setText("Refund Result");

        Boolean result = getIntent().getExtras().getBoolean(RefundActivity.REFUND_RESULT);
        mResult = result;

        mCoin = getIntent().getStringExtra(RefundActivity.REFUND_COIN);
        mToken = getIntent().getStringExtra(RefundActivity.REFUND_TOKEN);

        ImageView imgviewRet = findViewById(R.id.imageView_result);
        int color = ContextCompat.getColor(this, R.color.colorPaymentFail);
        String resultStr = "Refund Failed";
        if (result){
            resultStr = "Refund Successful";
            color = ContextCompat.getColor(this, R.color.colorPaymentSuccess);

            imgviewRet.setImageResource(R.drawable.payment_success);

            TextView textviewHis = findViewById(R.id.textView_history);
            textviewHis.setText("Transaction History");

        }else{
            imgviewRet.setImageResource(R.drawable.payment_failed);

            String error = getIntent().getStringExtra(RefundActivity.REFUND_ERROR);
            TextView textViewError = findViewById(R.id.textView_payment_error);
            textViewError.setText(error);
            textViewError.setVisibility(View.VISIBLE);
        }
        TextView textviewRet = findViewById(R.id.textView_payment_result);
        textviewRet.setTextColor(color);
        textviewRet.setText(resultStr);

        String amountStr = getIntent().getStringExtra(RefundActivity.REFUND_AMOUNT);
        TextView textviewAmt = findViewById(R.id.textView_payment_amount);
        textviewAmt.setTextColor(color);
        textviewAmt.setText(amountStr);

        String date = getIntent().getStringExtra(RefundActivity.REFUND_DATE);
        TextView textviewDate = findViewById(R.id.textView_payment_date);
        textviewDate.setText(date);

        String storename = getIntent().getStringExtra(RefundActivity.REFUND_STORENAME);
        TextView textviewStoreName = findViewById(R.id.textView_storename_value);
        textviewStoreName.setText(storename);

        LinearLayout payRetInfoLayout = findViewById(R.id.linearLayout_payment_result_history);
        payRetInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent newIntent = new Intent(mContext, TokenTransHistoryActivity.class);
                newIntent.putExtra(TokenTransHistoryActivity.TRANSACTION_HISTORY_COINTYPE, mCoin);
                newIntent.putExtra(TokenTransHistoryActivity.TRANSACTION_HISTORY_TOKEN, mToken);
                startActivity(newIntent);
                */

                Intent newIntent = new Intent(mContext, PaymentHistoryActivity.class);
                startActivity(newIntent);
            }
        });

    }

}
