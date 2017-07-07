package com.szfp.szfp.inter;

import com.szfp.szfp.bean.ParkingInfoBean;

/**
 * author：ct on 2017/7/7 18:34
 * email：cnhttt@163.com
 */

public interface OnSaveByParkingListener {
    void success(ParkingInfoBean bean,String s);
    void error(String str);
}
