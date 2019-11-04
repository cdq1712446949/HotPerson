package com.chenu.pvcstumansys.controller;

import com.chenu.pvcstumansys.db.bean.Profcategory;
import com.chenu.pvcstumansys.db.bean.Professional;
import com.chenu.pvcstumansys.db.bean.noindb.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * 基本作用：专业控制器
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
@Controller
public class ProfessionalController {

    @Autowired
    private RedisTemplate jsonRedisTemplate;

    @GetMapping("/prof/man")
    public String profMan(
            Model model
    ){
        // 放入专业数据
        TableDataInfo<Professional> profData = (TableDataInfo<Professional>) jsonRedisTemplate.opsForValue().get("findAll_professionalList_data");
        model.addAttribute("profData", profData);

        return "professional/list";
    }

    /**
     * 前往添加/编辑页面
     * @return
     */
    @GetMapping("/prof")
    public String profAddOrEdit(
            Model model
    ){
        // 放入专业类别列表
        TableDataInfo<Profcategory> profcategoryData = (TableDataInfo<Profcategory>) jsonRedisTemplate.opsForValue().get("findAll_profcategories_data");
        model.addAttribute("profcategoryData", profcategoryData);
        // 放入编辑的专业
        Professional editProf = (Professional) jsonRedisTemplate.opsForValue().get("prof_edit_view_prof_data");
        model.addAttribute("editProf", editProf);

        return "professional/addOrEdit";
    }


}
