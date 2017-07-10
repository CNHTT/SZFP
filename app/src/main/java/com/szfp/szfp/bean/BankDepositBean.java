package com.szfp.szfp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * author：ct on 2017/7/10 14:25
 * email：cnhttt@163.com
 */

@Entity
public class BankDepositBean implements Serializable {
    static final long serialVersionUID = 42L;

    @Id
    private Long id;
    private String IdNumber;
    private String acNumber;
    private String acName;
    private String bankName;
    private boolean isRecord;
    private float cashNumber;
    private float waihNumber;
    private float balance;
    @Generated(hash = 1839332381)
    public BankDepositBean(Long id, String IdNumber, String acNumber, String acName,
            String bankName, boolean isRecord, float cashNumber, float waihNumber,
            float balance) {
        this.id = id;
        this.IdNumber = IdNumber;
        this.acNumber = acNumber;
        this.acName = acName;
        this.bankName = bankName;
        this.isRecord = isRecord;
        this.cashNumber = cashNumber;
        this.waihNumber = waihNumber;
        this.balance = balance;
    }
    @Generated(hash = 363870167)
    public BankDepositBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getIdNumber() {
        return this.IdNumber;
    }
    public void setIdNumber(String IdNumber) {
        this.IdNumber = IdNumber;
    }
    public String getAcNumber() {
        return this.acNumber;
    }
    public void setAcNumber(String acNumber) {
        this.acNumber = acNumber;
    }
    public String getAcName() {
        return this.acName;
    }
    public void setAcName(String acName) {
        this.acName = acName;
    }
    public boolean getIsRecord() {
        return this.isRecord;
    }
    public void setIsRecord(boolean isRecord) {
        this.isRecord = isRecord;
    }
    public float getCashNumber() {
        return this.cashNumber;
    }
    public void setCashNumber(float cashNumber) {
        this.cashNumber = cashNumber;
    }
    public float getWaihNumber() {
        return this.waihNumber;
    }
    public void setWaihNumber(float waihNumber) {
        this.waihNumber = waihNumber;
    }
    public float getBalance() {
        return this.balance;
    }
    public void setBalance(float balance) {
        this.balance = balance;
    }
    public String getBankName() {
        return this.bankName;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
 }
