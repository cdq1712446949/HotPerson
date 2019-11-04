package com.chenu.pvcstumansys.service.impl;

import com.chenu.pvcstumansys.db.bean.Apply;
import com.chenu.pvcstumansys.db.bean.Message;
import com.chenu.pvcstumansys.db.bean.Reply;
import com.chenu.pvcstumansys.db.bean.User;
import com.chenu.pvcstumansys.db.bean.noindb.TableDataInfo;
import com.chenu.pvcstumansys.db.mapper.MessageMapper;
import com.chenu.pvcstumansys.db.mapper.ReplyMapper;
import com.chenu.pvcstumansys.db.mapper.UserMapper;
import com.chenu.pvcstumansys.service.ReplyService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 基本作用：申请回复服务实现类
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/4
 */
@CacheConfig(cacheNames = "reply" /* 本类使用缓存名称：reply */ )
@Service("replyService")
public class ReplyServiceImpl implements ReplyService {

    /**
     * 请求申请回复服务的服务路由键
     */
    public static final String SERVICE_ROUTING_KEY = "reply";

    /**
     * 消息服务器的申请回复消息队列
     */
    public static final String QUEUE = "pvcstumansys.replyservice.queue";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static boolean dataIsUpdate = true;   /* 数据库数据是否被更新了 */

    /**
     * dao
     */
    @Autowired
    private ReplyMapper replyMapper;
    @Autowired
    private MessageMapper messageMapper;

    /**
     * redis模版，做缓存和处理结果存放
     */
    @Autowired
    private RedisTemplate jsonRedisTemplate;

    /**
     * 服务具体功能索引号
     * 每添加一个新的功能，都需要在这里添加上索引号
     */
    public static final int REPLY_LIST = 0;                     /* 得到所有申请回复 */
    public static final int REPLY_REMOVE = 1;                /* 申请回复删除 */
    public static final int REPLY_DESC = 2;                 /* 申请回复详情 */
    public static final int REPLY_CLEAR = 3;                 /* 申请回复详情 */


    @Override
    @RabbitListener(queues = QUEUE)
    public Message service(Message message) {
        Message result = null;
        Gson gson = new Gson();
        int rows = 0;

        // 根据消息类型处理
        switch (message.getMesstype()){

            case REPLY_LIST:
                logger.info("申请回复服务收到消息，正在提供 REPLY_LIST 服务...");
                TableDataInfo<Reply> replyTableDataInfo = findAll(message.getfUserSource());
                result = MessageServiceImpl.ServiceFinish(message, replyTableDataInfo, Message.HANDLER_SUCCESS, "/");
                break;

            case REPLY_REMOVE:
                logger.info("申请回复服务收到消息，正在提供 REPLY_REMOVE 服务...");
                try {
                    // 得到id
                    Integer del_id = Integer.valueOf(message.getData());
                    rows = remove(del_id);
                    if(rows > 0){
                        TableDataInfo<Reply> rmDataInfo = findAll(message.getfUserSource());
                        result = MessageServiceImpl.ServiceFinish(message, rmDataInfo, Message.HANDLER_SUCCESS, "/");
                    } else {
                        result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "申请删除失败，请重试...");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    logger.warn("申请删除服务错误，id转换错误...");
                    result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "申请删除服务错误，没有id...");
                }
                break;

            case REPLY_CLEAR:
                logger.info("申请回复服务收到消息，正在提供 REPLY_CLEAR 服务...");
                clear();
                TableDataInfo<Reply> rmDataInfo = findAll(message.getfUserSource());
                result = MessageServiceImpl.ServiceFinish(message, rmDataInfo, Message.HANDLER_SUCCESS, "/");
                break;


            default:
                logger.warn("服务：你正在请求一个不存在的功能服务...");
                result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "服务失败，你请求了一个不存在的功能服务...");
                break;
        }

        return result;
    }

    /**
     * 删除所有
     */
    private void clear() {
        List<Reply> replyList = replyMapper.selectAll();
        for(Reply var : replyList){
            replyMapper.deleteByPrimaryKey(var.getpReplid());
        }
    }

    /**
     * 查询所有
     */
    private TableDataInfo<Reply> findAll(Integer userSource) {
        TableDataInfo rs = new TableDataInfo<Reply>();
        List<Reply> replyList = new ArrayList<>();

        // 每个用户只能查看自己的申请回复
        replyList = replyMapper.selectAllByUserSource(userSource);

        // 设置结果
        rs.setRows(replyList);
        rs.setTotal(replyList.size());
        return rs;
    }

    /**
     * 回复一条消息，这个方法一般跟在申请处理完成后
     * 当然，我们也可以通过这个方法直接给一个用户发送通知
     */
    @Override
    public void reply(
            Integer messageId,
            String note,
            byte beViewed
    ){
        // 设置回复
        Reply reply = new Reply();
        reply.setfMessage(messageId);
        reply.setNote(note);
        reply.setBeViewed(beViewed);
        // 保存即可
        save(reply);
    }

    @Override
    public int remove(Integer id) {
        dataIsUpdate = true;
        int rows = 0;
        Reply reply = replyMapper.selectByPrimaryKey(id);
        rows = replyMapper.deleteByPrimaryKey(id);
        return rows;
    }

    @Override
    public int save(Reply record) {
        dataIsUpdate = true;
        return replyMapper.insertSelective(record);
    }

    @Override
    public Reply findById(Integer id) {
        return replyMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(Reply record) {
        dataIsUpdate = true;
        return replyMapper.updateByPrimaryKeySelective(record);
    }
}
