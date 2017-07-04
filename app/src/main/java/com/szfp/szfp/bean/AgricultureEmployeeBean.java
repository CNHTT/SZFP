package com.szfp.szfp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * 项目名称：SZFP.
 * 创建人： CT.
 * 创建时间: 2017/7/3.
 * GitHub:https://github.com/CNHTT
 */

@Entity
public class AgricultureEmployeeBean implements Serializable {
    static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private Long id;

    private String name;
    private String  fingerPrintId;
    private String  iDNumber;
    private boolean  gender; //性别
    private String  registrationNumber;
    private String  contact;    //联系方式
    private String  jobTitle;
    private String  salary;
    private String  dataOfBirth;
    private String  employedDate;
    private String  homeTown;
    private String  collectionRoute;
    /**
     * Permanent, contractor, day laborer
     */
    private String  natureOfEmployment;
    @Generated(hash = 13205761)
    public AgricultureEmployeeBean(Long id, String name, String fingerPrintId,
            String iDNumber, boolean gender, String registrationNumber,
            String contact, String jobTitle, String salary, String dataOfBirth,
            String employedDate, String homeTown, String collectionRoute,
            String natureOfEmployment) {
        this.id = id;
        this.name = name;
        this.fingerPrintId = fingerPrintId;
        this.iDNumber = iDNumber;
        this.gender = gender;
        this.registrationNumber = registrationNumber;
        this.contact = contact;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.dataOfBirth = dataOfBirth;
        this.employedDate = employedDate;
        this.homeTown = homeTown;
        this.collectionRoute = collectionRoute;
        this.natureOfEmployment = natureOfEmployment;
    }
    @Generated(hash = 1256953803)
    public AgricultureEmployeeBean() {
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
    public String getSalary() {
        return this.salary;
    }
    public void setSalary(String salary) {
        this.salary = salary;
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
    public String getNatureOfEmployment() {
        return this.natureOfEmployment;
    }
    public void setNatureOfEmployment(String natureOfEmployment) {
        this.natureOfEmployment = natureOfEmployment;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return this.id;
    }
}
