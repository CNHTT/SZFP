package com.szfp.szfp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * 作者：ct on 2017/6/22 14:05
 * 邮箱：cnhttt@163.com
 */

@Entity
public class CommuterAccountInfoBean implements Serializable {
    static final long serialVersionUID = 42L;
    @Id
    private Long id;
    private String CommuterAccount;
    private String firstName;
    private String lastName;
    private String fullName;
    private String mobile;
    private String nationalID;
    private String email;
    private String fingerPrintFileUrl;
    private String photoFileUrl;
    private float balance;
    private float deposits;
    private float farePaid;
    private String timeStr;
    private Long timeMills;
    @Generated(hash = 591498849)
    public CommuterAccountInfoBean(Long id, String CommuterAccount,
            String firstName, String lastName, String fullName, String mobile,
            String nationalID, String email, String fingerPrintFileUrl,
            String photoFileUrl, float balance, float deposits, float farePaid,
            String timeStr, Long timeMills) {
        this.id = id;
        this.CommuterAccount = CommuterAccount;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.mobile = mobile;
        this.nationalID = nationalID;
        this.email = email;
        this.fingerPrintFileUrl = fingerPrintFileUrl;
        this.photoFileUrl = photoFileUrl;
        this.balance = balance;
        this.deposits = deposits;
        this.farePaid = farePaid;
        this.timeStr = timeStr;
        this.timeMills = timeMills;
    }
    @Generated(hash = 69220252)
    public CommuterAccountInfoBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCommuterAccount() {
        return this.CommuterAccount;
    }
    public void setCommuterAccount(String CommuterAccount) {
        this.CommuterAccount = CommuterAccount;
    }
    public String getFirstName() {
        return this.firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return this.lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getFullName() {
        return this.fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getMobile() {
        return this.mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getNationalID() {
        return this.nationalID;
    }
    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }
    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getFingerPrintFileUrl() {
        return this.fingerPrintFileUrl;
    }
    public void setFingerPrintFileUrl(String fingerPrintFileUrl) {
        this.fingerPrintFileUrl = fingerPrintFileUrl;
    }
    public String getPhotoFileUrl() {
        return this.photoFileUrl;
    }
    public void setPhotoFileUrl(String photoFileUrl) {
        this.photoFileUrl = photoFileUrl;
    }
    public float getBalance() {
        return this.balance;
    }
    public void setBalance(float balance) {
        this.balance = balance;
    }
    public String getTimeStr() {
        return this.timeStr;
    }
    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }
    public Long getTimeMills() {
        return this.timeMills;
    }
    public void setTimeMills(Long timeMills) {
        this.timeMills = timeMills;
    }
    public float getDeposits() {
        return this.deposits;
    }
    public void setDeposits(float deposits) {
        this.deposits = deposits;
    }
    public float getFarePaid() {
        return this.farePaid;
    }
    public void setFarePaid(float farePaid) {
        this.farePaid = farePaid;
    }

}
