package com.chenu.pvcstumansys.db.mapper;

import com.chenu.pvcstumansys.base.BaseDao;
import com.chenu.pvcstumansys.db.bean.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Mapper
@Component
public interface StudentMapper extends BaseDao<Student> {

    @Override
    int insert(Student record);

    @Override
    int insertSelective(Student record);

    @Override
    int updateByPrimaryKeySelective(Student record);

    @Override
    int updateByPrimaryKey(Student record);

    @Override
    int deleteByPrimaryKey(Serializable id);

    @Override
    Student selectByPrimaryKey(Serializable id);

    @Override
    List<Student> selectAll();

    /**
     * 查询某个学校的所有学籍信息
     * @param schoolId
     */
    List<Student> selectAllBySchool(Integer schoolId);

    /**
     * 插入学生记录
     * @param student
     * @return
     */
    public int insertMy(Student student);

    /**
     * 组合查询学生记录
     *
     * @param student
     * @return
     */
    List<Student> selectStudentByInfo( Student student);
}