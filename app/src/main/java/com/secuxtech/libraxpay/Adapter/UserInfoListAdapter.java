package com.secuxtech.libraxpay.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.secuxtech.libraxpay.Interface.CommonItem;
import com.secuxtech.libraxpay.R;
import com.secuxtech.libraxpay.Utility.CommonEntryItem;
import com.secuxtech.libraxpay.Utility.CommonSectionItem;


import java.util.ArrayList;

/**
 * Created by maochuns.sun@gmail.com on 2020-03-16
 */
public class UserInfoListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CommonItem> item;


    public UserInfoListAdapter() {
        super();
    }

    public UserInfoListAdapter(Context context, ArrayList<CommonItem> item) {
        this.context = context;
        this.item = item;
        //this.originalItem = item;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int position) {
        return item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (item.get(position).isSection()) {
            // if section header
            convertView = inflater.inflate(R.layout.layout_userinfo_section, parent, false);
            TextView tvSectionTitle = (TextView) convertView.findViewById(R.id.textview_sectionheader);
            tvSectionTitle.setText(((CommonSectionItem) item.get(position)).getTitle());
        }
        else
        {
            // if item
            convertView = inflater.inflate(R.layout.layout_userinfo_item, parent, false);
            TextView tvItemTitle = (TextView) convertView.findViewById(R.id.textview_itemtitle);
            TextView tvItemValue = (TextView) convertView.findViewById(R.id.textview_itemvale);
            tvItemTitle.setText(((CommonEntryItem) item.get(position)).getTitle());
            tvItemValue.setText(((CommonEntryItem) item.get(position)).getValue());

            if (position==1 || position==2){
                ImageView ivNext = convertView.findViewById(R.id.imageview_item_next);
                ivNext.setVisibility(View.VISIBLE);
                ivNext.getLayoutParams().width = 30;
            }
        }

        return convertView;
    }

    /**
     * Filter

    public Filter getFilter()
    {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                item = (ArrayList<Item>) results.values;
                notifyDataSetChanged();
            }

            @SuppressWarnings("null")
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<Item> filteredArrayList = new ArrayList<Item>();


                if(originalItem == null || originalItem.size() == 0)
                {
                    originalItem = new ArrayList<Item>(item);
                }

                //
                 // if constraint is null then return original value
                 // else return filtered value
                //
                if(constraint == null && constraint.length() == 0)
                {
                    results.count = originalItem.size();
                    results.values = originalItem;
                }
                else
                {
                    constraint = constraint.toString().toLowerCase(Locale.ENGLISH);
                    for (int i = 0; i < originalItem.size(); i++)
                    {
                        String title = originalItem.get(i).getTitle().toLowerCase(Locale.ENGLISH);
                        if(title.startsWith(constraint.toString()))
                        {
                            filteredArrayList.add(originalItem.get(i));
                        }
                    }
                    results.count = filteredArrayList.size();
                    results.values = filteredArrayList;
                }

                return results;
            }
        };

        return filter;
    }
    */



}
