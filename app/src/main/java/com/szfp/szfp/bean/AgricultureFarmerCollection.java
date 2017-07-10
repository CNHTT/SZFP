package com.szfp.szfp.bean;

import com.szfp.szfplib.utils.TimeUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * author：ct on 2017/7/4 14:19
 * email：cnhttt@163.com
 */

@Entity
public class AgricultureFarmerCollection implements Serializable {
    static final long serialVersionUID = 42L;
    @Id(autoincrement = true)
    private Long id;

    private String  registrationNumber;

    private boolean isPay =false;
    private String idNumber;
    private long time;
    /**
     * Liters
     */
    private int amountCollected;
    private float amount;
    @Generated(hash = 1824050594)
    public AgricultureFarmerCollection(Long id, String registrationNumber,
            boolean isPay, String idNumber, long time, int amountCollected,
            float amount) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.isPay = isPay;
        this.idNumber = idNumber;
        this.time = time;
        this.amountCollected = amountCollected;
        this.amount = amount;
    }
    @Generated(hash = 307790114)
    public AgricultureFarmerCollection() {
    }

    public String getIdNumber() {
        return this.idNumber;
    }
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public int getAmountCollected() {
        return this.amountCollected;
    }
    public void setAmountCollected(int amountCollected) {
        this.amountCollected = amountCollected;
    }

    @Override
    public String toString() {
        return "\n" +
                "time=" + TimeUtils.milliseconds2String(time)+
                "\nAmount collected (liters)=" + amountCollected
                +"\nAmonut=" + amount ;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return this.id;
    }
    public boolean getIsPay() {
        return this.isPay;
    }
    public void setIsPay(boolean isPay) {
        this.isPay = isPay;
    }
    public float getAmount() {
        return this.amount;
    }
    public void setAmount(float amount) {
        this.amount = amount;
    }
    public String getRegistrationNumber() {
        return this.registrationNumber;
    }
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
