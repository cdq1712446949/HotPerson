package com.chenu.pvcstumansys.controller;

import com.chenu.pvcstumansys.db.bean.Professional;
import com.chenu.pvcstumansys.db.bean.Sch2prof;
import com.chenu.pvcstumansys.db.bean.School;
import com.chenu.pvcstumansys.db.bean.noindb.TableDataInfo;
import com.chenu.pvcstumansys.db.mapper.Sch2profMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 基本作用：学校专业库控制器
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
@Controller
public class Sch2ProfController {

    @Autowired
    private Sch2profMapper sch2profMapper;

    @Autowired
    private RedisTemplate jsonRedisTemplate;

    /**
     * 学校-专业管理页面
     * @param model
     * @return
     */
    @GetMapping("/sch2prof/man")
    public String sch2profMan(
            Model model
    ) {
        // 放入映射
        TableDataInfo<Sch2prof> sch2profData = (TableDataInfo<Sch2prof>) jsonRedisTemplate.opsForValue().get("findAll_sch2profList_data");
        model.addAttribute("sch2profData", sch2profData);

        return "sch2prof/list";
    }

    /**
     * 学校-专业 添加或编辑页面
     * @param model
     * @return
     */
    @GetMapping("/sch2prof")
    public String sch2profAddOrEdit(
            Model model
    ){
        // 放入用户所处学校
        School userSourceSchool = (School) jsonRedisTemplate.opsForValue().get("user_source_school");
        model.addAttribute("userSourceSchool", userSourceSchool);
        // 放入所有学校
        List<School> schoolList = (List<School>) jsonRedisTemplate.opsForValue().get("findAll_school_data");
        model.addAttribute("schoolList", schoolList);
        // 放入所有专业
        List<Professional> professionalList = (List<Professional>) jsonRedisTemplate.opsForValue().get("findAll_professional_data");
        model.addAttribute("professionalList", professionalList);
        // 放入编辑的学校-专业映射
        Sch2prof editSch2prof = (Sch2prof) jsonRedisTemplate.opsForValue().get("editSch2prof");
        model.addAttribute("editSch2prof", editSch2prof);

        return "sch2prof/addOrEdit";
    }

}
