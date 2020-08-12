package com.secuxtech.libraxpay.Model;

import com.secuxtech.paymentkit.SecuXCoinAccount;
import com.secuxtech.paymentkit.SecuXCoinTokenBalance;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-21
 */
public class CoinTokenAccount {
    public String mCoinType = "";
    public String mAccountName = "";
    public String mToken = "";
    public SecuXCoinTokenBalance mBalance = null;

    public CoinTokenAccount(SecuXCoinAccount account, String token){
        mCoinType = account.mCoinType;
        mAccountName = account.mAccountName;
        mToken = token;
        mBalance = account.mTokenBalanceMap.get(token);
    }
}
