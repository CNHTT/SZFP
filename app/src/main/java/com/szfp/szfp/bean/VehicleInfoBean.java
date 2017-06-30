package com.szfp.szfp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者：ct on 2017/6/20 12:03
 * 邮箱：cnhttt@163.com
 */


@Entity
public class VehicleInfoBean {
    @Id(autoincrement = true)
    private  Long id;
    private String commuterAccount;
    private String nameOfOwner;
    private String ownerIDNo;
    private String ownerContacts;
    private String ownerAddress;
    private String ownerFingerPrintFileUrl;
    private String ownerPhotoFileUrl;

    private String driverName;
    private String driverNo;
    private String driverAddress;
    private String driverContacts;
    private String driverFingerPrintFileUrl;
    private String driverPhotoFileUrl;

    private String makeOfVehicle;
    private String regNo;
    private String model;
    private String yearOfVehicle;
    private String color;
    private String psvNo;
    private String desiginnatedRoute;
    private String passengerNo;
    private String conductor;


    private String timeStr;
    private Long timeMills;

    @Generated(hash = 1473888051)
    public VehicleInfoBean(Long id, String commuterAccount, String nameOfOwner,
            String ownerIDNo, String ownerContacts, String ownerAddress,
            String ownerFingerPrintFileUrl, String ownerPhotoFileUrl,
            String driverName, String driverNo, String driverAddress,
            String driverContacts, String driverFingerPrintFileUrl,
            String driverPhotoFileUrl, String makeOfVehicle, String regNo,
            String model, String yearOfVehicle, String color, String psvNo,
            String desiginnatedRoute, String passengerNo, String conductor,
            String timeStr, Long timeMills) {
        this.id = id;
        this.commuterAccount = commuterAccount;
        this.nameOfOwner = nameOfOwner;
        this.ownerIDNo = ownerIDNo;
        this.ownerContacts = ownerContacts;
        this.ownerAddress = ownerAddress;
        this.ownerFingerPrintFileUrl = ownerFingerPrintFileUrl;
        this.ownerPhotoFileUrl = ownerPhotoFileUrl;
        this.driverName = driverName;
        this.driverNo = driverNo;
        this.driverAddress = driverAddress;
        this.driverContacts = driverContacts;
        this.driverFingerPrintFileUrl = driverFingerPrintFileUrl;
        this.driverPhotoFileUrl = driverPhotoFileUrl;
        this.makeOfVehicle = makeOfVehicle;
        this.regNo = regNo;
        this.model = model;
        this.yearOfVehicle = yearOfVehicle;
        this.color = color;
        this.psvNo = psvNo;
        this.desiginnatedRoute = desiginnatedRoute;
        this.passengerNo = passengerNo;
        this.conductor = conductor;
        this.timeStr = timeStr;
        this.timeMills = timeMills;
    }
    @Generated(hash = 1138660239)
    public VehicleInfoBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNameOfOwner() {
        return this.nameOfOwner;
    }
    public void setNameOfOwner(String nameOfOwner) {
        this.nameOfOwner = nameOfOwner;
    }
    public String getOwnerIDNo() {
        return this.ownerIDNo;
    }
    public void setOwnerIDNo(String ownerIDNo) {
        this.ownerIDNo = ownerIDNo;
    }
    public String getOwnerContacts() {
        return this.ownerContacts;
    }
    public void setOwnerContacts(String ownerContacts) {
        this.ownerContacts = ownerContacts;
    }
    public String getOwnerAddress() {
        return this.ownerAddress;
    }
    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }
    public String getOwnerFingerPrintFileUrl() {
        return this.ownerFingerPrintFileUrl;
    }
    public void setOwnerFingerPrintFileUrl(String ownerFingerPrintFileUrl) {
        this.ownerFingerPrintFileUrl = ownerFingerPrintFileUrl;
    }
    public String getOwnerPhotoFileUrl() {
        return this.ownerPhotoFileUrl;
    }
    public void setOwnerPhotoFileUrl(String ownerPhotoFileUrl) {
        this.ownerPhotoFileUrl = ownerPhotoFileUrl;
    }
    public String getDriverName() {
        return this.driverName;
    }
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    public String getDriverNo() {
        return this.driverNo;
    }
    public void setDriverNo(String driverNo) {
        this.driverNo = driverNo;
    }
    public String getDriverAddress() {
        return this.driverAddress;
    }
    public void setDriverAddress(String driverAddress) {
        this.driverAddress = driverAddress;
    }
    public String getDriverContacts() {
        return this.driverContacts;
    }
    public void setDriverContacts(String driverContacts) {
        this.driverContacts = driverContacts;
    }
    public String getDriverFingerPrintFileUrl() {
        return this.driverFingerPrintFileUrl;
    }
    public void setDriverFingerPrintFileUrl(String driverFingerPrintFileUrl) {
        this.driverFingerPrintFileUrl = driverFingerPrintFileUrl;
    }
    public String getDriverPhotoFileUrl() {
        return this.driverPhotoFileUrl;
    }
    public void setDriverPhotoFileUrl(String driverPhotoFileUrl) {
        this.driverPhotoFileUrl = driverPhotoFileUrl;
    }
    public String getMakeOfVehicle() {
        return this.makeOfVehicle;
    }
    public void setMakeOfVehicle(String makeOfVehicle) {
        this.makeOfVehicle = makeOfVehicle;
    }
    public String getRegNo() {
        return this.regNo;
    }
    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }
    public String getModel() {
        return this.model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getYearOfVehicle() {
        return this.yearOfVehicle;
    }
    public void setYearOfVehicle(String yearOfVehicle) {
        this.yearOfVehicle = yearOfVehicle;
    }
    public String getColor() {
        return this.color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getPsvNo() {
        return this.psvNo;
    }
    public void setPsvNo(String psvNo) {
        this.psvNo = psvNo;
    }
    public String getDesiginnatedRoute() {
        return this.desiginnatedRoute;
    }
    public void setDesiginnatedRoute(String desiginnatedRoute) {
        this.desiginnatedRoute = desiginnatedRoute;
    }
    public String getPassengerNo() {
        return this.passengerNo;
    }
    public void setPassengerNo(String passengerNo) {
        this.passengerNo = passengerNo;
    }
    public String getConductor() {
        return this.conductor;
    }
    public void setConductor(String conductor) {
        this.conductor = conductor;
    }
    public String getCommuterAccount() {
        return this.commuterAccount;
    }
    public void setCommuterAccount(String commuterAccount) {
        this.commuterAccount = commuterAccount;
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

}
