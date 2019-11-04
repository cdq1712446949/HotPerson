package com.chenu.pvcstumansys.controller;

import com.chenu.pvcstumansys.db.bean.School;
import com.chenu.pvcstumansys.db.bean.noindb.TableDataInfo;
import com.chenu.pvcstumansys.db.bean.User;
import com.chenu.pvcstumansys.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * 基本作用：用户处理者
 * 详细解释：处理用户相关的请求结果
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
@Controller
public class UserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisTemplate jsonRedisTemplate;

    /**
     * 用户管理/列出所有用户
     */
    @GetMapping("/users")
    public String users(
            Model model
    ){
        // 放入用户列表
        TableDataInfo<User> userData = (TableDataInfo<User>) jsonRedisTemplate.opsForValue().get("findAll_users_data");
        model.addAttribute("userData", userData);
        // 放入查询参数
        Integer userPrivilege = (Integer) jsonRedisTemplate.opsForValue().get("findAll_users_privilege");
        model.addAttribute("userPrivilege", userPrivilege);
        Integer userOnlyRoot = (Integer) jsonRedisTemplate.opsForValue().get("findAll_users_onlyRoot");
        model.addAttribute("userOnlyRoot", userOnlyRoot);


        return "user/list";
    }

    /**
     * 前往添加/编辑页面
     * @param model
     * @return
     */
    @GetMapping("/user")
    public String userAddOrEdit(
            Model model
    ){
        // 放入学校列表
        TableDataInfo<School> schoolData = (TableDataInfo<School>) jsonRedisTemplate.opsForValue().get("findAll_schools_data");
        model.addAttribute("schoolData", schoolData);
        // 放入用户源的学校id
        Integer schoolId = (Integer) jsonRedisTemplate.opsForValue().get("userAddView_school_id");
        model.addAttribute("schoolId", schoolId);
        // 放入编辑的用户
        User editUser = (User) jsonRedisTemplate.opsForValue().get("user_edit_view_user_data");
        model.addAttribute("user", editUser);
        // 得到发消息的用户源
        Integer userSource = (Integer) jsonRedisTemplate.opsForValue().get(UserServiceImpl.USER_SOURCE_KEY);
        // 根据用户源不同去不同的添加/编辑页面
        if(userSource == UserServiceImpl.ROOT_USER_SOURCE){
            return "user/RootAddAndEdit";
        }

        return "user/addAndEdit";
    }


}
