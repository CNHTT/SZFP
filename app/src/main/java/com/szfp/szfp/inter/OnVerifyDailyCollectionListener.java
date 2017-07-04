package com.szfp.szfp.inter;

import com.szfp.szfp.bean.AgricultureFarmerBean;
import com.szfp.szfp.bean.AgricultureFarmerCollection;

/**
 * author：ct on 2017/7/4 11:57
 * email：cnhttt@163.com
 */

public interface OnVerifyDailyCollectionListener {
    void success(AgricultureFarmerBean bean, AgricultureFarmerCollection result);
    void error(String str);
}
