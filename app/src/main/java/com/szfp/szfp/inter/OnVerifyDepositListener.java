package com.szfp.szfp.inter;

import com.szfp.szfp.bean.BankDepositBean;

/**
 * author：ct on 2017/7/10 14:56
 * email：cnhttt@163.com
 */

public interface OnVerifyDepositListener {
    void success(BankDepositBean bean);
    void error(String str);
}
