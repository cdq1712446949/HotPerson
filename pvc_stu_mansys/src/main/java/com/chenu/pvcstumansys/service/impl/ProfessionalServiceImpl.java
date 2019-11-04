package com.chenu.pvcstumansys.service.impl;

import com.chenu.pvcstumansys.db.bean.*;
import com.chenu.pvcstumansys.db.bean.noindb.TableDataInfo;
import com.chenu.pvcstumansys.db.mapper.*;
import com.chenu.pvcstumansys.service.ProfessionalService;
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

import java.util.ArrayList;
import java.util.List;


/**
 * 基本作用：专业服务实现类
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
@CacheConfig(cacheNames = "prof" /* 本类使用缓存名称：prof */ )
@Service("professionalService")
public class ProfessionalServiceImpl implements ProfessionalService {

    /**
     * 请求用户服务的服务路由键
     */
    public static final String SERVICE_ROUTING_KEY = "prof";
    /**
     * 消息服务器的专业消息队列
     */
    public static final String QUEUE = "pvcstumansys.professionalservice.queue";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * dao
     */
    @Autowired
    ProfessionalMapper professionalMapper;
    @Autowired
    ProfcategoryMapper profcategoryMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UInfoMapper uInfoMapper;

    public static boolean professionalDataIsUpdate = true;   /* 数据库数据是否被更新了 */

    /**
     * redis模版，做缓存和处理结果存放
     */
    @Autowired
    private RedisTemplate jsonRedisTemplate;


    /**
     * 服务具体功能索引号
     * 每添加一个新的功能，都需要在这里添加上索引号
     */
    public static final int PROFESSIONAL_LIST = 0;                    /* 得到所有专业信息 */
    public static final int PROFESSIONAL_ADD_OR_EDIT_VIEW = 1;      /* 前往专业添加/编辑页面 */
    public static final int PROFESSIONAL_ADD_OR_UPDATE = 2;         /* 专业添加或更新 */
    public static final int PROFESSIONAL_REMOVE = 3;                /* 专业删除 */

    /**
     * 处理客户端发来的消息，提供服务，所有的服务都需要在这里注册，并规范化
     * 服务完成后，返回一个包含处理结果的消息，这个消息将回发到最初需要服务的例程
     * @param message 客户端发来的消息
     * @return 包含处理结果的消息
     */
    @Override
    @RabbitListener(queues = QUEUE)
    public Message service(Message message) {
        Message result = null;
        Gson gson = new Gson();
        int rows = 0;

        // 根据消息类型处理
        switch (message.getMesstype()){
            case PROFESSIONAL_LIST:
                logger.info("专业服务收到消息，正在提供 PROFESSIONAL_LIST 服务...");
                TableDataInfo<Professional> professionalData = findAll(message.getfUserSource());
                // 完成
                result = MessageServiceImpl.ServiceFinish(message, professionalData, Message.HANDLER_SUCCESS, "/prof/man");
                break;

            case PROFESSIONAL_ADD_OR_EDIT_VIEW:
                logger.info("专业服务收到消息，正在提供 PROFESSIONAL_ADD_OR_EDIT_VIEW 服务...");
                // 判断是否有id，有：编辑；没有：添加
                Integer editId = null;
                try{
                    editId = Integer.valueOf(message.getData());
                } catch (Exception e){
                    logger.info("id 转换错误，用户前往的是添加页面...");
                }
                TableDataInfo prof_add_view_data = profAddOrEditView(
                        message.getfUserSource(),           /* 用户源 */
                        editId                              /* 专业id */
                );
                // 完成
                result = MessageServiceImpl.ServiceFinish(message, prof_add_view_data, Message.HANDLER_SUCCESS, "/prof");
                break;

            case PROFESSIONAL_ADD_OR_UPDATE:
                logger.info("专业服务收到消息，正在提供 PROFESSIONAL_ADD_OR_UPDATE 服务...");
                // 首先解析消息中传过来的专业json
                try {
                    Professional professional = gson.fromJson(message.getData(), Professional.class);
                    rows = saveOrUpdate(professional);
                    if(rows > 0){
                        TableDataInfo prof_edit_data = findAll(message.getfUserSource());
                        // 完成
                        result = MessageServiceImpl.ServiceFinish(message, prof_edit_data, Message.HANDLER_SUCCESS, "/prof/man");
                    } else {
                        result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "添加或更新专业失败，请重试...");
                    }
                } catch (Exception e){
                    logger.warn("专业json转换错误...");
                    result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "更新失败，无法解析专业json数据，请检查消息参数...");
                }
                break;

            case PROFESSIONAL_REMOVE:
                logger.info("专业服务收到消息，正在提供 PROFESSIONAL_REMOVE 服务...");
                try {
                    Integer rmId = Integer.valueOf(message.getData());
                    rows = remove(rmId);
                    if(rows > 0){
                        TableDataInfo prof_rm_data = findAll(message.getfUserSource());
                        // 完成
                        result = MessageServiceImpl.ServiceFinish(message, prof_rm_data, Message.HANDLER_SUCCESS, "/prof/man");
                    } else {
                        // 完成
                        logger.warn("删除失败，这个专业可能不存在或已经被移除...");
                        result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "删除失败，这个专业可能不存在或已经被移除，请重试...");
                    }
                } catch (Exception e){
                    logger.warn("id 转换错误，未指明要删除专业的id");
                    result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "删除失败，你未指明任何专业id");
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
     * 查询所有专业
     * @param userSource 发消息的用户源
     * @return 查询到的专业数据
     */
    @Override
    public TableDataInfo<Professional> findAll(Integer userSource) {
        TableDataInfo rs = new TableDataInfo<Professional>();
        List<Professional> professionalList = new ArrayList<>();

        // 先查询用户源
        User source = userMapper.selectByPrimaryKey(userSource);

        if(source.getRole() == User.ROLE_NOR) {

            if(source.getPrivilege() == User.PRIVILEGE_SHENG){
                logger.info("===");
                // 省级管理员
                // 查询所有的专业数据
                List<Professional> profTemp = professionalMapper.selectAll();
                // 别忘了，查询专业的类别信息
                for(Professional prof : profTemp){
                    Profcategory profcategory = profcategoryMapper.selectByPrimaryKey(prof.getfProfcategory());
                    prof.setProfcategory(profcategory);
                    professionalList.add(prof);
                }
            }

        }
        // 将结果放入缓存
        rs.setRows(professionalList);
        rs.setTotal(professionalList.size());
        jsonRedisTemplate.opsForValue().set("findAll_professionalList_data", rs);

        return rs;
    }

    /**
     * 专业添加/编辑页面
     * @return
     */
    private TableDataInfo<Profcategory> profAddOrEditView(
            Integer source,         /* 用户源 */
            Integer profId           /* 专业的id */
    ){
        TableDataInfo rs = new TableDataInfo<Profcategory>();
        List<Profcategory> profcategories = null;
        Professional professional = null;

        // 放入所有专业类别的数据
        if(ProfcategoryServiceImpl.schoolDataIsUpdate){
            // 如果数据库数据被更新了，我们再从数据库重新获取
            profcategories = profcategoryMapper.selectAll();
            // 将结果放入缓存
            rs.setRows(profcategories);
            rs.setTotal(profcategories.size());
            jsonRedisTemplate.opsForValue().set("findAll_profcategories_data", rs);
            ProfcategoryServiceImpl.schoolDataIsUpdate = false;
        } else {
            // 数据库没更新
            rs = (TableDataInfo<Profcategory>) jsonRedisTemplate.opsForValue().get("findAll_profcategories_data");
        }
        // 放入要编辑的专业
        // 如果有专业id，则说明是前往编辑页面，查询该专业的信息
        if(profId != null){
            professional = professionalMapper.selectByPrimaryKey(profId);
            Profcategory profcategory = profcategoryMapper.selectByPrimaryKey(professional.getfProfcategory());
            professional.setProfcategory(profcategory);
        }
        jsonRedisTemplate.opsForValue().set("prof_edit_view_prof_data", professional);

        return rs;
    }

    /**
     * 保存或更新一个专业
     * @param professional
     * @return
     */
    private int saveOrUpdate(
            Professional professional
    ){
        // 根据是否有id决定是保存还是更新
        if(professional.getpProfid() == null){
            return save(professional);
        } else {
            return update(professional);
        }
    }


    @Override
    @CacheEvict(key = "#pProfid")
    public int remove(Integer pProfid) {
        professionalDataIsUpdate = true;
        return professionalMapper.deleteByPrimaryKey(pProfid);
    }

    @Override
    public int save(Professional record) {
        professionalDataIsUpdate = true;
        return professionalMapper.insertSelective(record);
    }

    @Override
    @Cacheable(
            key = "#pProfid",                              /* 缓存key使用专业的id */
            cacheManager = "jsonRedisCacheManager",     /* 使用json缓存管理器 */
            unless = "#result == null"                  /* 当没查到时，不缓存 */
    )
    public Professional findById(Integer pProfid) {
        return professionalMapper.selectByPrimaryKey(pProfid);
    }

    @Override
    @CachePut(key = "#record.pProfid")
    public int update(Professional record) {
        professionalDataIsUpdate = true;
        return professionalMapper.updateByPrimaryKeySelective(record);
    }
}
