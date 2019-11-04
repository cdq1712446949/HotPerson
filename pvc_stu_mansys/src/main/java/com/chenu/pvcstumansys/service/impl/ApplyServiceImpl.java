package com.chenu.pvcstumansys.service.impl;

import com.chenu.pvcstumansys.controller.MessageController;
import com.chenu.pvcstumansys.db.bean.*;
import com.chenu.pvcstumansys.db.bean.noindb.TableDataInfo;
import com.chenu.pvcstumansys.db.mapper.*;
import com.chenu.pvcstumansys.service.ApplyService;
import com.chenu.pvcstumansys.service.MessageService;
import com.chenu.pvcstumansys.service.ReplyService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基本作用：申请服务实现类
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/3
 */
@CacheConfig(cacheNames = "apply" /* 本类使用缓存名称：apply */ )
@Service("applyService")
public class ApplyServiceImpl implements ApplyService {

    /**
     * 请求申请服务的服务路由键
     */
    public static final String SERVICE_ROUTING_KEY = "apply";

    /**
     * 消息服务器的申请消息队列
     */
    public static final String QUEUE = "pvcstumansys.applyservice.queue";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static boolean dataIsUpdate = true;   /* 数据库数据是否被更新了 */

    /**
     * dao
     */
    @Autowired
    private ApplyMapper applyMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UInfoMapper uInfoMapper;
    @Autowired
    private SchoolMapper schoolMapper;
    @Autowired
    private ProfcategoryMapper profcategoryMapper;
    @Autowired
    private ProfessionalMapper professionalMapper;
    @Autowired
    private ReplyMapper replyMapper;
    @Autowired
    private StudentMapper studentMapper;

    /**
     * 消息服务和回复服务，因为请求服务需要内部消化消息和回复消息
     */
    @Autowired
    MessageService messageService;
    @Autowired
    ReplyService replyService;


    /**
     * redis模版，做缓存和处理结果存放
     */
    @Autowired
    private RedisTemplate jsonRedisTemplate;

    /**
     * gson
     */
    private Gson gson = new Gson();

    /**
     * 服务具体功能索引号
     * 每添加一个新的功能，都需要在这里添加上索引号
     */
    public static final int APPLY_LIST = 0;                  /* 得到所有申请信息 */
    public static final int APPLY_ADD = 1;                     /* 申请添加 */
    public static final int APPLY_REMOVE = 2;                /* 申请删除 */
    public static final int APPLY_DESC = 3;                 /* 申请详情 */
    public static final int APPLY_ACCESS= 4;                /* 申请通过/同意申请 */
    public static final int APPLY_REJECT = 5;                /* 申请不通过/拒绝申请 */


    @Override
    @RabbitListener(queues = QUEUE)
    public Message service(Message message) {
        Message result = null;
        int rows = 0;

        // 根据消息类型处理
        switch (message.getMesstype()){

            case APPLY_LIST:
                logger.info("申请服务收到消息，正在提供 APPLY_LIST 服务...");
                Integer state = 0;
                try{
                    // 解析，得到申请处理状态条件
                    state = Integer.valueOf(message.getData());
                } catch (Exception e){
                    logger.warn("申请处理状态条件转换错误，已使用默认值0...");
                }
                TableDataInfo<Apply> applyTableDataInfo = findAll(message.getfUserSource(), state);
                result = MessageServiceImpl.ServiceFinish(message, applyTableDataInfo, Message.HANDLER_SUCCESS, "/apply/list");
                break;

            case APPLY_ADD:
                logger.info("申请服务收到消息，正在提供 APPLY_ADD 服务...");
                // 首先解析消息中传过来的申请json
                try {
                    Apply apply = gson.fromJson(message.getData(), Apply.class);
                    logger.info("请求的申请 --> " + apply.toString());
                    rows = applyAdd(apply);
                    if(rows > 0){
                        jsonRedisTemplate.opsForValue().set("apply_ok_msg", "申请请求发送成功，请耐心等待处理结果...");
                        result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_SUCCESS, "/apply/ok");
                    } else {
                        result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "添加申请失败，请重试...");
                    }
                } catch (Exception e){
                    logger.warn("申请json转换错误...");
                    e.printStackTrace();
                    result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "请求申请失败，无法解析申请json数据，请检查消息参数...");
                }
                break;

            case APPLY_REMOVE:
                logger.info("申请服务收到消息，正在提供 APPLY_REMOVE 服务...");
                try {
                    // 得到id
                    Integer del_id = Integer.valueOf(message.getData());
                    rows = remove(del_id);
                    if(rows > 0){
                        // 成功
                        TableDataInfo<Apply> access_findAll = findAll(message.getfUserSource(), (Integer) jsonRedisTemplate.opsForValue().get("findAll_apply_state"));
                        result = MessageServiceImpl.ServiceFinish(message, access_findAll, Message.HANDLER_SUCCESS, "/apply/list");
                    } else {
                        result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "申请删除失败，请重试...");
                    }
                } catch (Exception e){
                    logger.warn("申请删除服务错误，id转换错误...");
                    result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "申请删除服务错误，没有id...");
                }
                break;

            case APPLY_DESC:
                logger.info("申请服务收到消息，正在提供 APPLY_DESC 服务...");
                try {
                    // 得到申请id
                    Integer desc_id = Integer.valueOf(message.getData());
                    Map<String, Object> desc_data = descApply(message.getfUserSource(), desc_id);
                    result = MessageServiceImpl.ServiceFinish(message, desc_data, Message.HANDLER_SUCCESS, "/apply/desc");
                } catch (Exception e){
                    e.printStackTrace();
                    logger.warn("申请详情服务错误，没有id");
                    result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "请求申请详情失败，id转换错误...");
                }
                break;

            case APPLY_ACCESS:
                logger.info("申请服务收到消息，正在提供 APPLY_ACCESS 服务...");
                try {
                    // 得到申请id
                    Integer acc_id = Integer.valueOf(message.getData());
                    accessApply(message.getfUserSource(), acc_id);

                    TableDataInfo<Apply> access_findAll = findAll(message.getfUserSource(), (Integer) jsonRedisTemplate.opsForValue().get("findAll_apply_state"));
                    result = MessageServiceImpl.ServiceFinish(message, access_findAll, Message.HANDLER_SUCCESS, "/apply/list");
                } catch (Exception e){
                    logger.warn("同意申请服务错误，没有id");
                    result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "同意申请失败，id转换错误...");
                }
                break;

            case APPLY_REJECT:
                logger.info("申请服务收到消息，正在提供 APPLY_REJECT 服务...");
                try {
                    // 得到申请id
                    Integer rej_id = Integer.valueOf(message.getData());
                    rejectApply(message.getfUserSource(), rej_id);
                    TableDataInfo<Apply> reject_findAll = findAll(message.getfUserSource(), (Integer) jsonRedisTemplate.opsForValue().get("findAll_apply_state"));
                    result = MessageServiceImpl.ServiceFinish(message, reject_findAll, Message.HANDLER_SUCCESS, "/apply/list");
                } catch (Exception e){
                    logger.warn("拒绝申请服务错误，没有id");
                    result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "拒绝申请失败，id转换错误...");
                }
                break;

            default:
                logger.warn("服务：你正在请求一个不存在的功能服务...");
                result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "服务失败，你请求了一个不存在的功能服务...");
                break;
        }
        // 提供服务完毕，返回一条处理结果消息
        return result;
    }

    /**
     * 申请添加
     */
    private int applyAdd(Apply apply) {
        // 如果申请的是添加/修改学籍信息，需要进行上传头像的特殊处理
        Message message = apply.getMessage();
        if(message.getToService() == MessageServiceImpl.STUDENT_SERVER && message.getMesstype() == StudentServiceImpl.STUDENT_ADD_OR_UPDATE){
            // 保存头像，文件名称以学生学籍号命名
            Student student = gson.fromJson(message.getData(), Student.class);
            String filename = student.getfSchool() + student.getCode() + ".flyan";
            logger.warn("保存文件名称 --> " + filename);
            if(MessageController.UPLOAD_FILE != null){
                try {
                    MessageController.UPLOAD_FILE.transferTo(new File(filename));
                    // 设置学生的头像属性
                    student.setPicture(filename);
                } catch (IOException e) {
                    e.printStackTrace();
                    return 0;
                }
                message.setData(gson.toJson(student));
                // 别忘了处理完毕，将消息封装回去
                apply.setMessage(message);
            } else {
                return 0;
            }
        }

        return save(apply);
    }

    /**
     * 拒绝一条申请
     */
    private void rejectApply(
            Integer userSource,
            Integer id
    ) {
        // 很简单，设置这个申请的状态为已处理 和 处理结果为拒绝 即可
        Apply apply = applyMapper.selectByPrimaryKey(id);
        apply.setState((byte)1);
        apply.setResult((byte)0);
        // 更新
        update(apply);
        // 回复
        replyService.reply(apply.getfMessage(), "很抱歉，您的申请被拒绝了，请联系省管理员或者重试！", (byte) 0);
    }


    /**
     * 同意一条申请
     */
    private void accessApply(
            Integer userSource,
            Integer id
    ) {
        // 将这条消息以内部方式消化处理掉
        Apply apply = applyMapper.selectByPrimaryKey(id);
        Message message = messageMapper.selectByPrimaryKey(apply.getfMessage());
        messageService.sendAndReceive(message, message.getToService());
        // 然后，设置这个申请的状态为已处理 和 处理结果为同意 即可
        apply.setState((byte)1);
        apply.setResult((byte)1);
        // 更新
        update(apply);
        // 回复
        replyService.reply(apply.getfMessage(), "恭喜您，您的申请被同意并通过了！", (byte) 0);
    }

    /**
     * 申请详情处理
     */
    private Map<String, Object> descApply(
            Integer userSource,
            Integer id
    ) {
        Map<String, Object> rs = new HashMap<>();
        Apply apply = null;

        // 先查询用户源
        User source = userMapper.selectByPrimaryKey(userSource);

        // 只有省级管理员才能有权使用此功能
        if(source.getPrivilege() == User.PRIVILEGE_SHENG && source.getRole() == User.ROLE_NOR){
            // 查询申请
            apply = applyMapper.selectByPrimaryKey(id);
            // 查询申请所有的详细信息
            Message message = messageMapper.selectByPrimaryKey(apply.getfMessage());
            User user = userMapper.selectByPrimaryKey(message.getfUserSource());
            UInfo uInfo = uInfoMapper.selectByPrimaryKey(user.getfInfo());
            School school = schoolMapper.selectByPrimaryKey(uInfo.getfSchool());
            uInfo.setSchool(school);
            user.setUinfo(uInfo);
            message.setUserSource(user);
            apply.setMessage(message);
            // 根据请求服务不同，解析不同的json数据
            json2ObjectAndPut2Redis(message);
            // 设置备注信息
            setNote(apply);
        }

        // 设置结果
        rs.put("apply", apply);
        // 设置缓冲区
        jsonRedisTemplate.opsForValue().set("apply", apply);

        return rs;
    }

    /**
     * 解析消息中的data json数据为对象，并放到redis缓存中
     */
    private void json2ObjectAndPut2Redis(Message message) {
        Gson gson = new Gson();
        Object data = null;
        Integer id = null;

        switch (message.getToService()){

            case MessageServiceImpl.PROFESSIONAL_SERVER:
                switch (message.getMesstype()){
                    case ProfessionalServiceImpl.PROFESSIONAL_ADD_OR_UPDATE:
                        Professional professional = gson.fromJson(message.getData(), Professional.class);
                        professional.setProfcategory(profcategoryMapper.selectByPrimaryKey(professional.getfProfcategory()));
                        data = professional;
                        break;
                }
            break;

            case MessageServiceImpl.STUDENT_SERVER:
                Student student = null;
                switch (message.getMesstype()){
                    case StudentServiceImpl.STUDENT_ADD_OR_UPDATE:
                        student = gson.fromJson(message.getData(), Student.class);
                        student.setSchool(schoolMapper.selectByPrimaryKey(student.getfSchool()));
                        student.setProfessional(professionalMapper.selectByPrimaryKey(student.getfProfessional()));
//                        logger.warn(student.toString());
                        data = student;
                        break;

                    case StudentServiceImpl.STUDENT_REMOVE:
                        student = studentMapper.selectByPrimaryKey(message.getData());
                        student.setSchool(schoolMapper.selectByPrimaryKey(student.getfSchool()));
                        student.setProfessional(professionalMapper.selectByPrimaryKey(student.getfProfessional()));
                        data = student;
                        break;

                }
            break;

        }

        // 缓存
        jsonRedisTemplate.opsForValue().set("desc_apply_data", data);
        jsonRedisTemplate.opsForValue().set("desc_apply_id", id);

    }

    /**
     * 查询所有，根据条件处理状态
     * @param userSource 发消息的用户源
     * @param state 查询条件，处理状态，0：未处理；1：已处理；其他值：都可以
     */
    private TableDataInfo<Apply> findAll(
            Integer userSource,
            Integer state           /* 处理状态 */
    ){
        TableDataInfo rs = new TableDataInfo<Apply>();
        List<Apply> applyList = new ArrayList<>();

        // 先查询用户源
        User source = userMapper.selectByPrimaryKey(userSource);

        // 只有省级管理员才能有权使用此功能
        if(source.getPrivilege() == User.PRIVILEGE_SHENG && source.getRole() == User.ROLE_NOR){

            if(state == 0 || state == 1){
                // 有条件
                applyList = applyMapper.selectAllByState(state);
            } else {
                // 无条件，查询所有申请消息
                applyList = applyMapper.selectAll();
            }
            // 设置请求的详细信息
            for (Apply var : applyList){
                Message message = messageMapper.selectByPrimaryKey(var.getfMessage());
                User user = userMapper.selectByPrimaryKey(message.getfUserSource());
                message.setUserSource(user);
                var.setMessage(message);
            }
            // 设置备注信息
            setNote(applyList);
        }

        // 设置结果
        rs.setRows(applyList);
        rs.setTotal(applyList.size());
        // 缓存区
        jsonRedisTemplate.opsForValue().set("applyData", rs);
        jsonRedisTemplate.opsForValue().set("findAll_apply_state", state);

        return rs;
    }

    /**
     * 查询所有申请消息
     */
    private TableDataInfo<Apply> findAll(
            Integer userSource
    ){
        return findAll(userSource, -1);
    }

    /**
     * 根据申请设置提示备注信息
     */
    private void setNote(Apply apply){
        Message message = apply.getMessage();
        // 首先，我们得知道，这条申请是请求的什么服务
        switch (message.getToService()){

            case MessageServiceImpl.PROFESSIONAL_SERVER:

                switch (message.getMesstype()){
                    case ProfessionalServiceImpl.PROFESSIONAL_ADD_OR_UPDATE:
                        apply.setNote("添加/更新一个专业");
                        break;
                }
                break;

            case MessageServiceImpl.STUDENT_SERVER:
                switch (message.getMesstype()){
                    case StudentServiceImpl.STUDENT_ADD_OR_UPDATE:
                        apply.setNote("添加/更新一个学籍信息");
                        break;

                    case StudentServiceImpl.STUDENT_REMOVE:
                        apply.setNote("删除一个学生学籍信息");
                        break;
                }
                break;

            default:
                logger.warn("申请无效...");
                apply.setNote("无效的申请，请清理...");
                break;

        }
    }

    /**
     * 根据申请列表设置其备注信息
     */
    private void setNote(List<Apply> applyList){

        for(Apply var : applyList){
            setNote(var);
        }

    }

    /**
     * 保存或更新
     */
    private int saveOrUpdate(
            Apply apply
    ){
        // 根据是否有id决定是保存还是更新
        if(apply.getpApplid() == null){
            return save(apply);
        } else {
            return update(apply);
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public int remove(Integer id) {
        dataIsUpdate = true;
        // 这里注意，只删除申请，不删除申请里的请求消息，因为这个消息还会被回复引用，需要回复服务去删除
        return applyMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int save(Apply record) {
        int rows = 0;
        dataIsUpdate = true;
        // 首先，先保存消息
        messageMapper.insertSelective(record.getMessage());
        // 然后，再保存申请
        record.setfMessage((Integer) messageMapper.selectNewId());
        rows = applyMapper.insertSelective(record);

        return rows;
    }

    @Override
    @Cacheable(
            key = "#id",                                /* 缓存key使用id */
            cacheManager = "jsonRedisCacheManager",     /* 使用json缓存管理器 */
            unless = "#result == null"                  /* 当没查到时，不缓存 */
    )
    public Apply findById(Integer id) {
        return applyMapper.selectByPrimaryKey(id);
    }

    @Override
    @CachePut(key = "#record.pApplid")
    public int update(Apply record) {
        dataIsUpdate = true;
        return applyMapper.updateByPrimaryKeySelective(record);
    }
}
