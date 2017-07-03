package com.szfp.szfp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 项目名称：SZFP.
 * 创建人： CT.
 * 创建时间: 2017/7/3.
 * GitHub:https://github.com/CNHTT
 */

@Entity
public class AgricultureFarmerBean implements Serializable {
    static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private long id;

    private String name;
    private String  fingerPrintId;
    private String  iDNumber;
    private boolean  gender; //性别
    private String  registrationNumber;
    private String  contact;    //联系方式
    private String  jobTitle;
    private int     numberOfAnimals;
    private String  dataOfBirth;
    private String  employedDate;
    private String  homeTown;
    private String  collectionRoute;
    @Generated(hash = 1288432299)
    public AgricultureFarmerBean(long id, String name, String fingerPrintId,
            String iDNumber, boolean gender, String registrationNumber,
            String contact, String jobTitle, int numberOfAnimals,
            String dataOfBirth, String employedDate, String homeTown,
            String collectionRoute) {
        this.id = id;
        this.name = name;
        this.fingerPrintId = fingerPrintId;
        this.iDNumber = iDNumber;
        this.gender = gender;
        this.registrationNumber = registrationNumber;
        this.contact = contact;
        this.jobTitle = jobTitle;
        this.numberOfAnimals = numberOfAnimals;
        this.dataOfBirth = dataOfBirth;
        this.employedDate = employedDate;
        this.homeTown = homeTown;
        this.collectionRoute = collectionRoute;
    }
    @Generated(hash = 852173373)
    public AgricultureFarmerBean() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFingerPrintId() {
        return this.fingerPrintId;
    }
    public void setFingerPrintId(String fingerPrintId) {
        this.fingerPrintId = fingerPrintId;
    }
    public String getIDNumber() {
        return this.iDNumber;
    }
    public void setIDNumber(String iDNumber) {
        this.iDNumber = iDNumber;
    }
    public boolean getGender() {
        return this.gender;
    }
    public void setGender(boolean gender) {
        this.gender = gender;
    }
    public String getRegistrationNumber() {
        return this.registrationNumber;
    }
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    public String getContact() {
        return this.contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getJobTitle() {
        return this.jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    public int getNumberOfAnimals() {
        return this.numberOfAnimals;
    }
    public void setNumberOfAnimals(int numberOfAnimals) {
        this.numberOfAnimals = numberOfAnimals;
    }
    public String getDataOfBirth() {
        return this.dataOfBirth;
    }
    public void setDataOfBirth(String dataOfBirth) {
        this.dataOfBirth = dataOfBirth;
    }
    public String getEmployedDate() {
        return this.employedDate;
    }
    public void setEmployedDate(String employedDate) {
        this.employedDate = employedDate;
    }
    public String getHomeTown() {
        return this.homeTown;
    }
    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }
    public String getCollectionRoute() {
        return this.collectionRoute;
    }
    public void setCollectionRoute(String collectionRoute) {
        this.collectionRoute = collectionRoute;
    }
}
