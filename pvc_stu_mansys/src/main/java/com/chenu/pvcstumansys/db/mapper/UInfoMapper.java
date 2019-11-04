package com.chenu.pvcstumansys.db.mapper;

import com.chenu.pvcstumansys.base.BaseDao;
import com.chenu.pvcstumansys.db.bean.UInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Mapper
@Component
public interface UInfoMapper extends BaseDao<UInfo> {

    @Override
    int deleteByPrimaryKey(Serializable id);

    @Override
    int insert(UInfo record);

    @Override
    int insertSelective(UInfo record);

    @Override
    UInfo selectByPrimaryKey(Serializable id);

    @Override
    int updateByPrimaryKeySelective(UInfo record);

    @Override
    int updateByPrimaryKey(UInfo record);

    @Override
    List<UInfo> selectAll();

    /**
     * 查询数据库中最新用户信息的Id
     */
    Serializable selectNewId();
}