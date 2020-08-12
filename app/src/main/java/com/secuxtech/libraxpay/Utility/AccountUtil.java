package com.secuxtech.libraxpay.Utility;

import androidx.annotation.DrawableRes;

import com.secuxtech.libraxpay.Model.CoinTokenAccount;
import com.secuxtech.libraxpay.Model.Setting;
import com.secuxtech.libraxpay.R;
import com.secuxtech.paymentkit.SecuXCoinAccount;
import com.secuxtech.paymentkit.SecuXCoinTokenBalance;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-20
 */
public class AccountUtil {

    static public @DrawableRes
    int getCoinLogo(String coinType){
        switch (coinType){
            case "DCT":
                return R.drawable.dct;
            case "LBR":
                return R.drawable.lbr;

            default:
                return R.drawable.dct;
        }
    }

    static public ArrayList<CoinTokenAccount> getCoinTokenAccounts(){
        ArrayList<CoinTokenAccount> accountArr = new ArrayList<>();
        for(int i = 0; i< Setting.getInstance().mAccount.mCoinAccountArr.size(); i++){
            SecuXCoinAccount coinAccount = Setting.getInstance().mAccount.mCoinAccountArr.get(i);

            if (Setting.getInstance().mShowLBRAccountOnlyFlag && coinAccount.mCoinType.compareToIgnoreCase("LBR")!=0){
                continue;
            }

            Set<Map.Entry<String, SecuXCoinTokenBalance>> entrySet = coinAccount.mTokenBalanceMap.entrySet();
            for (Map.Entry<String, SecuXCoinTokenBalance> entry: entrySet){
                String token = entry.getKey();

                CoinTokenAccount tokenAccount = new CoinTokenAccount(coinAccount, token);
                accountArr.add(tokenAccount);
            }
        }
        return accountArr;
    }
}
