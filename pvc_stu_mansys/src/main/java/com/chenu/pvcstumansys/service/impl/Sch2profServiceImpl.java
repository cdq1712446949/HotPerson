package com.chenu.pvcstumansys.service.impl;

import com.chenu.pvcstumansys.db.bean.*;
import com.chenu.pvcstumansys.db.bean.noindb.TableDataInfo;
import com.chenu.pvcstumansys.db.mapper.*;
import com.chenu.pvcstumansys.service.Sch2profService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基本作用： 学校专业库（学校-专业映射表）服务实现类
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
@CacheConfig(cacheNames = "sch2prof" /* 本类使用缓存名称：sch2prof */ )
@Service("sch2profService")
public class Sch2profServiceImpl implements Sch2profService {

    /**
     * 请求用户服务的服务路由键
     */
    public static final String SERVICE_ROUTING_KEY = "sch2prof";
    /**
     * 消息服务器的学校专业库消息队列
     */
    public static final String QUEUE = "pvcstumansys.sch2prof.queue";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * dao
     */
    @Autowired
    Sch2profMapper sch2profMapper;
    @Autowired
    ProfessionalMapper professionalMapper;
    @Autowired
    SchoolMapper schoolMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UInfoMapper uInfoMapper;

    /**
     * redis模版，做缓存和处理结果存放
     */
    @Autowired
    private RedisTemplate jsonRedisTemplate;

    public static boolean professionalDataIsUpdate = true;   /* 数据库数据是否被更新了 */

    /**
     * 服务具体功能索引号
     * 每添加一个新的功能，都需要在这里添加上索引号
     */
    public static final int SCH2PROF_LIST = 0;                    /* 得到所有学校专业信息 */
    public static final int SCH2PROF_ADD_OR_EDIT_VIEW = 1;      /* 前往学校专业添加/编辑页面 */
    public static final int SCH2PROF_ADD_OR_UPDATE = 2;         /* 学校专业添加或更新 */
    public static final int SCH2PROF_REMOVE = 3;                /* 学校专业删除 */


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
            case SCH2PROF_LIST:
                logger.info("学校专业库服务收到消息，正在提供 SCH2PROF_LIST 服务...");
                // 查询所需数据
                TableDataInfo<Sch2prof> sch2profData = findAll(message.getfUserSource());
                // 完成
                result = MessageServiceImpl.ServiceFinish(message, sch2profData, Message.HANDLER_SUCCESS, "/sch2prof/man");
                break;

            case SCH2PROF_ADD_OR_EDIT_VIEW:
                logger.info("学校专业库服务收到消息，正在提供 SCH2PROF_ADD_OR_EDIT_VIEW 服务...");
                // 判断是否有id，有：编辑；没有：添加
                Integer editId = null;
                try{
                    editId = Integer.valueOf(message.getData());
                } catch (Exception e){
                    logger.info("id 转换错误，用户前往的是添加页面...");
                }
                Map<String, Object> sch2prof_add_view_data = sch2profAddOrEditView(
                        message.getfUserSource(),           /* 用户源 */
                        editId                              /* 专业id */
                );
                // 完成
                result = MessageServiceImpl.ServiceFinish(message, sch2prof_add_view_data, Message.HANDLER_SUCCESS, "/sch2prof");
                break;

            case SCH2PROF_ADD_OR_UPDATE:
                logger.info("学校专业库服务收到消息，正在提供 SCH2PROF_ADD_OR_UPDATE 服务...");
                // 首先解析消息中传过来的学校-专业映射 json
                try {
                    Sch2prof sch2prof = gson.fromJson(message.getData(), Sch2prof.class);
                    rows = saveOrUpdate(sch2prof);
                    if(rows > 0){
                        TableDataInfo sch2prof_edit_data = findAll(message.getfUserSource());
                        // 完成
                        result = MessageServiceImpl.ServiceFinish(message, sch2prof_edit_data, Message.HANDLER_SUCCESS, "/sch2prof/man");
                    } else if(rows == -1){
                        result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "重复的学校-专业，添加或更新失败...");
                    } else {
                        result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "添加或更新学校-专业失败，请重试...");
                    }
                } catch (Exception e){
                    logger.warn("学校-专业json转换错误...");
                    result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "更新失败，无法解析学校-专业json数据，请检查消息参数...");
                }
                break;

            case SCH2PROF_REMOVE:
                logger.info("学校专业库服务收到消息，正在提供 SCH2PROF_REMOVE 服务...");
                try {
                    Integer rmId = Integer.valueOf(message.getData());
                    rows = remove(rmId);
                    if(rows > 0){
                        TableDataInfo sch2prof_rm_data = findAll(message.getfUserSource());
                        // 完成
                        result = MessageServiceImpl.ServiceFinish(message, sch2prof_rm_data, Message.HANDLER_SUCCESS, "/sch2prof/man");
                    } else {
                        // 完成
                        logger.warn("删除失败，这个学校-专业可能不存在或已经被移除...");
                        result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "删除失败，这个学校-专业可能不存在或已经被移除，请重试...");
                    }
                } catch (Exception e){
                    logger.warn("id 转换错误，未指明要删除学校-专业的id");
                    result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "删除失败，你未指明任何学校-专业的id");
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
     * 查询所有的学校专业映射
     * @param userSource 发消息的用户源
     * @return 查询到的学校专业映射信息数据
     */
    @Override
    public TableDataInfo<Sch2prof> findAll(Integer userSource) {
        TableDataInfo rs = new TableDataInfo<Sch2prof>();
        List<Sch2prof> sch2profList = new ArrayList<>();
        List<Professional> professionalList = new ArrayList<>();
        List<School> schoolList = new ArrayList<>();

        // 先查询用户源
        User source = userMapper.selectByPrimaryKey(userSource);
        if(source != null){
            // 查询uinfo
            UInfo uInfo = uInfoMapper.selectByPrimaryKey(source.getfInfo());
            source.setUinfo(uInfo);
        } else {
            // 都没给用户源，还怎么玩？
            return rs;
        }

        if(source.getRole() == User.ROLE_NOR){
            // 省级管理员：查看所有映射，且能进行crud操作
            if(source.getPrivilege() == User.PRIVILEGE_SHENG){
                // 先把学校专业库映射表查出来
                sch2profList = sch2profMapper.selectAll();
                for(Sch2prof var : sch2profList){
                    var.setSchool(schoolMapper.selectByPrimaryKey(var.getfSchool()));
                    var.setProfessional(professionalMapper.selectByPrimaryKey(var.getfProfessional()));
                }

            }
            // 校级管理员：只能查看自个学校的映射，不能进行任何操作，需要crud，需要提交申请
            else if(source.getPrivilege() == User.PRIVILEGE_XIAO) {
                // 查出学校的所有专业映射
                sch2profList = sch2profMapper.selectBySchool(source.getUinfo().getfSchool());
                for(Sch2prof var : sch2profList){
                    var.setSchool(schoolMapper.selectByPrimaryKey(var.getfSchool()));
                    var.setProfessional(professionalMapper.selectByPrimaryKey(var.getfProfessional()));
                }
            }

        }

        // 设置结果
        rs.setRows(sch2profList);
        rs.setTotal(sch2profList.size());
        // 学校专业映射放入缓存
        jsonRedisTemplate.opsForValue().set("findAll_sch2profList_data", rs);
        // 学校放入缓存
        jsonRedisTemplate.opsForValue().set("findAll_sch2prof_school_data", schoolList);
        // 专业放入缓存
        jsonRedisTemplate.opsForValue().set("findAll_sch2prof_prof_data", professionalList);

        return rs;
    }

    private Map<String, Object> sch2profAddOrEditView(
            Integer userSource,
            Integer id
    ){
        Map<String, Object> rs = new HashMap<>();
        List<School> schoolList = null;
        List<Professional> professionalList = null;
        School school = null;
        Sch2prof editSch2prof = null;

        // 先查询用户源
        User source = userMapper.selectByPrimaryKey(userSource);
        if(source != null){
            // 查询uinfo
            UInfo uInfo = uInfoMapper.selectByPrimaryKey(source.getfInfo());
            source.setUinfo(uInfo);
        } else {
            // 都没给用户源，还怎么玩？
            return rs;
        }

        if(source.getRole() == User.ROLE_NOR) {
            // 省级管理员：添加所有已经存在学校和专业间的映射
            if (source.getPrivilege() == User.PRIVILEGE_SHENG) {
                // 添加；查出所有专业和学校
                if(SchoolServiceImpl.schoolDataIsUpdate == true){
                    schoolList = schoolMapper.selectAll();
                    jsonRedisTemplate.opsForValue().set("findAll_school_data", schoolList);
                    SchoolServiceImpl.schoolDataIsUpdate = false;
                } else {
                    schoolList = (List<School>) jsonRedisTemplate.opsForValue().get("findAll_school_data");
                }
            }
            // 校级管理员
            else if(source.getPrivilege() == User.PRIVILEGE_XIAO) {
                // 查出其自己的学校信息
                school = schoolMapper.selectByPrimaryKey(source.getUinfo().getfSchool());
            }

            // 查出所有专业，放在这是因为不管省管理员或校管理员都需要所有的专业的信息
            if(ProfessionalServiceImpl.professionalDataIsUpdate == true){
                // 数据库被更新过才重新查询
                professionalList = professionalMapper.selectAll();
                jsonRedisTemplate.opsForValue().set("findAll_professional_data", professionalList);
                ProfessionalServiceImpl.professionalDataIsUpdate = false;
            } else {
                professionalList = (List<Professional>) jsonRedisTemplate.opsForValue().get("findAll_professional_data");
            }

            if(id != null){
                // 编辑，查出该学校-专业 映射信息
                editSch2prof = sch2profMapper.selectByPrimaryKey(id);
                editSch2prof.setSchool(schoolMapper.selectByPrimaryKey(editSch2prof.getfSchool()));
                editSch2prof.setProfessional(professionalMapper.selectByPrimaryKey(editSch2prof.getfProfessional()));
            }

        }

        // 设置结果
        rs.put("school", school);
        rs.put("editSch2prof", editSch2prof);
        rs.put("schoolList", schoolList);
        rs.put("professionalList", professionalList);
        // 将结果放入缓存
        jsonRedisTemplate.opsForValue().set("user_source_school", school);
        jsonRedisTemplate.opsForValue().set("editSch2prof", editSch2prof);


        return rs;
    }

    /**
     * 保存或更新一个学校专业映射
     * @param sch2prof
     * @return
     */
    private int saveOrUpdate(
            Sch2prof sch2prof
    ){
        // 先查重
        List<Sch2prof> sch2profList = sch2profMapper.selectAll();
        for(Sch2prof var : sch2profList){
            if(var.equals(sch2prof)){
                // 重复，返回 -1
                return -1;
            }
        }

        // 根据是否有id决定是保存还是更新
        if(sch2prof.getpSchProfId() == null){
            return save(sch2prof);
        } else {
            return update(sch2prof);
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public int remove(Integer id) {
        professionalDataIsUpdate = true;
        return sch2profMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int save(Sch2prof record) {
        professionalDataIsUpdate = true;
        return sch2profMapper.insertSelective(record);
    }

    @Override
    @Cacheable(
            key = "#id",                                /* 缓存key使用id */
            cacheManager = "jsonRedisCacheManager",     /* 使用json缓存管理器 */
            unless = "#result == null"                  /* 当没查到时，不缓存 */
    )
    public Sch2prof findById(Integer id) {
        return sch2profMapper.selectByPrimaryKey(id);
    }

    @Override
    @CachePut(key = "#record.pSchProfId")
    public int update(Sch2prof record) {
        professionalDataIsUpdate = true;
        return sch2profMapper.updateByPrimaryKeySelective(record);
    }
}
