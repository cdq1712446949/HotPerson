package com.chenu.pvcstumansys.db.bean.noindb;

import java.io.Serializable;

public class MessageData implements Serializable {

    private String note;
    private String url;

    @Override
    public String toString() {
        return "MessageData{" +
                "note='" + note + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
