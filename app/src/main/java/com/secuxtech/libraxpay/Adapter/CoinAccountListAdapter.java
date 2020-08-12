package com.secuxtech.libraxpay.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.secuxtech.libraxpay.Interface.AdapterItemClickListener;
import com.secuxtech.libraxpay.Model.CoinTokenAccount;
import com.secuxtech.libraxpay.R;
import com.secuxtech.libraxpay.Utility.AccountUtil;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-21
 */
public class CoinAccountListAdapter extends RecyclerView.Adapter<CoinAccountListAdapter.ViewHolder>{

    private Context                     mContext;
    private List<CoinTokenAccount>      mCoinAccountList;
    Map<String, Boolean>                mItemSelectMap = new HashMap<>();
    private AdapterItemClickListener    mItemClickListener;

    public CoinAccountListAdapter(Context context, List<CoinTokenAccount> accountList, AdapterItemClickListener clickListener) {
        this.mContext = context;
        this.mCoinAccountList = accountList;
        this.mItemClickListener = clickListener;

        for (CoinTokenAccount acc : accountList){
            mItemSelectMap.put(acc.mAccountName, false);
        }
    }

    @Override
    public CoinAccountListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater ll = LayoutInflater.from(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_accountinfo_layout, parent, false);

        return new CoinAccountListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CoinAccountListAdapter.ViewHolder holder, int position) {
        final CoinTokenAccount accItem = mCoinAccountList.get(position);

        holder.textviewAccountName.setText(accItem.mAccountName);
        holder.textviewBalance.setText(accItem.mBalance.mFormattedBalance.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " " + accItem.mToken);
        //holder.textviewUsdbalance.setText("$" + accItem.mBalance.mUSDBalance.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        holder.imageviewCoinLogo.setImageResource(AccountUtil.getCoinLogo(accItem.mCoinType));

        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        holder.rlayoutInside.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
    }


    @Override
    public int getItemCount() {
        return mCoinAccountList.size();
    }

    public void updateAccountList(List<CoinTokenAccount> accountList){
        mCoinAccountList = accountList;

        for (CoinTokenAccount acc : accountList){
            mItemSelectMap.put(acc.mAccountName, false);
        }
    }

    public ArrayList<CoinTokenAccount> getSelectAccountList(){
        ArrayList<CoinTokenAccount> selAccList = new ArrayList<>();
        for(CoinTokenAccount acc: mCoinAccountList){
            if (mItemSelectMap.get(acc.mAccountName)){
                selAccList.add(acc);
            }
        }
        return selAccList;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textviewAccountName, textviewUsdbalance, textviewBalance;
        ImageView imageviewCoinLogo;
        RelativeLayout rlayoutInside;
        CardView cardView;
        ViewHolder(View itemView) {
            super(itemView);

            textviewAccountName = itemView.findViewById(R.id.textView_account_name);
            //textviewUsdbalance = itemView.findViewById(R.id.textView_account_usdbalance);
            textviewBalance = itemView.findViewById(R.id.textView_account_balance);
            imageviewCoinLogo = itemView.findViewById(R.id.imageView_account_coinlogo);
            rlayoutInside = itemView.findViewById(R.id.rlayout_account_cardview);
            cardView = itemView.findViewById(R.id.cardView_account);

            cardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    String accName = textviewAccountName.getText().toString();
                    boolean select = mItemSelectMap.get(accName);
                    int itemIdx = 0;
                    for(CoinTokenAccount acc: mCoinAccountList){
                        if (mItemSelectMap.get(acc.mAccountName) && acc.mAccountName.compareTo(accName) != 0){
                            notifyItemChanged(itemIdx);
                            mItemSelectMap.put(acc.mAccountName, false);
                        }

                        itemIdx += 1;
                    }
                    mItemSelectMap.put(accName, !select);

                    if (mItemSelectMap.get(accName)){
                        cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorYellow));

                        GradientDrawable gradientDrawable   =   new GradientDrawable();
                        gradientDrawable.setCornerRadii(new float[]{20, 20, 20, 20, 20, 20, 20, 20});
                        gradientDrawable.setColor(ContextCompat.getColor(mContext, R.color.colorYellowLight));
                        rlayoutInside.setBackground(gradientDrawable);
                    }else{
                        cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                        rlayoutInside.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    }

                    if (mItemClickListener != null){
                        mItemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });

        }

    }
}
