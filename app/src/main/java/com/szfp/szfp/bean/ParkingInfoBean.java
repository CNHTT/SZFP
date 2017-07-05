package com.szfp.szfp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * author：ct on 2017/7/5 09:32
 * email：cnhttt@163.com
 */

@Entity
public class ParkingInfoBean implements Serializable {
    static final long serialVersionUID = 42L;


    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String idNumber;
    private String fingerID;
    private String vehicleRegNumber;
    private float balance;
    /**
     * per hour, per day, per month
     */
    private int parameters_type;
    private long startTime;
    private long endTime;
    @Generated(hash = 800694932)
    public ParkingInfoBean(Long id, String name, String idNumber, String fingerID,
            String vehicleRegNumber, float balance, int parameters_type,
            long startTime, long endTime) {
        this.id = id;
        this.name = name;
        this.idNumber = idNumber;
        this.fingerID = fingerID;
        this.vehicleRegNumber = vehicleRegNumber;
        this.balance = balance;
        this.parameters_type = parameters_type;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    @Generated(hash = 1594980640)
    public ParkingInfoBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIdNumber() {
        return this.idNumber;
    }
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
    public String getVehicleRegNumber() {
        return this.vehicleRegNumber;
    }
    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }
   
    public int getParameters_type() {
        return this.parameters_type;
    }
    public void setParameters_type(int parameters_type) {
        this.parameters_type = parameters_type;
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
    public String getFingerID() {
        return this.fingerID;
    }
    public void setFingerID(String fingerID) {
        this.fingerID = fingerID;
    }
    public float getBalance() {
        return this.balance;
    }
    public void setBalance(float balance) {
        this.balance = balance;
    }

}
