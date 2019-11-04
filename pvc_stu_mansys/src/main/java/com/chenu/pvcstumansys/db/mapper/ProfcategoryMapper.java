package com.chenu.pvcstumansys.db.mapper;

import com.chenu.pvcstumansys.base.BaseDao;
import com.chenu.pvcstumansys.db.bean.Profcategory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Mapper
@Component
public interface ProfcategoryMapper extends BaseDao<Profcategory> {

    @Override
    int deleteByPrimaryKey(Serializable id);

    @Override
    Profcategory selectByPrimaryKey(Serializable id);

    int insert(Profcategory record);

    int insertSelective(Profcategory record);

    int updateByPrimaryKeySelective(Profcategory record);

    int updateByPrimaryKey(Profcategory record);

    @Override
    List<Profcategory> selectAll();
}