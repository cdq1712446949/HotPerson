package com.chenu.pvcstumansys.service;

import com.chenu.pvcstumansys.db.bean.Message;
import com.chenu.pvcstumansys.db.bean.User;
import org.springframework.stereotype.Service;


/**
 * 基本作用：消息服务
 * 详细解释：提供消息通信服务
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
public interface MessageService {

    int remove(Integer pUid);

    int save(Message record);

    Message findById(Integer pUid);

    int update(Message record);

    /**
     * 发送一条消息到某个服务请求一个功能服务，返回一个包含处理结果的消息对象
     * @param message 消息
     * @param toService 要发送到的服务，例如用户服务，给一个 0 即可，这个是有专门的规范的
     * @return 包含处理结果的消息对象
     */
    Message sendAndReceive(Message message, int toService);


}
