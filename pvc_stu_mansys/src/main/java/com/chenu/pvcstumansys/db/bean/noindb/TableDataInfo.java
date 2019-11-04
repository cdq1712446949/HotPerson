package com.chenu.pvcstumansys.db.bean.noindb;

import java.util.List;

/**
 * 表格数据信息
 * 返回这个对象，前端可以根据其中的信息完成分页
 */
public class TableDataInfo<T> {

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<T> rows;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
