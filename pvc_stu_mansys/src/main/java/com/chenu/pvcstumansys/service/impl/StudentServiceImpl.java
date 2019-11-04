package com.chenu.pvcstumansys.service.impl;

import com.chenu.pvcstumansys.db.bean.*;
import com.chenu.pvcstumansys.db.bean.noindb.TableDataInfo;
import com.chenu.pvcstumansys.db.mapper.*;
import com.chenu.pvcstumansys.service.StudentService;
import com.chenu.pvcstumansys.utils.IdNumberUtils;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基本作用：学籍服务实现类
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/4
 */
@CacheConfig(cacheNames = "stu" /* 本类使用缓存名称：stu */ )
@Service("studentService")
public class StudentServiceImpl implements StudentService {

    /**
     * 请求学籍服务的服务路由键
     */
    public static final String SERVICE_ROUTING_KEY = "student";
    /**
     * 消息服务器的学籍消息队列
     */
    public static final String QUEUE = "pvcstumansys.studentservice.queue";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * dao
     */
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SchoolMapper schoolMapper;
    @Autowired
    private ProfessionalMapper professionalMapper;
    @Autowired
    private UInfoMapper uInfoMapper;
    @Autowired
    private Sch2profMapper sch2profMapper;

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
    public static final int STUDENT_LIST = 0;                    /* 得到所有学籍信息 */
    public static final int STUDENT_ADD_OR_EDIT_VIEW = 1;      /* 前往学籍添加/编辑页面 */
    public static final int STUDENT_ADD_OR_UPDATE = 2;         /* 学籍添加或更新 */
    public static final int STUDENT_REMOVE = 3;                /* 学籍删除 */

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

            case STUDENT_LIST:
                logger.info("学籍服务收到消息，正在提供 STUDENT_LIST 服务...");
                TableDataInfo<Student> studentData = findAll(message.getfUserSource());
                // 完成
                result = MessageServiceImpl.ServiceFinish(message, studentData, Message.HANDLER_SUCCESS, "/student/list");
                break;

            case STUDENT_ADD_OR_EDIT_VIEW:
                logger.info("学籍服务收到消息，正在提供 STUDENT_ADD_OR_EDIT_VIEW 服务...");
                // 判断是否有id，有：编辑/查看；没有：添加
                String editId = message.getData();
                if(StringUtils.isEmpty(editId)){
                    // 如果得到的id是空字符串，说明不是一个正确的id
                    editId = null;
                }
                Map<String, Object> student_add_view_data = studentAddOrEditView(
                        message.getfUserSource(),           /* 用户源 */
                        editId                              /* id */
                );
                // 完成
                result = MessageServiceImpl.ServiceFinish(message, student_add_view_data, Message.HANDLER_SUCCESS, "/student");
                break;

            case STUDENT_ADD_OR_UPDATE:
                logger.info("学籍服务收到消息，正在提供 STUDENT_ADD_OR_UPDATE 服务...");
                // 首先解析学校json
                try {
                    Student student = gson.fromJson(message.getData(), Student.class);
                    rows = saveOrUpdate(student);
                    if(rows > 0){
                        TableDataInfo stu_data = findAll(message.getfUserSource());
                        // 完成
                        result = MessageServiceImpl.ServiceFinish(message, stu_data, Message.HANDLER_SUCCESS, "/student/list");
                    } else if(rows == -1){
                        result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "重复的学籍信息，添加或更新失败...");
                    } else {
                        result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "添加或更新学籍失败，请重试...");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    logger.warn("学籍json转换错误...");
                    result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "更新失败，无法解析学籍json数据，请检查消息参数...");
                }
                break;

            case STUDENT_REMOVE:
                logger.info("学籍服务收到消息，正在提供 STUDENT_REMOVE 服务...");
                String rmId = message.getData();
                if(!StringUtils.isEmpty(rmId)){
                    rows = remove(rmId);
                    if (rows > 0){
                        TableDataInfo<Student> rmData = findAll(message.getfUserSource());
                        // 完成
                        result = MessageServiceImpl.ServiceFinish(message, rmData, Message.HANDLER_SUCCESS, "/student/list");
                    } else {
                        logger.warn("删除失败，这个学生可能不存在或已经被移除...");
                        result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "删除失败，这个学校-这个学生可能不存在或已经被移除，请重试...");
                    }
                } else {
                    logger.warn("未指明删除学籍的id或并未给出");
                    result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "删除失败，未指明删除学籍的id或并未给出...");
                }
                break;

            default:
                logger.warn("服务：你正在请求一个不存在的功能服务...");
                result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "服务失败，你请求了一个不存在的功能服务...");
                break;
        }

        return result;
    }

    /**
     * 添加/更新一个学籍信息
     * @param student
     * @return
     */
    private int saveOrUpdate(Student student) {
        // 根据是否有id决定是保存还是更新
        if(student.getpStuid() == null){
            return save(student);
        } else {
            return update(student);
        }
    }

    /**
     * 添加/编辑页面
     */
    private Map<String, Object> studentAddOrEditView(
            Integer userSource,
            String editId
    ) {
        Map<String, Object> rs = new HashMap<>();
        List<Professional> professionalList = new ArrayList<>();
        School school = null;
        Student editStudent = null;

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

            // 校级管理员：只能申请添加编辑自己学校的学生学籍
            if(source.getPrivilege() == User.PRIVILEGE_XIAO) {
                // 查出其自己的学校信息
                school = schoolMapper.selectByPrimaryKey(source.getUinfo().getfSchool());
                // 查出所有本学校的所有专业

                List<Sch2prof> sch2profList = sch2profMapper.selectBySchool(source.getUinfo().getfSchool());
                for(Sch2prof var : sch2profList){
                    professionalList.add(professionalMapper.selectByPrimaryKey(var.getfProfessional()));
                }
            }

            // 有id，是编辑，查出该学生学籍
            if(editId != null){
                editStudent = studentMapper.selectByPrimaryKey(editId);
                editStudent.setSchool(schoolMapper.selectByPrimaryKey(editStudent.getfSchool()));
                editStudent.setProfessional(professionalMapper.selectByPrimaryKey(editStudent.getfProfessional()));
            }

        }

        // 设置结果
        rs.put("school", school);
        rs.put("editStudent", editStudent);
        rs.put("professionalList", professionalList);
        // 将结果放入缓存
        logger.info(editId + ", "  + editStudent);
        jsonRedisTemplate.opsForValue().set("sch_professional_List", professionalList);
        jsonRedisTemplate.opsForValue().set("user_source_school", school);
        jsonRedisTemplate.opsForValue().set("editStudent", editStudent);

        return rs;
    }

    /**
     * 查询所有
     */
    private TableDataInfo<Student> findAll(
            Integer userSource
    ) {
        return findAll(userSource, null);
    }

    /**
     * 查询所有学籍信息，通过学校条件
     * 如果schoolId为null，即表示无需条件
     * 这个学校id的条件是针对省管理员的，校管理员即使请求了此服务，给了这个参数，也是无效的。
     */
    private TableDataInfo<Student> findAll(
            Integer userSource,
            Integer schoolId
    ) {
        TableDataInfo<Student> rs = new TableDataInfo<>();
        List<Student> studentList = new ArrayList<>();

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
            // 省级管理员：查看所有学校的学籍
            if(source.getPrivilege() == User.PRIVILEGE_SHENG){
                if(schoolId == null) {
                    // 查询所有的学籍信息
                    studentList = studentMapper.selectAll();
                } else {
                    // 查询某个学校的所有学籍信息
                    studentList = studentMapper.selectAllBySchool(schoolId);
                }
            }
            // 校级管理员
            else if(source.getPrivilege() == User.PRIVILEGE_XIAO) {
                // 不管有没有给条件，学籍管理员也只能查看自己学校的学籍信息
                studentList = studentMapper.selectAllBySchool(source.getUinfo().getfSchool());
            }

            // 查询学籍中的学校信息和专业信息
            for(Student var : studentList){
                School school = schoolMapper.selectByPrimaryKey(var.getfSchool());
                var.setSchool(school);
                Professional professional = professionalMapper.selectByPrimaryKey(var.getfProfessional());
                var.setProfessional(professional);
            }
        }

        // 设置结果
        rs.setRows(studentList);
        rs.setTotal(studentList.size());
        // 设置缓冲区
        jsonRedisTemplate.opsForValue().set("findAll_studentData", rs);
        jsonRedisTemplate.opsForValue().set("findALL_student_condition_schoolId", schoolId);
        return rs;
    }

    @Override
    @CacheEvict(key = "#id")
    public int remove(String id) {
        return studentMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int save(Student record) {
        professionalDataIsUpdate = true;
        // 保存前，需要进行一系列的操作
        // 1、计算出学籍号
        if(record.getfSchool() != null && record.getCode() != null)
            record.setpStuid(record.getfSchool() + record.getCode());
        else return 0;
        // 2、根据身份证计算出：性别、年龄
        int gender = IdNumberUtils.getGender(record.getIdnumber());
        if(gender == 0 || gender == 1) record.setGender((byte) gender);
        int age = IdNumberUtils.getAge(record.getIdnumber());
        if(age >= 10 && age <= 100) record.setAge((byte) age);
        logger.warn(record.toString());

        return studentMapper.insertMy(record);
    }

    @Override
    @Cacheable(
            key = "#id",                                /* 缓存key使用id */
            cacheManager = "jsonRedisCacheManager",     /* 使用json缓存管理器 */
            unless = "#result == null"                  /* 当没查到时，不缓存 */
    )
    public Student findById(String id) {
        return studentMapper.selectByPrimaryKey(id);
    }

    @Override
    @CachePut(key = "#record.pStuid")
    public int update(Student record) {
        professionalDataIsUpdate = true;
        // 更新学籍信息
        int rows = studentMapper.updateByPrimaryKeySelective(record);
        return rows;
    }

}
