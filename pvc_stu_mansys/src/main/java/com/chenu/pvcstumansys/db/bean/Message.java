package com.chenu.pvcstumansys.db.bean;

public class Message {

    /**
     * 外键约束
     */
    private User userSource;
    public void setUserSource(User userSource) {
        this.userSource = userSource;
    }
    public User getUserSource() {
        return userSource;
    }

    private Integer pMessid;

    private Integer fUserSource;

    private Integer messtype;

    private String data;

    private Byte hasView;

    private Integer toService;

    private Object tag;

    /**
     * 处理成功与否，取决于消息类型，其索引号如下
     */
    public static final int HANDLER_SUCCESS = 1;
    public static final int HANDLER_FAILURE = 0;

    /**
     * 是否需要请求页面
     */
    public static final Byte NO_VIEW = 0;
    public static final Byte YES_VIEW = 1;

    /**
     * 错误号
     */
    public static final int ERROR_NORMAL = -1;
    public static final int ERROR_REQUEST_TYPE = -2;
    public static final int ERROR_NO_RESPOND = -3;

    public Message(Integer pMessid, Integer fUserSource, Integer messtype, String data, Byte hasView, Integer toService) {
        this.pMessid = pMessid;
        this.fUserSource = fUserSource;
        this.messtype = messtype;
        this.data = data;
        this.hasView = hasView;
        this.toService = toService;
    }

    public Message() {
        super();
    }

    public Integer getpMessid() {
        return pMessid;
    }

    public void setpMessid(Integer pMessid) {
        this.pMessid = pMessid;
    }

    public Integer getfUserSource() {
        return fUserSource;
    }

    public void setfUserSource(Integer fUserSource) {
        this.fUserSource = fUserSource;
    }

    public Integer getMesstype() {
        return messtype;
    }

    public void setMesstype(Integer messtype) {
        this.messtype = messtype;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data == null ? null : data.trim();
    }

    public Byte getHasView() {
        return hasView;
    }

    public void setHasView(Byte hasView) {
        this.hasView = hasView;
    }

    public Integer getToService() {
        return toService;
    }

    public void setToService(Integer toService) {
        this.toService = toService;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object getTag() {
        return tag;
    }
}