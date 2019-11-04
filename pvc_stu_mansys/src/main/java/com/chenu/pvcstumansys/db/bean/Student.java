package com.chenu.pvcstumansys.db.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 学生信息，也称学籍
 */
public class Student implements Serializable {
    /**
     * 外键映射
     */
    private transient School school;
    public void setSchool(School school) {
        this.school = school;
    }
    public School getSchool() {
        return school;
    }
    private transient Professional professional;
    public void setProfessional(Professional professional) {
        this.professional = professional;
    }
    public Professional getProfessional() {
        return professional;
    }

    private String pStuid;

    private String code;

    private String name;

    private String idnumber;

    private String picture;

    private Byte gender;

    private Byte age;

    private String homeadd;

    private String phone;

    private Integer fSchool;

    private Date enrolltime;

    private Byte schoYearSys;

    private Date graduatime;

    private Byte diploma;

    private Integer fProfessional;

    public Student(String pStuid, String code, String name, String idnumber, String picture, Byte gender, Byte age, String homeadd, String phone, Integer fSchool, Date enrolltime, Byte schoYearSys, Date graduatime, Byte diploma, Integer fProfessional) {
        this.pStuid = pStuid;
        this.code = code;
        this.name = name;
        this.idnumber = idnumber;
        this.picture = picture;
        this.gender = gender;
        this.age = age;
        this.homeadd = homeadd;
        this.phone = phone;
        this.fSchool = fSchool;
        this.enrolltime = enrolltime;
        this.schoYearSys = schoYearSys;
        this.graduatime = graduatime;
        this.diploma = diploma;
        this.fProfessional = fProfessional;
    }

    @Override
    public String toString() {
        return "Student{" +
                "pStuid='" + pStuid + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", idnumber='" + idnumber + '\'' +
                ", picture='" + picture + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", homeadd='" + homeadd + '\'' +
                ", phone='" + phone + '\'' +
                ", fSchool=" + fSchool +
                ", enrolltime=" + enrolltime +
                ", schoYearSys=" + schoYearSys +
                ", graduatime=" + graduatime +
                ", diploma=" + diploma +
                ", fProfessional=" + fProfessional +
                '}';
    }

    public Student() {
        super();
    }

    public String getpStuid() {
        return pStuid;
    }

    public void setpStuid(String pStuid) {
        this.pStuid = pStuid == null ? null : pStuid.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber == null ? null : idnumber.trim();
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture == null ? null : picture.trim();
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public Byte getAge() {
        return age;
    }

    public void setAge(Byte age) {
        this.age = age;
    }

    public String getHomeadd() {
        return homeadd;
    }

    public void setHomeadd(String homeadd) {
        this.homeadd = homeadd == null ? null : homeadd.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Integer getfSchool() {
        return fSchool;
    }

    public void setfSchool(Integer fSchool) {
        this.fSchool = fSchool;
    }

    public Date getEnrolltime() {
        return enrolltime;
    }

    public void setEnrolltime(Date enrolltime) {
        this.enrolltime = enrolltime;
    }

    public Byte getSchoYearSys() {
        return schoYearSys;
    }

    public void setSchoYearSys(Byte schoYearSys) {
        this.schoYearSys = schoYearSys;
    }

    public Date getGraduatime() {
        return graduatime;
    }

    public void setGraduatime(Date graduatime) {
        this.graduatime = graduatime;
    }

    public Byte getDiploma() {
        return diploma;
    }

    public void setDiploma(Byte diploma) {
        this.diploma = diploma;
    }

    public Integer getfProfessional() {
        return fProfessional;
    }

    public void setfProfessional(Integer fProfessional) {
        this.fProfessional = fProfessional;
    }
}