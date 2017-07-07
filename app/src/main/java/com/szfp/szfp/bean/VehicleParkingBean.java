package com.szfp.szfp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * author：ct on 2017/7/7 09:17
 * email：cnhttt@163.com
 */

@Entity
public class VehicleParkingBean implements Serializable {
    static final long serialVersionUID = 42L;
    @Id
    private     Long id;
    private     String numberID;
    private     String vehicleNumber;
    private     String fingerPrintID;
    private     long    startTime;
    private     long    endTime;
    private     boolean isPay=false;
    private     int     time;
    private     String  payNum;
    @Generated(hash = 8902597)
    public VehicleParkingBean(Long id, String numberID, String vehicleNumber,
            String fingerPrintID, long startTime, long endTime, boolean isPay,
            int time, String payNum) {
        this.id = id;
        this.numberID = numberID;
        this.vehicleNumber = vehicleNumber;
        this.fingerPrintID = fingerPrintID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isPay = isPay;
        this.time = time;
        this.payNum = payNum;
    }
    @Generated(hash = 370878980)
    public VehicleParkingBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNumberID() {
        return this.numberID;
    }
    public void setNumberID(String numberID) {
        this.numberID = numberID;
    }
    public String getVehicleNumber() {
        return this.vehicleNumber;
    }
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
    public String getFingerPrintID() {
        return this.fingerPrintID;
    }
    public void setFingerPrintID(String fingerPrintID) {
        this.fingerPrintID = fingerPrintID;
    }
    public long getStartTime() {
        return this.startTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public long getEndTime() {
        return this.endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public boolean getIsPay() {
        return this.isPay;
    }
    public void setIsPay(boolean isPay) {
        this.isPay = isPay;
    }
    public String getPayNum() {
        return this.payNum;
    }
    public void setPayNum(String payNum) {
        this.payNum = payNum;
    }
    public int getTime() {
        return this.time;
    }
    public void setTime(int time) {
        this.time = time;
    }
}
