package com.szfp.szfp.inter;

import com.szfp.szfp.bean.StudentBean;

/**
 * author：ct on 2017/7/3 15:08
 * email：cnhttt@163.com
 */

public interface OnStudentGatePassVerify {
    void studentGatePassSuccess(StudentBean bean);
    void studentGatePassError(String str);
}
