package com.chenu.pvcstumansys.db.mapper;

import com.chenu.pvcstumansys.base.BaseDao;
import com.chenu.pvcstumansys.db.bean.Reply;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Mapper
@Component
public interface ReplyMapper extends BaseDao<Reply> {

    int deleteByPrimaryKey(Serializable pReplid);

    int insert(Reply record);

    int insertSelective(Reply record);

    @Override
    Reply selectByPrimaryKey(Serializable id);

    int updateByPrimaryKeySelective(Reply record);

    int updateByPrimaryKey(Reply record);

    @Override
    List<Reply> selectAll();

    /**
     * 查询所有，通过用户源
     * @param userSource 用户源
     */
    List<Reply> selectAllByUserSource(Integer userSource);
}