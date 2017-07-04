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


    private boolean isPay =false;
    private String idNumber;
    private long time;
    private int amountCollected;
    @Generated(hash = 53501553)
    public AgricultureFarmerCollection(Long id, boolean isPay, String idNumber,
            long time, int amountCollected) {
        this.id = id;
        this.isPay = isPay;
        this.idNumber = idNumber;
        this.time = time;
        this.amountCollected = amountCollected;
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
                "\namountCollected=" + amountCollected ;
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
}
