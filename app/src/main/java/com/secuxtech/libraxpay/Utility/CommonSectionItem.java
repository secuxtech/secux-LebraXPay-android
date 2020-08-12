package com.secuxtech.libraxpay.Utility;

import com.secuxtech.libraxpay.Interface.CommonItem;

/**
 * Created by maochuns.sun@gmail.com on 2020-03-16
 */
public class CommonSectionItem implements CommonItem {

    private final String title;

    public CommonSectionItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean isSection() {
        return true;
    }
}
