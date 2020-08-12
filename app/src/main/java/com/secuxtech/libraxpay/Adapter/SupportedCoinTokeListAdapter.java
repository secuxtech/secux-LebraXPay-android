package com.secuxtech.libraxpay.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.util.Pair;

import com.secuxtech.libraxpay.Model.Setting;
import com.secuxtech.libraxpay.R;
import com.secuxtech.libraxpay.Utility.AccountUtil;

import java.util.List;

/**
 * Created by maochuns.sun@gmail.com on 2020/3/30
 */
public class SupportedCoinTokeListAdapter extends BaseAdapter {
    private Context context;
    private List<Pair<String, String>> itemArray;

    public SupportedCoinTokeListAdapter() {
        super();
    }

    public SupportedCoinTokeListAdapter(Context context) {
        this.context = context;
        this.itemArray = Setting.getInstance().mCoinTokenArray;
    }

    @Override
    public int getCount() {
        return itemArray.size();
    }

    @Override
    public Object getItem(int position) {
        return itemArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.layout_supported_cointoken_item, parent, false);

        Pair<String, String> item = (Pair<String, String>) itemArray.get(position);

        ImageView imgView = convertView.findViewById(R.id.imageView_supported_cointoken_coinlogo);
        imgView.setImageResource(AccountUtil.getCoinLogo(item.first));

        TextView tvItemTitle = (TextView) convertView.findViewById(R.id.textView_supported_cointoken_name);
        tvItemTitle.setText(item.second);


        return convertView;
    }

}
