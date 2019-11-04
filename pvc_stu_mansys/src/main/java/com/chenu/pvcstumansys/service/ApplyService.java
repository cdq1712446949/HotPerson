package com.chenu.pvcstumansys.service;

import com.chenu.pvcstumansys.db.bean.Apply;
import com.chenu.pvcstumansys.db.bean.Message;

/**
 * 基本作用：申请服务
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/3
 */
public interface ApplyService {

    /**
     * 处理客户端发来的消息，提供服务
     */
    Message service(Message message);

    int remove(Integer id);

    int save(Apply record);

    Apply findById(Integer id);

    int update(Apply record);

}
