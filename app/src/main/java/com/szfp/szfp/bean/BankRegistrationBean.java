package com.szfp.szfp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * 作者：ct on 2017/6/22 18:28
 * 邮箱：cnhttt@163.com
 */

@Entity
public class BankRegistrationBean implements Serializable {
    static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private Long id;

    private String nationalID;
    private String  BankName;
    private String  bankAddress;
    private String  bankBranches;
    private String  bankContacts;
    private String  bankAccountTypes;
    private String  registerTimeStr;
    private Long    registerTime;

    private String  bankPhotoUrl;
    
    @Generated(hash = 356957559)
    public BankRegistrationBean(Long id, String nationalID, String BankName,
            String bankAddress, String bankBranches, String bankContacts,
            String bankAccountTypes, String registerTimeStr, Long registerTime,
            String bankPhotoUrl) {
        this.id = id;
        this.nationalID = nationalID;
        this.BankName = BankName;
        this.bankAddress = bankAddress;
        this.bankBranches = bankBranches;
        this.bankContacts = bankContacts;
        this.bankAccountTypes = bankAccountTypes;
        this.registerTimeStr = registerTimeStr;
        this.registerTime = registerTime;
        this.bankPhotoUrl = bankPhotoUrl;
    }
    @Generated(hash = 490253563)
    public BankRegistrationBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBankName() {
        return this.BankName;
    }
    public void setBankName(String BankName) {
        this.BankName = BankName;
    }
    public String getBankAddress() {
        return this.bankAddress;
    }
    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }
    public String getBankBranches() {
        return this.bankBranches;
    }
    public void setBankBranches(String bankBranches) {
        this.bankBranches = bankBranches;
    }
    public String getBankContacts() {
        return this.bankContacts;
    }
    public void setBankContacts(String bankContacts) {
        this.bankContacts = bankContacts;
    }
    public String getBankAccountTypes() {
        return this.bankAccountTypes;
    }
    public void setBankAccountTypes(String bankAccountTypes) {
        this.bankAccountTypes = bankAccountTypes;
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
    public String getNationalID() {
        return this.nationalID;
    }
    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }
    public String getBankPhotoUrl() {
        return this.bankPhotoUrl;
    }
    public void setBankPhotoUrl(String bankPhotoUrl) {
        this.bankPhotoUrl = bankPhotoUrl;
    }

}
