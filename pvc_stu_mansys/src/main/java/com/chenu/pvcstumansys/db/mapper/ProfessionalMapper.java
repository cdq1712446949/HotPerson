package com.chenu.pvcstumansys.db.mapper;

import com.chenu.pvcstumansys.base.BaseDao;
import com.chenu.pvcstumansys.db.bean.Professional;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Mapper
@Component
public interface ProfessionalMapper extends BaseDao<Professional> {

    @Override
    int deleteByPrimaryKey(Serializable id);

    @Override
    Professional selectByPrimaryKey(Serializable id);

    int insert(Professional record);

    int insertSelective(Professional record);

    int updateByPrimaryKeySelective(Professional record);

    int updateByPrimaryKey(Professional record);

    @Override
    List<Professional> selectAll();
}