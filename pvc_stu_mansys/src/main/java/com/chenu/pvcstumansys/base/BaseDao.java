package com.chenu.pvcstumansys.base;

import java.io.Serializable;
import java.util.List;

/**
 * 基本作用：工具类，完成了Dao层应该完成的最基本的增删改查，可以添加上一些自己常用的
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
public interface BaseDao<T> {

    /**
     * 最基础的增删改查
     */

    int deleteByPrimaryKey(Serializable id);

    int insert(T record);

    int insertSelective(T record);

    T selectByPrimaryKey(Serializable id);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);

    List<T> selectAll();


}
