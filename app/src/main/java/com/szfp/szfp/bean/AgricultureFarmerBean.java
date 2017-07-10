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
public class AgricultureFarmerBean implements Serializable {
    static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private Long id;

    private String name;
    private String  fingerPrintId;
    private String  iDNumber;
    private boolean  gender; //性别
    private String  registrationNumber;
    private String  contact;    //联系方式
    private int     numberOfAnimals;
    private String  dataOfBirth;
    private String  homeTown;
    private String  collectionRoute;
    private float   amount;
    @Generated(hash = 301223603)
    public AgricultureFarmerBean(Long id, String name, String fingerPrintId,
            String iDNumber, boolean gender, String registrationNumber,
            String contact, int numberOfAnimals, String dataOfBirth,
            String homeTown, String collectionRoute, float amount) {
        this.id = id;
        this.name = name;
        this.fingerPrintId = fingerPrintId;
        this.iDNumber = iDNumber;
        this.gender = gender;
        this.registrationNumber = registrationNumber;
        this.contact = contact;
        this.numberOfAnimals = numberOfAnimals;
        this.dataOfBirth = dataOfBirth;
        this.homeTown = homeTown;
        this.collectionRoute = collectionRoute;
        this.amount = amount;
    }
    @Generated(hash = 852173373)
    public AgricultureFarmerBean() {
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

    @Override
    public String toString() {
        return
                "Name='" + name + '\'' +
                "\niDNumber='" + iDNumber + '\'' +
                "\n" +
                        "gender=" + (gender?"M":"F") +
                "\nregistrationNumber='" + registrationNumber + '\'' +
                "\ncontact='" + contact + '\'' +
                "\nnumberOfAnimals=" + numberOfAnimals +
                "\ndataOfBirth='" + dataOfBirth + '\'' +
                "\nhomeTown='" + homeTown + '\'' +
                "\ncollectionRoute='" + collectionRoute + '\''+
                "Amount = " +amount;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return this.id;
    }
    public float getAmount() {
        return this.amount;
    }
    public void setAmount(float amount) {
        this.amount = amount;
    }
}
