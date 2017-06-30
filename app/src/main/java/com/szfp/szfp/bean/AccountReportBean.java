package com.szfp.szfp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * ���ߣ�ct on 2017/6/29 09:52
 * ���䣺cnhttt@163.com
 */

@Entity
public class AccountReportBean implements Serializable {

    static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private Long id;
    private String ACNumber;
    private float deposits;
    private long  depositsDate;
    private float   farePaid;
    private long    farePaidDate;
    private float   balance;
    @Generated(hash = 302613008)
    public AccountReportBean(Long id, String ACNumber, float deposits,
            long depositsDate, float farePaid, long farePaidDate, float balance) {
        this.id = id;
        this.ACNumber = ACNumber;
        this.deposits = deposits;
        this.depositsDate = depositsDate;
        this.farePaid = farePaid;
        this.farePaidDate = farePaidDate;
        this.balance = balance;
    }
    @Generated(hash = 1016263781)
    public AccountReportBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getACNumber() {
        return this.ACNumber;
    }
    public void setACNumber(String ACNumber) {
        this.ACNumber = ACNumber;
    }
    public float getDeposits() {
        return this.deposits;
    }
    public void setDeposits(float deposits) {
        this.deposits = deposits;
    }
    public long getDepositsDate() {
        return this.depositsDate;
    }
    public void setDepositsDate(long depositsDate) {
        this.depositsDate = depositsDate;
    }
    public float getFarePaid() {
        return this.farePaid;
    }
    public void setFarePaid(float farePaid) {
        this.farePaid = farePaid;
    }
    public long getFarePaidDate() {
        return this.farePaidDate;
    }
    public void setFarePaidDate(long farePaidDate) {
        this.farePaidDate = farePaidDate;
    }
    public float getBalance() {
        return this.balance;
    }
    public void setBalance(float balance) {
        this.balance = balance;
    }
}
