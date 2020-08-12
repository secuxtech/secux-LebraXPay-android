package com.secuxtech.libraxpay.Adapter;

import android.content.Context;

import androidx.core.content.ContextCompat;
import android.util.Pair;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.secuxtech.libraxpay.Model.CoinTokenAccount;
import com.secuxtech.libraxpay.Model.Setting;
import com.secuxtech.libraxpay.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by maochuns.sun@gmail.com on 2020/6/2
 */
public class CoinTokenListAdapter extends RecyclerView.Adapter<CoinTokenListAdapter.ViewHolder> {

    Context mContext = null;
    Map<String, Boolean> mItemSelectMap = new HashMap<>();
    ArrayList<Pair<String, String>> mCoinTokenArr;

    public CoinTokenListAdapter(Context context, ArrayList<Pair<String, String>> coinTokenArr){
        this.mContext = context;

        this.mCoinTokenArr = coinTokenArr;

        for (Pair<String,String> item : coinTokenArr){
            mItemSelectMap.put(item.first + " : " + item.second, false);
        }
    }

    @Override
    public CoinTokenListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater ll = LayoutInflater.from(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview_coin_token_info, parent, false);

        return new CoinTokenListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CoinTokenListAdapter.ViewHolder holder, int position) {
        Pair<String, String> coinTokenItem = mCoinTokenArr.get(position);

        holder.textviewCoinTokenItem.setText(coinTokenItem.first + " : " + coinTokenItem.second);
    }

    @Override
    public int getItemCount() {

        return mCoinTokenArr.size();
    }

    public ArrayList<Pair<String,String>> getSelectAccountList(){
        ArrayList<Pair<String,String>> selConTokenList = new ArrayList<>();
        for (Pair<String,String> item : mCoinTokenArr){
            if (mItemSelectMap.get(item.first + " : " + item.second)){
                selConTokenList.add(item);
            }
        }

        if (selConTokenList.size()==0 && mCoinTokenArr.size()==1){
            selConTokenList.add(mCoinTokenArr.get(0));
        }

        return selConTokenList;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textviewCoinTokenItem;
        CardView cardviewCoinToken;

        ViewHolder(View itemView) {
            super(itemView);
            textviewCoinTokenItem = itemView.findViewById(R.id.textView_coin_token_item);
            cardviewCoinToken = itemView.findViewById(R.id.cardView_coin_token);

            cardviewCoinToken.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String itemTxt = textviewCoinTokenItem.getText().toString();
                    mItemSelectMap.put(itemTxt, !mItemSelectMap.get(itemTxt));

                    if (mItemSelectMap.get(itemTxt)){
                        cardviewCoinToken.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorYellowLight));

                    }else{
                        cardviewCoinToken.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));

                    }
                }
            });


        }

    }
}
