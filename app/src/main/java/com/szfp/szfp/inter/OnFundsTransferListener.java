package com.szfp.szfp.inter;

import com.szfp.szfp.bean.BankDepositBean;

/**
 * author：ct on 2017/9/12 17:57
 * email：cnhttt@163.com
 */

public interface OnFundsTransferListener {
    void success(BankDepositBean bean,BankDepositBean beanFundsTransfer);
    void error(String str);
}
