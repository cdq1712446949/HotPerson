package com.chenu.pvcstumansys.service;

import com.chenu.pvcstumansys.db.bean.Message;
import com.chenu.pvcstumansys.db.bean.noindb.TableDataInfo;
import com.chenu.pvcstumansys.db.bean.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 基本作用：用户服务
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
public interface UserService extends UserDetailsService {

    /**
     * 处理客户端发来的消息，提供服务
     */
    Message service(Message message);

    /**
     * 查询所有用户
     */
    TableDataInfo<User> findAll(Integer userSource);

    /**
     * 查询所有用户，可以根据权限查询，还可以指定是否只看超级用户
     */
    TableDataInfo<User> findAll(Integer[] parms, Integer userSource);

    int remove(Integer pUid);

    int save(User record);

    User findById(Integer pUid);

    int update(User record);

    User login(String username, String password);

}
