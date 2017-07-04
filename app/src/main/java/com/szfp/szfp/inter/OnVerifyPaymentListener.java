package com.szfp.szfp.inter;

import com.szfp.szfp.bean.AgricultureFarmerBean;
import com.szfp.szfp.bean.AgricultureFarmerCollection;

import java.util.ArrayList;

/**
 * author：ct on 2017/7/4 16:52
 * email：cnhttt@163.com
 */

public interface OnVerifyPaymentListener {
    void success(AgricultureFarmerBean bean, ArrayList<AgricultureFarmerCollection>  list);
    void error(String str);
}
