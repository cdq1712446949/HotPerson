package com.chenu.pvcstumansys.db.bean;

import java.io.Serializable;

/**
 * 专业类别
 */
public class Profcategory implements Serializable {
    private Integer pProfcatid;

    private String name;

    private String note;

    public Profcategory(Integer pProfcatid, String name, String note) {
        this.pProfcatid = pProfcatid;
        this.name = name;
        this.note = note;
    }

    public Profcategory() {
        super();
    }

    public Integer getpProfcatid() {
        return pProfcatid;
    }

    public void setpProfcatid(Integer pProfcatid) {
        this.pProfcatid = pProfcatid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }
}