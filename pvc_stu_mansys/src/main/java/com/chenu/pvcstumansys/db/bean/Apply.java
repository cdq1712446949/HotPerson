package com.chenu.pvcstumansys.db.bean;

public class Apply {

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

    private Integer pApplid;

    private Integer fMessage;

    private Byte state;

    private Byte result;

    private String note;

    @Override
    public String toString() {
        return "Apply{" +
                "pApplid=" + pApplid +
                ", fMessage=" + fMessage +
                ", state=" + state +
                ", result=" + result +
                '}';
    }

    public Apply() {
        super();
    }

    public Apply(Integer pApplid, Integer fMessage, Byte state, Byte result) {
        this.pApplid = pApplid;
        this.fMessage = fMessage;
        this.state = state;
        this.result = result;
    }

    public Integer getpApplid() {
        return pApplid;
    }

    public void setpApplid(Integer pApplid) {
        this.pApplid = pApplid;
    }

    public Integer getfMessage() {
        return fMessage;
    }

    public void setfMessage(Integer fMessage) {
        this.fMessage = fMessage;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public Byte getState() {
        return state;
    }

    public Byte getResult() {
        return result;
    }

    public void setResult(Byte result) {
        this.result = result;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }
}