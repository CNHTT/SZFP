package com.szfp.szfp.inter;

import com.szfp.szfp.bean.ParkingInfoBean;

/**
 * author：ct on 2017/7/5 16:15
 * email：cnhttt@163.com
 */

public interface OnVerifyParkingListener {
    void success(ParkingInfoBean bean);
    void error(String s);
}
