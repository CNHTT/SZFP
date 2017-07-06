package com.szfp.szfp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * author：ct on 2017/7/5 10:12
 * email：cnhttt@163.com
 */

@Entity
public class ParkingRechargeBean implements Serializable {
    static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private Long id;

    private String idNumber;
    private float amount;
    private long time;
    @Generated(hash = 1007305974)
    public ParkingRechargeBean(Long id, String idNumber, float amount, long time) {
        this.id = id;
        this.idNumber = idNumber;
        this.amount = amount;
        this.time = time;
    }
    @Generated(hash = 925040269)
    public ParkingRechargeBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getIdNumber() {
        return this.idNumber;
    }
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
    public float getAmount() {
        return this.amount;
    }
    public void setAmount(float amount) {
        this.amount = amount;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
}
