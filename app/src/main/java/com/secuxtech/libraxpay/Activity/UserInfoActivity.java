package com.secuxtech.libraxpay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.secuxtech.libraxpay.Adapter.UserInfoListAdapter;
import com.secuxtech.libraxpay.BuildConfig;
import com.secuxtech.libraxpay.Interface.CommonItem;
import com.secuxtech.libraxpay.Model.Setting;
import com.secuxtech.libraxpay.R;
import com.secuxtech.libraxpay.Utility.CommonEntryItem;
import com.secuxtech.libraxpay.Utility.CommonSectionItem;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserInfoActivity extends BaseActivity {
    private ListView mListViewInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.mShowBackButton = false;
        this.mShowLogo = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        TextView tvName = findViewById(R.id.textview_userinfo_name);
        tvName.setText(Setting.getInstance().mAccount.mAlias);

        TextView tvEmail = findViewById(R.id.textview_userinfo_email);
        tvEmail.setText(Setting.getInstance().mAccount.mEmail);

        TextView tvTel = findViewById(R.id.textview_userinfo_tel);
        tvTel.setText(Setting.getInstance().mAccount.mPhoneNum);

        mListViewInfo = findViewById(R.id.listview_userinfo);

        final ArrayList<CommonItem> itemlist = generateListData();
        final UserInfoListAdapter adapter = new UserInfoListAdapter(this, itemlist);
        mListViewInfo.setAdapter(adapter);
        mListViewInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,"click item " + position);

                if (position==1){
                    Intent i = new Intent(UserInfoActivity.this, ChangePasswordActivity.class);
                    //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }else if (position==2){
                    //Setting.getInstance().mUserLogout = true;
                    Intent i = new Intent(UserInfoActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        });

        BottomNavigationView navigationView = findViewById(R.id.navigation_main);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        MenuItem menuItem = navigationView.getMenu().getItem(2).setChecked(true);
    }

    ArrayList<CommonItem> generateListData() {
        ArrayList<CommonItem> countryList = new ArrayList<>();

        countryList.add(new CommonSectionItem("ACCOUNT"));
        //countryList.add(new CommonEntryItem("Update user information"));
        countryList.add(new CommonEntryItem("Change password"));
        countryList.add(new CommonEntryItem("Logout"));

        countryList.add(new CommonSectionItem("ABOUT"));
        countryList.add(new CommonEntryItem("Version", BuildConfig.VERSION_NAME));

        return countryList;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_main_accounts:
                    Intent newIntent = new Intent(mContext, CoinAccountListActivity.class);
                    startActivity(newIntent);
                    return true;

                case R.id.navigation_main_payment:
                    Intent paymentIntent = new Intent(mContext, PaymentMainActivity.class);
                    startActivity(paymentIntent);
                    return true;

                case R.id.navigation_main_userinfo:
                    return true;
            }
            return false;
        }

    };
}
