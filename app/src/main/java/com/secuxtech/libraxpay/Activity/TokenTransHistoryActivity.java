package com.secuxtech.libraxpay.Activity;


import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.util.Pair;

import com.secuxtech.libraxpay.Adapter.HistoryListAdapter;
import com.secuxtech.libraxpay.Adapter.TokenTransHistoryAdapter;
import com.secuxtech.libraxpay.Interface.AdapterItemClickListener;
import com.secuxtech.libraxpay.Interface.OnListScrollListener;
import com.secuxtech.libraxpay.Model.Setting;
import com.secuxtech.libraxpay.R;
import com.secuxtech.libraxpay.Utility.CommonProgressDialog;
import com.secuxtech.paymentkit.SecuXAccountManager;
import com.secuxtech.paymentkit.SecuXServerRequestHandler;
import com.secuxtech.paymentkit.SecuXTransferHistory;

import java.util.ArrayList;

public class TokenTransHistoryActivity extends BaseActivity {

    public final static String TRANSACTION_HISTORY_COINTYPE = "com.secuxtech.libraxpay.TRANSHISCOINTYPE";
    public final static String TRANSACTION_HISTORY_TOKEN = "com.secuxtech.libraxpay.TRANSHISTOKEN";

    private SecuXAccountManager mAccountManager = new SecuXAccountManager();
    private ArrayList<SecuXTransferHistory> mTransHistoryArray = new ArrayList<>();
    private TokenTransHistoryAdapter mAdapter = null;
    private RecyclerView mRecyclerView;

    private String mCoinType;
    private String mToken;

    private int mLoadRecordIndex = 0;
    private int mLoadRecordCount = 20;
    private boolean mLoadRecordComplete = false;

    AdapterItemClickListener mItemClickListener = new AdapterItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            SecuXTransferHistory history = mTransHistoryArray.get(position);
            Log.i(TAG, history.mDetailslUrl);

            Intent newIntent = new Intent(mContext, TokenTransferDetailsActivity.class);
            newIntent.putExtra(TokenTransferDetailsActivity.TRANSACTION_HISTORY_DETAIL_URL, history.mDetailslUrl);
            startActivity(newIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_trans_history);

        Intent intent = getIntent();
        mCoinType = intent.getStringExtra(TRANSACTION_HISTORY_COINTYPE);
        mToken = intent.getStringExtra(TRANSACTION_HISTORY_TOKEN);

        mRecyclerView = findViewById(R.id.recyclerView_transhistory_list);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        CommonProgressDialog.showProgressDialog(mContext, "Loading");
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadRecords();
            }
        }).start();
    }

    void loadRecords(){
        if (mLoadRecordComplete){
            CommonProgressDialog.dismiss();
            return;
        }

        ArrayList<SecuXTransferHistory> hisArray = new ArrayList<>();
        Pair<Integer, String> ret = mAccountManager.getTransferHistory(mCoinType, mToken, mLoadRecordIndex, mLoadRecordCount, hisArray);

        CommonProgressDialog.dismiss();
        if (ret.first!= SecuXServerRequestHandler.SecuXRequestOK){
            showMessageInMain("Get payment history failed! Error: " + ret.second);

            if (ret.second.contains("No token") || ret.first == SecuXServerRequestHandler.SecuXRequestUnauthorized){

                showLoginWndInMain();

            }

            return;
        }

        if (mLoadRecordIndex == 0){
            mTransHistoryArray.clear();
        }

        if (hisArray.size() < mLoadRecordCount){
            mLoadRecordComplete = true;
        }else{
            mLoadRecordIndex += 1;
        }

        mTransHistoryArray.addAll(hisArray);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mTransHistoryArray.size()>0 && mAdapter == null){
                    mAdapter = new TokenTransHistoryAdapter(mContext, mTransHistoryArray, mItemClickListener);
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.setOnListScrollListener(new OnListScrollListener(){
                        @Override
                        public void onBottomReached(int position){
                            Log.i(TAG, "onBottomReached");
                            CommonProgressDialog.showProgressDialog(mContext, "Loading");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    loadRecords();
                                }
                            }).start();
                        }
                    });

                }else if (mTransHistoryArray.size()>0) {
                    mAdapter.notifyDataSetChanged();
                }else{
                        TextView textviewNoHistory = findViewById(R.id.textView_transhistory_nohistory);
                        textviewNoHistory.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                }

            }

        });

    }

}
