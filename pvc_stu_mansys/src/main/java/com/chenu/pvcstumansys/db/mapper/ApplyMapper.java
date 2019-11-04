package com.chenu.pvcstumansys.db.mapper;

import com.chenu.pvcstumansys.base.BaseDao;
import com.chenu.pvcstumansys.db.bean.Apply;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ApplyMapper extends BaseDao<Apply> {

    int deleteByPrimaryKey(Integer pApplid);

    int insert(Apply record);

    int insertSelective(Apply record);

    Apply selectByPrimaryKey(Integer pApplid);

    int updateByPrimaryKeySelective(Apply record);

    int updateByPrimaryKey(Apply record);

    @Override
    List<Apply> selectAll();

    /**
     * 查询所有，根据条件处理状态
     * @param state
     * @return
     */
    List<Apply> selectAllByState(Integer state);
}