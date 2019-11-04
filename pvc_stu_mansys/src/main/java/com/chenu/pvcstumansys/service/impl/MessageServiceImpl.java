package com.chenu.pvcstumansys.service.impl;

import com.chenu.pvcstumansys.db.bean.Message;
import com.chenu.pvcstumansys.db.mapper.MessageMapper;
import com.chenu.pvcstumansys.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;


/**
 * 基本作用：消息服务实现
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
@Service("messageService")
public class MessageServiceImpl implements MessageService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 请求服务消息的消息转发器
     */
    public static final String SERVICE_EXCHANGES = "pvcstumansys.direct";

    /**
     * 消息框架模版
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 以下是系统可提供服务的所有索引号
     * 每添加一个新的服务，都需要在这里添加上索引号
     */
    public static final int APPLY_SERVER = -1;                /* 申请服务索引号 */
    public static final int USER_SERVER = 0;                /* 用户服务索引号 */
    public static final int PROFESSIONAL_SERVER = 1;      /* 专业服务索引号 */
    public static final int SCH_TO_PROF_SERVER = 2;      /* 学校专业库服务索引号 */
    public static final int SCHOOL_SERVER = 3;          /* 学校服务索引号 */
    public static final int STUDENT_SERVER = 4;          /* 学籍服务索引号 */
    public static final int REPLY_SERVER = 5;          /* 申请回复服务索引号 */




    @Autowired
    private MessageMapper messageMapper;

    @Override
    public int remove(Integer pUid) {
        return messageMapper.deleteByPrimaryKey(pUid);
    }

    @Override
    public int save(Message record) {
        return messageMapper.insertSelective(record);
    }

    @Override
    public Message findById(Integer pUid) {
        return messageMapper.selectByPrimaryKey(pUid);
    }

    @Override
    public int update(Message record) {
        return messageMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public Message sendAndReceive(Message message, int toService) {

        // 根据索引号，发送消息给对应的服务
        // 服务完成后，得到返回消息
        switch (toService){
            /* 申请服务 */
            case APPLY_SERVER:
                logger.info("你请求了一个申请服务...");
                return (Message) rabbitTemplate.convertSendAndReceive(
                        SERVICE_EXCHANGES,
                        ApplyServiceImpl.SERVICE_ROUTING_KEY,
                        message
                );

            /* 用户服务 */
            case USER_SERVER:
                logger.info("你请求了一个用户服务...");
                return (Message) rabbitTemplate.convertSendAndReceive(
                        SERVICE_EXCHANGES,
                        UserServiceImpl.SERVICE_ROUTING_KEY,
                        message
                );

            /* 专业服务 */
            case PROFESSIONAL_SERVER:
                logger.info("你请求了一个专业服务...");
                return (Message) rabbitTemplate.convertSendAndReceive(
                        SERVICE_EXCHANGES,
                        ProfessionalServiceImpl.SERVICE_ROUTING_KEY,
                        message
                );

            /* 学校专业库服务 */
            case SCH_TO_PROF_SERVER:
                logger.info("你请求了一个学校专业库服务...");
                return (Message) rabbitTemplate.convertSendAndReceive(
                        SERVICE_EXCHANGES,
                        Sch2profServiceImpl.SERVICE_ROUTING_KEY,
                        message
                );

            /* 学校服务 */
            case SCHOOL_SERVER:
                logger.info("你请求了一个学校服务...");
                return (Message) rabbitTemplate.convertSendAndReceive(
                        SERVICE_EXCHANGES,
                        ProfessionalServiceImpl.SERVICE_ROUTING_KEY,
                        message
                );

            /* 学籍服务 */
            case STUDENT_SERVER:
                logger.info("你请求了一个学籍服务...");
                return (Message) rabbitTemplate.convertSendAndReceive(
                        SERVICE_EXCHANGES,
                        StudentServiceImpl.SERVICE_ROUTING_KEY,
                        message
                );

            /* 申请回复服务 */
            case REPLY_SERVER:
                logger.info("你请求了一个申请回复服务...");
                return (Message) rabbitTemplate.convertSendAndReceive(
                        SERVICE_EXCHANGES,
                        ReplyServiceImpl.SERVICE_ROUTING_KEY,
                        message
                );

            /* 不存在的服务，直接返回一个错误消息 */
            default:
                logger.warn("你请求了一个不存在的服务...");
                Message bad = new Message();
                bad.setMesstype(Message.HANDLER_FAILURE);
                bad.setData("你请求了一个不存在的服务...");
                return bad;
        }
    }

    /**
     * 封装并返回一个包含处理结果消息，当服务完成后统一调用这个方法
     * @param message 客户端发来的消息
     * @param tag 消息所需携带的查询结果
     * @param isSuccess 是否处理成功了
     * @param data 消息携带回发数据，用户客户端那边处理
     * @return 包含处理结果消息
     */
    public static Message ServiceFinish(
            Message message,
            Object tag,
            int isSuccess,
            String data
    ){
        Message result = new Message();
        // 设置公共的参数
        result.setfUserSource(message.getfUserSource());
        result.setHasView(message.getHasView());

        // 根据是否成功封装消息
        if(Message.HANDLER_SUCCESS == isSuccess){
            result.setTag(tag);
            // 设置是否处理成功状态
            result.setMesstype(Message.HANDLER_SUCCESS);
            // 设置返回数据，这里放入需要返回的页面
            result.setData(data);
        } else if (Message.HANDLER_FAILURE == isSuccess){
            result.setMesstype(Message.HANDLER_FAILURE);
            result.setData(data);
        }

        return result;
    }

}
