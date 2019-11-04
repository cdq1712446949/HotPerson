package com.chenu.pvcstumansys.db.mapper;

import com.chenu.pvcstumansys.base.BaseDao;
import com.chenu.pvcstumansys.db.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Mapper
@Component
public interface UserMapper extends BaseDao<User> {

    int deleteByPrimaryKey(Serializable id);

    User selectByPrimaryKey(Serializable id);

    int insert(User record);

    int insertSelective(User record);


    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByUsername(String username);

    User login(String username, String password);

    @Override
    List<User> selectAll();

    /**
     * 查询所有根据权限
     * @param privilege 用户权限
     * @return 结果链表
     */
    List<User> selectAllByPrivilege(Byte privilege);

    /**
     * 查询所有超级用户
     * @return 结果链表
     */
    List<User> selectAllOnlyRoot();

    /**
     * 查询所有超级用户根据权限
     * @param privilege 权限
     * @return 结果链表
     */
    List<User> selectAllOnlyRootByPrivilege(Byte privilege);

    /**
     * 查询除了root用户的所有，根据学校
     * @param schoolId 学校id
     * @return 结果链表
     */
    List<User> selectAllNotRootBySchool(Integer schoolId);

}