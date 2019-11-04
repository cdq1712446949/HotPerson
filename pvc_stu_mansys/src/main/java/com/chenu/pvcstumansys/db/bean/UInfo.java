package com.chenu.pvcstumansys.db.bean;

import java.io.Serializable;

/**
 * 用户资料
 */
public class UInfo implements Serializable {

    /**
     * 外键映射
     */
    private School school;
    public void setSchool(School school) {
        this.school = school;
    }
    public School getSchool() {
        return school;
    }

    private Integer pUfid;

    private Integer fSchool;

    private String position;

    private String phone;

    public UInfo(Integer pUfid, Integer fSchool, String position, String phone) {
        this.pUfid = pUfid;
        this.fSchool = fSchool;
        this.position = position;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UInfo{" +
                "school=" + school +
                ", pUfid=" + pUfid +
                ", fSchool=" + fSchool +
                ", position='" + position + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public UInfo() {
        super();
    }

    public Integer getpUfid() {
        return pUfid;
    }

    public void setpUfid(Integer pUfid) {
        this.pUfid = pUfid;
    }

    public Integer getfSchool() {
        return fSchool;
    }

    public void setfSchool(Integer fSchool) {
        this.fSchool = fSchool;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position == null ? null : position.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }
}