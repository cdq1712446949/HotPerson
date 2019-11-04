package com.chenu.pvcstumansys.db.bean;

import java.io.Serializable;
import java.util.Objects;

/**
 * 学校专业映射，组成一个学校专业库
 */
public class Sch2prof implements Serializable {

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
    private Professional professional;
    public void setProfessional(Professional professional) {
        this.professional = professional;
    }
    public Professional getProfessional() {
        return professional;
    }

    private Integer pSchProfId;

    private Integer fSchool;

    private Integer fProfessional;

    public Sch2prof(Integer pSchProfId, Integer fSchool, Integer fProfessional) {
        this.pSchProfId = pSchProfId;
        this.fSchool = fSchool;
        this.fProfessional = fProfessional;
    }

    public Sch2prof() {
        super();
    }

    public Integer getpSchProfId() {
        return pSchProfId;
    }

    public void setpSchProfId(Integer pSchProfId) {
        this.pSchProfId = pSchProfId;
    }

    public Integer getfSchool() {
        return fSchool;
    }

    public void setfSchool(Integer fSchool) {
        this.fSchool = fSchool;
    }

    public Integer getfProfessional() {
        return fProfessional;
    }

    public void setfProfessional(Integer fProfessional) {
        this.fProfessional = fProfessional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sch2prof sch2prof = (Sch2prof) o;
        return Objects.equals(fSchool, sch2prof.fSchool) &&
                Objects.equals(fProfessional, sch2prof.fProfessional);
    }

    @Override
    public int hashCode() {

        return Objects.hash(fSchool, fProfessional);
    }
}