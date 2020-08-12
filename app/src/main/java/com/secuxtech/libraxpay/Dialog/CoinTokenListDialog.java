package com.secuxtech.libraxpay.Dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Pair;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.secuxtech.libraxpay.Adapter.CoinTokenListAdapter;

import com.secuxtech.libraxpay.R;
import com.secuxtech.libraxpay.Utility.LogHandler;
import com.secuxtech.libraxpay.Utility.SecuXUtility;

import java.util.ArrayList;


/**
 * Created by maochuns.sun@gmail.com on 2020/6/2
 */
public class CoinTokenListDialog {

    private static AlertDialog mAlertDialog = null;
    private static CoinTokenListAdapter mListAdapter = null;

    private static ItemTouchHelper.Callback mItemTouchCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN,
            0) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {

            final int fromPos = viewHolder.getAdapterPosition();
            final int toPos = target.getAdapterPosition();

            mListAdapter.notifyItemMoved(fromPos, toPos);
            return true; // true if moved, false otherwise
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    public static void show(Context context, ArrayList<Pair<String, String>> coinTokenArr) {
        /*
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            LogHandler.Log("CoinTokenListDialog shows already");
            return;
        }

         */

        if (((Activity)context).isFinishing()){
            LogHandler.Log("show CoinTokenListDialog with invalid context");
            return;
        }

        mAlertDialog = new AlertDialog.Builder(context).create();
        View loadView = LayoutInflater.from(context).inflate(R.layout.dialog_coin_token_info_list, null);
        RecyclerView recyclerView = (RecyclerView) loadView.findViewById(R.id.recyclerview_coin_token_list);
        ViewGroup.LayoutParams params=recyclerView.getLayoutParams();
        if (coinTokenArr.size() < 8) {
            params.height = (int) (SecuXUtility.convertDpToPixel(70, context) * coinTokenArr.size());
        }else{
            params.height = (int) (SecuXUtility.convertDpToPixel(70, context) * 7);
        }
        recyclerView.setLayoutParams(params);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mListAdapter = new CoinTokenListAdapter(context, coinTokenArr);
        recyclerView.setAdapter(mListAdapter);


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.colorUpdateFWProgressBk)));
        mAlertDialog.setView(loadView, 0, 0, 0, 0);
        mAlertDialog.setCanceledOnTouchOutside(true);

        try {
            mAlertDialog.show();
        }catch (Exception e){
            e.printStackTrace();
            LogHandler.Log("Show CoinTokenListDialog exception");
        }
    }

    public static void dismiss(){
        mAlertDialog.dismiss();

    }

    public static ArrayList<Pair<String,String>> getSelectAccountList(){
        return mListAdapter.getSelectAccountList();

    }

}
