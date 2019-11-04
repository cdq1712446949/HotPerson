package com.chenu.pvcstumansys;

import com.chenu.pvcstumansys.db.bean.Profcategory;
import com.chenu.pvcstumansys.db.bean.Student;
import com.chenu.pvcstumansys.db.bean.UInfo;
import com.chenu.pvcstumansys.db.bean.User;
import com.chenu.pvcstumansys.db.mapper.ProfcategoryMapper;
import com.chenu.pvcstumansys.db.mapper.StudentMapper;
import com.chenu.pvcstumansys.db.mapper.UInfoMapper;
import com.chenu.pvcstumansys.db.mapper.UserMapper;
import com.chenu.pvcstumansys.utils.TimeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProvincialStudentInfoManSysApplicationTests {

    @Autowired
    ProfcategoryMapper profcategoryMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UInfoMapper uInfoMapper;
    @Autowired
    StudentMapper studentMapper;


    @Test
    public void testProfcategoryMapper() {
        // 1、插入
        Profcategory profcategory = new Profcategory();
        profcategory.setName("计算机");
        profcategory.setNote("没有...");
        int i = profcategoryMapper.insert(profcategory);
        System.out.println("---> " + i);
    }

    @Test
    public void testUinfoMapper(){
        UInfo uInfo = uInfoMapper.selectByPrimaryKey(1);
        System.out.println(uInfo.toString());
    }

    @Test
    public void testUserMapper(){
        // 1、插入
        User user = new User();
        user.setName("张鹏");
        user.setPwd("123456");
        user.setRole((byte) 1);
        user.setPrivilege((byte) 1);
        user.setfInfo(1);
        int i = userMapper.insert(user);
        System.out.println("---> " + i);
    }

    @Test
    public void testMD5(){
//        Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();
//        System.out.println(md5PasswordEncoder.encodePassword("123456", "chenu"));
        // 598a806a2dcaec4dc31a4bdc92c53d49
    }

    @Test
    public void demo3(){
        Student student = studentMapper.selectByPrimaryKey("12016011328");
        student.setpStuid("666");
        studentMapper.insertMy(student);
    }

    @Test
    public void testSelectStudent(){
        Student student=new Student();
//        student.setName("陈");
        Date d = new Date();
        d.setYear(2016);
        student.setEnrolltime(d);
        List<Student> list=studentMapper.selectStudentByInfo(student);
        System.out.println(list);
    }

}
