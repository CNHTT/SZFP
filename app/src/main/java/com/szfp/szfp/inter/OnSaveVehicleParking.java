package com.szfp.szfp.inter;

import com.szfp.szfp.bean.VehicleParkingBean;

/**
 * author：ct on 2017/7/7 09:38
 * email：cnhttt@163.com
 */

public interface OnSaveVehicleParking {
    void success(VehicleParkingBean bean);
    void error(String str);
}
