package com.szfp.szfp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * author：ct on 2017/7/3 15:06
 * email：cnhttt@163.com
 */

@Entity
public class StudentStaffBean implements Serializable {
    static final long serialVersionUID = 42L;
    @Id
    private Long id;

    private String fingerPrintId;
    private String name;
    @Generated(hash = 2117599482)
    public StudentStaffBean(Long id, String fingerPrintId, String name) {
        this.id = id;
        this.fingerPrintId = fingerPrintId;
        this.name = name;
    }
    @Generated(hash = 831451468)
    public StudentStaffBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFingerPrintId() {
        return this.fingerPrintId;
    }
    public void setFingerPrintId(String fingerPrintId) {
        this.fingerPrintId = fingerPrintId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
