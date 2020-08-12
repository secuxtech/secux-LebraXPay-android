package com.secuxtech.libraxpay.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.secuxtech.libraxpay.Interface.AdapterItemClickListener;
import com.secuxtech.libraxpay.Interface.OnListScrollListener;
import com.secuxtech.libraxpay.R;
import com.secuxtech.paymentkit.SecuXTransferHistory;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-21
 */
public class TokenTransHistoryAdapter extends RecyclerView.Adapter<TokenTransHistoryAdapter.ViewHolder> {

    private Context mContext;
    private List<SecuXTransferHistory> mTransHistoryList;

    private AdapterItemClickListener mClickListener;
    OnListScrollListener mOnListScrollListener = null;

    public TokenTransHistoryAdapter(Context context, List<SecuXTransferHistory> accountList, AdapterItemClickListener clickListener) {
        this.mContext = context;
        this.mTransHistoryList = accountList;
        this.mClickListener = clickListener;
    }

    @Override
    public TokenTransHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater ll = LayoutInflater.from(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_account_transaction_layout, parent, false);

        return new TokenTransHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TokenTransHistoryAdapter.ViewHolder holder, int position) {
        final SecuXTransferHistory hisItem = mTransHistoryList.get(position);

        String symbol;
        if (hisItem.mTxType.compareTo("Send") == 0){
            holder.imageviewTypeLogo.setImageResource(R.drawable.icon_send);
            symbol = "-";
        }else{
            holder.imageviewTypeLogo.setImageResource(R.drawable.icon_receive_yellow);
            symbol = "+";
        }

        holder.textviewType.setText(hisItem.mTxType);
        holder.textviewBalance.setText(symbol + hisItem.mFormattedAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " " + hisItem.mAmountSymbol);
        holder.textviewUsdbalance.setText("$" + hisItem.mAmountUsd.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        holder.textviewTimestamp.setText(hisItem.mTimestamp);

        if (position == mTransHistoryList.size()-1 && mOnListScrollListener!=null){
            mOnListScrollListener.onBottomReached(position);
        }
    }

    @Override
    public int getItemCount() {
        return mTransHistoryList.size();
    }

    public void setOnListScrollListener(OnListScrollListener listener){
        mOnListScrollListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textviewType, textviewTimestamp, textviewUsdbalance, textviewBalance;
        ImageView imageviewTypeLogo;
        ViewHolder(View itemView) {
            super(itemView);

            textviewType = itemView.findViewById(R.id.textView_transcardview_type);
            textviewUsdbalance = itemView.findViewById(R.id.textView_transcardview_usdbalance);
            textviewBalance = itemView.findViewById(R.id.textView_transcardview_balance);
            imageviewTypeLogo = itemView.findViewById(R.id.imageView_transcardview_type);
            textviewTimestamp = itemView.findViewById(R.id.textView_transcardview_timestamp);
            CardView cardView = itemView.findViewById(R.id.cardView_account_transactions);

            cardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (mClickListener != null)
                        mClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }
}
