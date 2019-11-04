package com.chenu.pvcstumansys.db.bean;

import java.io.Serializable;

/**
 * 专业
 */
public class Professional implements Serializable {

    /**
     * 外键映射
     */
    private Profcategory profcategory;
    public void setProfcategory(Profcategory profcategory) {
        this.profcategory = profcategory;
    }
    public Profcategory getProfcategory() {
        return profcategory;
    }

    private Integer pProfid;

    private String name;

    private Integer fProfcategory;

    public Professional(Integer pProfid, String name, Integer fProfcategory) {
        this.pProfid = pProfid;
        this.name = name;
        this.fProfcategory = fProfcategory;
    }

    public Professional() {
        super();
    }

    public Integer getpProfid() {
        return pProfid;
    }

    public void setpProfid(Integer pProfid) {
        this.pProfid = pProfid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getfProfcategory() {
        return fProfcategory;
    }

    public void setfProfcategory(Integer fProfcategory) {
        this.fProfcategory = fProfcategory;
    }
}