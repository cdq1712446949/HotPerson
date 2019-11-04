package com.chenu.pvcstumansys.service;

import com.chenu.pvcstumansys.db.bean.Message;
import com.chenu.pvcstumansys.db.bean.Student;

/**
 * 基本作用：学籍服务
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/4
 */
public interface StudentService {

    /**
     * 处理客户端发来的消息，提供服务
     */
    Message service(Message message);

    int remove(String id);

    int save(Student record);

    Student findById(String id);

    int update(Student record);

}
