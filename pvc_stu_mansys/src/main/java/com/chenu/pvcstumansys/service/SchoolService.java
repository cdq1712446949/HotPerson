package com.chenu.pvcstumansys.service;

import com.chenu.pvcstumansys.db.bean.Message;
import com.chenu.pvcstumansys.db.bean.School;
import com.chenu.pvcstumansys.db.bean.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 基本作用：学校服务
 * 详细解释：
 * 作者：曹东泉
 * 时间：2019/10/2
 */
public interface SchoolService {

    /**
     * 处理客户端发来的消息，提供服务
     */
    Message service(Message message);

    /**
     * 查询所有学校
     */
    List<School> findAll();

    int remove(Integer pUid);

    int save(School record);

    int update(School record);

    User findById(Integer pUid);

}
