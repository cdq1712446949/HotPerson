package com.chenu.pvcstumansys.service.impl;

import com.chenu.pvcstumansys.db.bean.Message;
import com.chenu.pvcstumansys.db.bean.School;
import com.chenu.pvcstumansys.db.bean.User;
import com.chenu.pvcstumansys.db.mapper.SchoolMapper;
import com.chenu.pvcstumansys.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 基本作用： 学校服务实现类
 * 详细解释：
 * 作者：曹东泉
 * 时间：2019/10/2
 */
@Service("schoolService")
public class SchoolServiceImpl implements SchoolService {

    /**
     * 请求用户服务的服务路由键
     */
    public static final String SERVICE_ROUTING_KEY = "school";
    /**
     * 消息服务器的学校专业库消息队列
     */
    public static final String QUEUE = "pvcstumansys.school.queue";

    /**
     * dao
     */
    @Autowired
    SchoolMapper schoolMapper;

    public static boolean schoolDataIsUpdate = true;   /* 数据库数据是否被更新了 */

    @Override
    public Message service(Message message) {
        return null;
    }

    @Override
    public List<School> findAll() {
        return schoolMapper.selectAll();
    }

    @Override
    public int remove(Integer pUid) {
        schoolDataIsUpdate = true;
        return 0;
    }

    @Override
    public int save(School record) {
        schoolDataIsUpdate = true;
        return 0;
    }

    @Override
    public int update(School record) {
        schoolDataIsUpdate = true;
        return 0;
    }

    @Override
    public User findById(Integer pUid) {
        return null;
    }

}
