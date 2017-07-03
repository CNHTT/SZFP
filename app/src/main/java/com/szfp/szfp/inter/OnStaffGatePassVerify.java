package com.szfp.szfp.inter;

import com.szfp.szfp.bean.StudentStaffBean;

/**
 * author：ct on 2017/7/3 15:11
 * email：cnhttt@163.com
 */

public interface OnStaffGatePassVerify {
    void staffGatePassSuccess(StudentStaffBean bean);
    void staffGatePassError(String str);
}
