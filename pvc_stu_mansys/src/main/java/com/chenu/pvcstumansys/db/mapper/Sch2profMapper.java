package com.chenu.pvcstumansys.db.mapper;

import com.chenu.pvcstumansys.base.BaseDao;
import com.chenu.pvcstumansys.db.bean.Sch2prof;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Mapper
@Component
public interface Sch2profMapper extends BaseDao<Sch2prof> {

    @Override
    int deleteByPrimaryKey(Serializable id);

    @Override
    Sch2prof selectByPrimaryKey(Serializable id);

    int insert(Sch2prof record);

    int insertSelective(Sch2prof record);

    int updateByPrimaryKeySelective(Sch2prof record);

    int updateByPrimaryKey(Sch2prof record);

    @Override
    List<Sch2prof> selectAll();

    /**
     * 查询一个学校的所有专业映射
     * @param schoolId 学校id
     */
    List<Sch2prof> selectBySchool(Integer schoolId);
}