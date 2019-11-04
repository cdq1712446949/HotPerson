package com.chenu.pvcstumansys.db.bean;

import java.io.Serializable;

/**
 * 学校
 */
public class School implements Serializable {
    private Integer pSchid;

    private String name;

    private String location;

    private String phone;

    private String website;

    public School(Integer pSchid, String name, String location, String phone, String website) {
        this.pSchid = pSchid;
        this.name = name;
        this.location = location;
        this.phone = phone;
        this.website = website;
    }

    public School() {
        super();
    }

    public Integer getpSchid() {
        return pSchid;
    }

    public void setpSchid(Integer pSchid) {
        this.pSchid = pSchid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website == null ? null : website.trim();
    }
}