package com.chenu.pvcstumansys.db.mapper;

import com.chenu.pvcstumansys.base.BaseDao;
import com.chenu.pvcstumansys.db.bean.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Mapper
@Component
public interface MessageMapper extends BaseDao<Message> {


    @Override
    int deleteByPrimaryKey(Serializable id);

    @Override
    Message selectByPrimaryKey(Serializable id);

    int insert(Message record);

    int insertSelective(Message record);

    int updateByPrimaryKeySelective(Message record);

    int updateByPrimaryKey(Message record);

    @Override
    List<Message> selectAll();

    /**
     * 查询数据库中最新用户信息的Id
     */
    Serializable selectNewId();
}