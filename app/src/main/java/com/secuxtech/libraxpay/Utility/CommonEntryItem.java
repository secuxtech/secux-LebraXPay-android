package com.secuxtech.libraxpay.Utility;

import com.secuxtech.libraxpay.Interface.CommonItem;

/**
 * Created by maochuns.sun@gmail.com on 2020-03-16
 */
public class CommonEntryItem implements CommonItem {

    public final String title;
    public final String value;

    public CommonEntryItem(String title) {
        this.title = title;
        this.value = "";
    }

    public CommonEntryItem(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() { return value; }

    @Override
    public boolean isSection() {
        return false;
    }
}
