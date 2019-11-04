package com.chenu.pvcstumansys.db.bean;

public class Reply {

    /**
     * 外键映射
     */
    private Message message;
    public void setMessage(Message message) {
        this.message = message;
    }
    public Message getMessage() {
        return message;
    }

    private Integer pReplid;

    private Integer fMessage;

    private String note;

    private Byte beViewed;

    public Reply(Integer pReplid, Integer fMessage, String note, Byte beViewed) {
        this.pReplid = pReplid;
        this.fMessage = fMessage;
        this.note = note;
        this.beViewed = beViewed;
    }

    public Reply() {
        super();
    }

    public Integer getpReplid() {
        return pReplid;
    }

    public void setpReplid(Integer pReplid) {
        this.pReplid = pReplid;
    }

    public Integer getfMessage() {
        return fMessage;
    }

    public void setfMessage(Integer fMessage) {
        this.fMessage = fMessage;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }

    public Byte getBeViewed() {
        return beViewed;
    }

    public void setBeViewed(Byte beViewed) {
        this.beViewed = beViewed;
    }
}