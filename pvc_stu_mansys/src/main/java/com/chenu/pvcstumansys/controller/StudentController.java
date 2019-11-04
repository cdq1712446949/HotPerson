package com.chenu.pvcstumansys.controller;

import com.chenu.pvcstumansys.db.bean.Professional;
import com.chenu.pvcstumansys.db.bean.School;
import com.chenu.pvcstumansys.db.bean.Student;
import com.chenu.pvcstumansys.db.bean.User;
import com.chenu.pvcstumansys.db.bean.noindb.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 基本作用：学籍控制器
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/4
 */
@Controller
public class StudentController {

    @Autowired
    private RedisTemplate jsonRedisTemplate;

    @GetMapping("/student/list")
    public String studentList(
            Model model
    ){
        // 放入学籍列表
        TableDataInfo<Student> studentData = (TableDataInfo<Student>) jsonRedisTemplate.opsForValue().get("findAll_studentData");
        model.addAttribute("studentData", studentData);
        // 放入查询条件：学校id
        Integer schoolId = (Integer) jsonRedisTemplate.opsForValue().get("findALL_student_condition_schoolId");
        model.addAttribute("schoolId", schoolId);

        return "student/list";
    }

    /**
     * 学籍 添加或编辑页面
     * @param model
     * @return
     */
    @GetMapping("/student")
    public String studentAddOrEdit(
        Model model
    ){

        // 放入用户所处学校
        School userSourceSchool = (School) jsonRedisTemplate.opsForValue().get("user_source_school");
        model.addAttribute("userSourceSchool", userSourceSchool);
        // 放入学校的所有专业
        List<Professional> professionalList = (List<Professional>) jsonRedisTemplate.opsForValue().get("sch_professional_List");
        model.addAttribute("sch_professional_List", professionalList);
        // 放入编辑的学生学籍
        Student editStudent = (Student) jsonRedisTemplate.opsForValue().get("editStudent");
        model.addAttribute("editStudent", editStudent);

        return "student/addOrEdit";

    }

}
