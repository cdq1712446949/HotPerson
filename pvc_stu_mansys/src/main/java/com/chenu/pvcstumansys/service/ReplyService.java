package com.chenu.pvcstumansys.service;

import com.chenu.pvcstumansys.db.bean.Message;
import com.chenu.pvcstumansys.db.bean.Reply;

/**
 * 基本作用：申请回复服务
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/4
 */
public interface ReplyService {

    /**
     * 处理客户端发来的消息，提供服务
     */
    Message service(Message message);

    public void reply(
            Integer messageId,
            String note,
            byte beViewed
    );

    int remove(Integer id);

    int save(Reply record);

    Reply findById(Integer id);

    int update(Reply record);

}
