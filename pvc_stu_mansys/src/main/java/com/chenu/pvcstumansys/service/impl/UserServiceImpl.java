package com.chenu.pvcstumansys.service.impl;

import com.chenu.pvcstumansys.compoent.MyMd5PasswordEncoder;
import com.chenu.pvcstumansys.db.bean.*;
import com.chenu.pvcstumansys.db.bean.noindb.TableDataInfo;
import com.chenu.pvcstumansys.db.mapper.SchoolMapper;
import com.chenu.pvcstumansys.db.mapper.UInfoMapper;
import com.chenu.pvcstumansys.db.mapper.UserMapper;
import com.chenu.pvcstumansys.service.UserService;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 基本作用：用户服务实现类
 * 详细解释：为客户端的请求提供用户服务
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
@CacheConfig(cacheNames = "user" /* 本类使用缓存名称：user */ )
@Service("userService")
public class UserServiceImpl implements UserService {

    /**
     * 请求用户服务的服务路由键
     */
    public static final String SERVICE_ROUTING_KEY = "user";

    /**
     * 消息服务器的用户消息队列
     */
    public static final String QUEUE = "pvcstumansys.userservice.queue";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * dao
     */
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UInfoMapper uInfoMapper;
    @Autowired
    private SchoolMapper schoolMapper;

    /**
     * redis模版，做缓存和处理结果存放
     */
    @Autowired
    private RedisTemplate jsonRedisTemplate;

    /**
     * 最高级root(省级root)用户源，全系统只有一个
     */
    public static final int ROOT_USER_SOURCE = 0;

    /**
     * 发消息过来的的用户源键，通过这个键可以获得发消息的用户源
     */
    public static final String USER_SOURCE_KEY = "user_source";


    /**
     * 服务具体功能索引号
     * 每添加一个新的功能，都需要在这里添加上索引号
     */
    public static final int USER_LIST = 0;                  /* 得到所有用户信息 */
    public static final int USER_ADD_OR_EDIT_VIEW = 1;      /* 前往用户添加/编辑页面 */
    public static final int USER_ADD_OR_UPDATE = 2;         /* 用户添加或更新 */
    public static final int USER_REMOVE = 3;                /* 用户删除 */


    /**
     * 处理客户端发来的消息，提供服务，所有的服务都需要在这里注册，并规范化
     * 服务完成后，返回一个包含处理结果的消息，这个消息将回发到最初需要服务的例程
     * @param message 客户端发来的消息
     * @return 包含处理结果的消息
     */
    @Override
    @RabbitListener(queues = QUEUE)
    public Message service(
            Message message
    ) {
        Message result = null;
        Gson gson = new Gson();
        int rows = 0;

        // 根据消息类型处理
        switch (message.getMesstype()){
            // 用户管理/列出所有用户
            case USER_LIST:
                logger.info("用户服务收到消息，正在提供 USER_LIST 服务...");
                Integer[] parms = gson.fromJson(message.getData(), Integer[].class);
                TableDataInfo<User> userData = findAll(parms,message.getfUserSource());
                // 完成
                result = MessageServiceImpl.ServiceFinish(message, userData, Message.HANDLER_SUCCESS, "/users");
                break;

            case USER_ADD_OR_EDIT_VIEW:
                logger.info("用户服务收到消息，正在提供 USER_ADD_OR_EDIT_VIEW 服务...");
                // 判断是否有用户id，有：编辑；没有：添加
                Integer userId = null;
                try{
                    userId = Integer.valueOf(message.getData());
                } catch (Exception e){
                    logger.info("uid 转换错误，用户前往的是添加页面...");
                }
                TableDataInfo user_add_view_data = userAddOrEditView(
                        message.getfUserSource(),           /* 用户源 */
                        userId                              /* 用户id */
                );
                // 完成
                result = MessageServiceImpl.ServiceFinish(message, user_add_view_data, Message.HANDLER_SUCCESS, "/user");
                break;

            case USER_ADD_OR_UPDATE:
                logger.info("用户服务收到消息，正在提供 USER_ADD_OR_UPDATE 服务...");
                try {
                    // 解析消息传过来的用户json
                    User user = gson.fromJson(message.getData(), User.class);
                    rows = saveOrUpdate(user);
                    if(rows > 0){
                        Integer userPrivilege = (Integer) jsonRedisTemplate.opsForValue().get("findAll_users_privilege");
                        Integer userOnlyRoot = (Integer) jsonRedisTemplate.opsForValue().get("findAll_users_onlyRoot");
                        TableDataInfo user_edit_data = findAll(new Integer[]{ userPrivilege, userOnlyRoot },message.getfUserSource());
                        // 完成
                        result = MessageServiceImpl.ServiceFinish(message, user_edit_data, Message.HANDLER_SUCCESS, "/users");
                    } else {
                        // 完成
                        result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "添加或更新用户失败，请重试...");
                    }
                } catch (Exception e){
                    logger.warn("用户json转换错误...");
                    result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "更新失败，无法解析用户json数据，请检查消息参数...");
                }
                break;

            case USER_REMOVE:
                logger.info("用户服务收到消息，正在提供 USER_REMOVE 服务...");
                try {
                    Integer rmId = Integer.valueOf(message.getData());
                    rows = remove(rmId);
                    if(rows > 0){
                        Integer userPrivilege = (Integer) jsonRedisTemplate.opsForValue().get("findAll_users_privilege");
                        Integer userOnlyRoot = (Integer) jsonRedisTemplate.opsForValue().get("findAll_users_onlyRoot");
                        TableDataInfo user_rm_data = findAll(new Integer[]{ userPrivilege, userOnlyRoot }, message.getfUserSource());
                        // 完成
                        result = MessageServiceImpl.ServiceFinish(message, user_rm_data, Message.HANDLER_SUCCESS, "/users");
                    } else {
                        // 完成
                        result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "删除失败，这个用户可能不存在或已经被移除，请重试...");
                    }
                } catch (Exception e){
                    logger.warn("id 转换错误，未指明要删除用户的id");
                    result = MessageServiceImpl.ServiceFinish(message, null, Message.HANDLER_FAILURE, "删除失败，你未指明任何用户id");
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
     * 查询所有
     * @return
     */
    public static boolean userDataIsUpdate = true;   /* 数据库数据是否被更新了 */
    @Override
    public TableDataInfo<User> findAll(Integer userSource) {
        return findAll(new Integer[]{ -1, 0 }, userSource);
    }

    /**
     * 查询所有用户，根据权限，还可以指定是否只查看超级用户
     * @param parms Integer数组
     *              第一个参数表示权限，为1：校级；为0：省级；其他：都可以
     *              第二个参数表示是否只查看超级用户，为1：只查看超级用户；为0：所有用户都查看
     * @return 查询到的用户数据
     */
    @Override
    public TableDataInfo<User> findAll(Integer[] parms, Integer userSource) {
        TableDataInfo rs = new TableDataInfo<User>();
        List<User> users = new ArrayList<>();

        if(userSource == ROOT_USER_SOURCE){
            // 是最高的root用户

            if(parms[1] == 1){
                // 只需要超级用户
                if(parms[0] == 0 || parms[0] == 1){
                    // 只要省级或校级
                    users = userMapper.selectAllOnlyRootByPrivilege(parms[0].byteValue());
                } else {
                    // 都可以
                    users = userMapper.selectAllOnlyRoot();
                }

            } else if(parms[1] == 0) {
                // 所有用户都需要
                if(parms[0] == 0 || parms[0] == 1){
                    // 只要省级或校级
                    users = userMapper.selectAllByPrivilege(parms[0].byteValue());
                } else {
                    // 都可以
                    users = userMapper.selectAll();
                }

            }

        } else {
            // 普通校级root用户，不能查询到任何其他的root用户！
            // 且只能查到自己学校的管理员
            User user = userMapper.selectByPrimaryKey(userSource);
            if(user != null){
                UInfo uInfo = uInfoMapper.selectByPrimaryKey(user.getfInfo());
                School school = schoolMapper.selectByPrimaryKey(uInfo.getfSchool());
                if(school != null){
                    users = userMapper.selectAllNotRootBySchool(school.getpSchid());
                }
            }


        }
        // 查询用户的个人信息
        for(int i = 0; i < users.size(); i++){
            UInfo uInfo = uInfoMapper.selectByPrimaryKey(users.get(i).getfInfo());
            School school = schoolMapper.selectByPrimaryKey(uInfo.getfSchool());
            uInfo.setSchool(school);
            users.get(i).setUinfo(uInfo);
        }
        // 将结果放入缓存
        rs.setRows(users);
        rs.setTotal(users.size());
        jsonRedisTemplate.opsForValue().set("findAll_users_data", rs);
        // 将参数放入缓存
        jsonRedisTemplate.opsForValue().set("findAll_users_privilege", parms[0]);
        jsonRedisTemplate.opsForValue().set("findAll_users_onlyRoot", parms[1]);

        return rs;
    }

    /**
     * 前往用户添加/编辑页面
     */
    private TableDataInfo<School> userAddOrEditView(
            Integer userSource,     /* 消息用户源 */
            Integer userId          /* 用户Id */
    ){
        TableDataInfo rs = new TableDataInfo<School>();
        List<School> schools = null;
        User user = null;

        // 放入所有学校数据
        if(SchoolServiceImpl.schoolDataIsUpdate == true){
            // 如果数据库数据被更新了，我们再从数据库重新获取
            schools = schoolMapper.selectAll();
            // 将结果放入缓存
            rs.setRows(schools);
            rs.setTotal(schools.size());
            jsonRedisTemplate.opsForValue().set("findAll_schools_data", rs);
            SchoolServiceImpl.schoolDataIsUpdate = false;
        } else {
            // 如果数据库数据没被更新过，直接从缓存中拿就可以
            rs = (TableDataInfo<School>) jsonRedisTemplate.opsForValue().get("findAll_schools_data");
        }
        // 放入用户源
        jsonRedisTemplate.opsForValue().set(USER_SOURCE_KEY, userSource);
        // 放入用户源所在学校id
        User sourceUser = userMapper.selectByPrimaryKey(userSource);
        if(sourceUser != null){
            UInfo uInfo = uInfoMapper.selectByPrimaryKey(sourceUser.getfInfo());
            School school = schoolMapper.selectByPrimaryKey(uInfo.getfSchool());
            if(school != null){
                jsonRedisTemplate.opsForValue().set("userAddView_school_id", school.getpSchid());
            }
        }
        // 放入要编辑的用户
        // 如果有用户id，则说明是前往编辑页面，查询该用户信息
        if(userId != null){
            user = userMapper.selectByPrimaryKey(userId);
            UInfo uInfo = uInfoMapper.selectByPrimaryKey(user.getfInfo());
            user.setUinfo(uInfo);
        }
        jsonRedisTemplate.opsForValue().set("user_edit_view_user_data", user);

        return rs;
    }

    /**
     * 用户添加或更新
     */
    private int saveOrUpdate(User user){
        int rows = 0;
        // 根据是否有id决定是添加还是更新
        if(user.getpUid() == null){
            rows = save(user);
        } else {
            rows = update(user);
        }

        return rows;
    }

    /**
     * 删除一个用户
     * 支持缓存，当删除一个用户时，也从缓存中删除
     *
     * @param pUid 用户的id
     * @return 影响数据库的行数
     */
    @Override
    @CacheEvict(key = "#pUid")
    public int remove(Integer pUid) {
        userDataIsUpdate = true;
        int rows = 0;
        User user = userMapper.selectByPrimaryKey(pUid);
        Integer infoId = null;
        if(user != null){
            infoId = user.getfInfo();
            rows = userMapper.deleteByPrimaryKey(pUid);
        }
        // 别忘了，把用户资料信息也删了
        uInfoMapper.deleteByPrimaryKey(infoId);

        return rows;
    }


    @Override
    public int save(User record) {
        MyMd5PasswordEncoder md5 = new MyMd5PasswordEncoder();
        int rows = 0;
        userDataIsUpdate = true;

        logger.warn("=== 添加 ===");
        // 添加
        // 保存用户，别忘了先得保存其用户信息
        uInfoMapper.insertSelective(record.getUinfo());
        record.setfInfo((Integer) uInfoMapper.selectNewId());
        // 别忘了给密码加密
        record.setPwd(md5.encode(record.getPwd()));
        rows = userMapper.insertSelective(record);
        return rows;
    }

    /**
     * 查询一名用户
     * 支持缓存，当第一次查询到一个用户，我们将其放入redis缓存
     *
     * @param pUid 用户的唯一id
     * @return 查询到的用户，没查到自然为空
     */
    @Override
    @Cacheable(
            key = "#pUid",                              /* 缓存key使用用户的id */
            cacheManager = "jsonRedisCacheManager",     /* 使用json缓存管理器 */
            unless = "#result == null"                  /* 当没查到时，不缓存 */
    )
    public User findById(Integer pUid) {
        return userMapper.selectByPrimaryKey(pUid);
    }

    /**
     * 更新一个用户信息
     * 支持缓存，当用户信息被更新时候，同时更新缓存数据
     *
     * @param record 更新的用户
     * @return 影响数据库的行数
     */
    @Override
    @CachePut(key = "#record.pUid")
    public int update(User record) {
        MyMd5PasswordEncoder md5 = new MyMd5PasswordEncoder();
        int rows = 0;
        userDataIsUpdate = true;
        User newUser = userMapper.selectByPrimaryKey(record.getpUid());

        logger.warn("=== 更新 ===");
        // 先更新user
        if(!StringUtils.isEmpty(record.getPwd())){
            // 如果密码被更改了，重置它，记得加密
            record.setPwd(md5.encode(record.getPwd()));
        } else {
            // 密码没更改
            record.setPwd(newUser.getPwd());
        }
        rows = userMapper.updateByPrimaryKeySelective(record);
        // 再更新用户信息
        UInfo uInfo = record.getUinfo();
        uInfo.setpUfid(newUser.getfInfo());
        uInfoMapper.updateByPrimaryKeySelective(uInfo);

        return rows;
    }

    /**
     * 登录，通过用户名和密码得到用户信息
     * 支持缓存，当第一次查询到的时候，放入redis缓存
     *
     * @param username 用户名
     * @param password 密码明文
     * @return 查询到的用户，没查到为空
     */
    @Override
    @Cacheable(
            key = "#username",                          /* 缓存key使用用户的用户名 */
            cacheManager = "jsonRedisCacheManager",     /* 使用json缓存管理器 */
            unless = "#result == null"                  /* 当没查到时，不缓存 */
    )
    public User login(String username, String password) {
        return userMapper.login(username, new MyMd5PasswordEncoder().encode(password));
    }

    /**
     * 通过用户名加载用户信息
     * 这个方法是因为继承 UserDetailsService 接口需要实现的，作用是为Spring Security提供支持
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户信息
        User user = userMapper.selectByUsername(username);
        if(user != null){
            // 封装用户信息并返回，三个参数分别是：用户名，密码，用户角色
            // 由于用户名和密码从数据库获取了，只需要配置用户权限即可
            user.setEnabled(true);      /* 启用账户 */
            //创建角色集合对象
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            if(user.getRole() == 0){
                // 超级用户：两个角色 ROOT 和 USER
                GrantedAuthority grantedAuthority1 = new SimpleGrantedAuthority("ROLE_ROOT");
                GrantedAuthority grantedAuthority2 = new SimpleGrantedAuthority("ROLE_USER");
                authorities.add(grantedAuthority1);
                authorities.add(grantedAuthority2);
            } else if(user.getRole() == 1){
                // 普通管理员：一个角色 USER
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
                authorities.add(grantedAuthority);
            } else {
                // 该账号没有任何角色
                // 既然没任何角色，那就禁用此用户
                user.setEnabled(false);
            }
            user.setAuthorities(authorities);
            logger.info("查询到的用户 --> " + user);
            return user;
        }
        return null;
    }

}
