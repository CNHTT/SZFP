package com.szfp.szfp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * 作者：ct on 2017/6/23 15:13
 * 邮箱：cnhttt@163.com
 */

@Entity
public class StudentBean implements Serializable {
    static final long serialVersionUID = 42L;
    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private String fullName;
    private String admissionNumber;
    private String captureFingerprintFileURl;
    private String studentPhotoFileURl;
    private boolean gender;

    private String admissionDateStr;
    private long admissionDateLong;
    private String dataOfBirthStr;
    private long dataOfBirthLong;

    private String nationality;
    private int department;
    private String boardingType;
    private String email;
    private String cellPhone;


    private String firstGuardianName;
    private String firstGuardianRelationship;
    private String firstGuardianContacts;

    private String secondGuardianName;
    private String secondGuardianRelationship;
    private String secondGuardianContacts;
    private String permanentHomeAddress;
    private String presentHomeAddress;
    @Generated(hash = 1657594535)
    public StudentBean(Long id, String firstName, String lastName, String fullName,
            String admissionNumber, String captureFingerprintFileURl,
            String studentPhotoFileURl, boolean gender, String admissionDateStr,
            long admissionDateLong, String dataOfBirthStr, long dataOfBirthLong,
            String nationality, int department, String boardingType, String email,
            String cellPhone, String firstGuardianName,
            String firstGuardianRelationship, String firstGuardianContacts,
            String secondGuardianName, String secondGuardianRelationship,
            String secondGuardianContacts, String permanentHomeAddress,
            String presentHomeAddress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.admissionNumber = admissionNumber;
        this.captureFingerprintFileURl = captureFingerprintFileURl;
        this.studentPhotoFileURl = studentPhotoFileURl;
        this.gender = gender;
        this.admissionDateStr = admissionDateStr;
        this.admissionDateLong = admissionDateLong;
        this.dataOfBirthStr = dataOfBirthStr;
        this.dataOfBirthLong = dataOfBirthLong;
        this.nationality = nationality;
        this.department = department;
        this.boardingType = boardingType;
        this.email = email;
        this.cellPhone = cellPhone;
        this.firstGuardianName = firstGuardianName;
        this.firstGuardianRelationship = firstGuardianRelationship;
        this.firstGuardianContacts = firstGuardianContacts;
        this.secondGuardianName = secondGuardianName;
        this.secondGuardianRelationship = secondGuardianRelationship;
        this.secondGuardianContacts = secondGuardianContacts;
        this.permanentHomeAddress = permanentHomeAddress;
        this.presentHomeAddress = presentHomeAddress;
    }
    @Generated(hash = 2097171990)
    public StudentBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public String getAdmissionNumber() {
        return this.admissionNumber;
    }
    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }
    public String getCaptureFingerprintFileURl() {
        return this.captureFingerprintFileURl;
    }
    public void setCaptureFingerprintFileURl(String captureFingerprintFileURl) {
        this.captureFingerprintFileURl = captureFingerprintFileURl;
    }
    public String getStudentPhotoFileURl() {
        return this.studentPhotoFileURl;
    }
    public void setStudentPhotoFileURl(String studentPhotoFileURl) {
        this.studentPhotoFileURl = studentPhotoFileURl;
    }
    public boolean getGender() {
        return this.gender;
    }
    public void setGender(boolean gender) {
        this.gender = gender;
    }
    public String getAdmissionDateStr() {
        return this.admissionDateStr;
    }
    public void setAdmissionDateStr(String admissionDateStr) {
        this.admissionDateStr = admissionDateStr;
    }
    public String getDataOfBirthStr() {
        return this.dataOfBirthStr;
    }
    public void setDataOfBirthStr(String dataOfBirthStr) {
        this.dataOfBirthStr = dataOfBirthStr;
    }
    public String getNationality() {
        return this.nationality;
    }
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    public int getDepartment() {
        return this.department;
    }
    public void setDepartment(int department) {
        this.department = department;
    }
    public String getBoardingType() {
        return this.boardingType;
    }
    public void setBoardingType(String boardingType) {
        this.boardingType = boardingType;
    }
    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getCellPhone() {
        return this.cellPhone;
    }
    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }
    public String getFirstGuardianName() {
        return this.firstGuardianName;
    }
    public void setFirstGuardianName(String firstGuardianName) {
        this.firstGuardianName = firstGuardianName;
    }
    public String getFirstGuardianRelationship() {
        return this.firstGuardianRelationship;
    }
    public void setFirstGuardianRelationship(String firstGuardianRelationship) {
        this.firstGuardianRelationship = firstGuardianRelationship;
    }
    public String getFirstGuardianContacts() {
        return this.firstGuardianContacts;
    }
    public void setFirstGuardianContacts(String firstGuardianContacts) {
        this.firstGuardianContacts = firstGuardianContacts;
    }
    public String getSecondGuardianName() {
        return this.secondGuardianName;
    }
    public void setSecondGuardianName(String secondGuardianName) {
        this.secondGuardianName = secondGuardianName;
    }
    public String getSecondGuardianRelationship() {
        return this.secondGuardianRelationship;
    }
    public void setSecondGuardianRelationship(String secondGuardianRelationship) {
        this.secondGuardianRelationship = secondGuardianRelationship;
    }
    public String getSecondGuardianContacts() {
        return this.secondGuardianContacts;
    }
    public void setSecondGuardianContacts(String secondGuardianContacts) {
        this.secondGuardianContacts = secondGuardianContacts;
    }
    public String getPermanentHomeAddress() {
        return this.permanentHomeAddress;
    }
    public void setPermanentHomeAddress(String permanentHomeAddress) {
        this.permanentHomeAddress = permanentHomeAddress;
    }
    public String getPresentHomeAddress() {
        return this.presentHomeAddress;
    }
    public void setPresentHomeAddress(String presentHomeAddress) {
        this.presentHomeAddress = presentHomeAddress;
    }
    public void setAdmissionDateLong(long admissionDateLong) {
        this.admissionDateLong = admissionDateLong;
    }
    public void setDataOfBirthLong(long dataOfBirthLong) {
        this.dataOfBirthLong = dataOfBirthLong;
    }
    public long getAdmissionDateLong() {
        return this.admissionDateLong;
    }
    public long getDataOfBirthLong() {
        return this.dataOfBirthLong;
    }






}
