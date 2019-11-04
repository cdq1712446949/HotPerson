package com.chenu.pvcstumansys.db.mapper;

import com.chenu.pvcstumansys.base.BaseDao;
import com.chenu.pvcstumansys.db.bean.School;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Mapper
@Component
public interface SchoolMapper extends BaseDao<School> {

    @Override
    int deleteByPrimaryKey(Serializable id);

    @Override
    School selectByPrimaryKey(Serializable id);

    @Override
    int insert(School record);

    @Override
    int insertSelective(School record);

    @Override
    int updateByPrimaryKeySelective(School record);

    @Override
    int updateByPrimaryKey(School record);

    @Override
    List<School> selectAll();
}