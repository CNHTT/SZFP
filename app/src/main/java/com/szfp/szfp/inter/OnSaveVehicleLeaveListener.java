package com.szfp.szfp.inter;

import com.szfp.szfp.bean.ParkingInfoBean;
import com.szfp.szfp.bean.VehicleParkingBean;

/**
 * author：ct on 2017/7/7 17:43
 * email：cnhttt@163.com
 */

public interface OnSaveVehicleLeaveListener {
    public void success(VehicleParkingBean vehicleParkingBean, ParkingInfoBean bean) ;

    public void error(String s) ;
}
