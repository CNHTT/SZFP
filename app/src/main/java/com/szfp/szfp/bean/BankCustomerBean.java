package com.szfp.szfp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * 作者：ct on 2017/6/23 09:31
 * 邮箱：cnhttt@163.com
 */

@Entity
public class BankCustomerBean implements Serializable {
    static final long serialVersionUID = 42L;
    @Id(autoincrement = true)
    private Long id;

    private String fosaAccount;
    private boolean gender;
    private String  number;
    /**
     * 婚姻状况
     */
    private String  maritalStatus;
    /**
     * 用户状态
     */
    private String  model;

    /**
     * 联系
     */
    private String  contacts;
    private String  branch;
    private String  name;
    private String  email;
    private String  nationalId;
    private String  fingerPrintFileUrl;
    private String  photoFileUrl;
    private String  registerTimeStr;
    private Long    registerTime;
    private float   deposit;
    private float   total;
    @Generated(hash = 846859183)
    public BankCustomerBean(Long id, String fosaAccount, boolean gender,
            String number, String maritalStatus, String model, String contacts,
            String branch, String name, String email, String nationalId,
            String fingerPrintFileUrl, String photoFileUrl, String registerTimeStr,
            Long registerTime, float deposit, float total) {
        this.id = id;
        this.fosaAccount = fosaAccount;
        this.gender = gender;
        this.number = number;
        this.maritalStatus = maritalStatus;
        this.model = model;
        this.contacts = contacts;
        this.branch = branch;
        this.name = name;
        this.email = email;
        this.nationalId = nationalId;
        this.fingerPrintFileUrl = fingerPrintFileUrl;
        this.photoFileUrl = photoFileUrl;
        this.registerTimeStr = registerTimeStr;
        this.registerTime = registerTime;
        this.deposit = deposit;
        this.total = total;
    }
    @Generated(hash = 252314687)
    public BankCustomerBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFosaAccount() {
        return this.fosaAccount;
    }
    public void setFosaAccount(String fosaAccount) {
        this.fosaAccount = fosaAccount;
    }
    public boolean getGender() {
        return this.gender;
    }
    public void setGender(boolean gender) {
        this.gender = gender;
    }
    public String getNumber() {
        return this.number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getMaritalStatus() {
        return this.maritalStatus;
    }
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
    public String getModel() {
        return this.model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getContacts() {
        return this.contacts;
    }
    public void setContacts(String contacts) {
        this.contacts = contacts;
    }
    public String getBranch() {
        return this.branch;
    }
    public void setBranch(String branch) {
        this.branch = branch;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNationalId() {
        return this.nationalId;
    }
    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
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
    public String getRegisterTimeStr() {
        return this.registerTimeStr;
    }
    public void setRegisterTimeStr(String registerTimeStr) {
        this.registerTimeStr = registerTimeStr;
    }
    public Long getRegisterTime() {
        return this.registerTime;
    }
    public void setRegisterTime(Long registerTime) {
        this.registerTime = registerTime;
    }
    public float getDeposit() {
        return this.deposit;
    }
    public void setDeposit(float deposit) {
        this.deposit = deposit;
    }
    public float getTotal() {
        return this.total;
    }
    public void setTotal(float total) {
        this.total = total;
    }
}
