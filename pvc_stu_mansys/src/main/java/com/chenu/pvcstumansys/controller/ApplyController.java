package com.chenu.pvcstumansys.controller;

import com.chenu.pvcstumansys.db.bean.Apply;
import com.chenu.pvcstumansys.db.bean.noindb.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 基本作用：
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/3
 */
@Controller
public class ApplyController {

    @Autowired
    private RedisTemplate jsonRedisTemplate;

    /**
     * 列出所有申请页面
     */
    @GetMapping("/apply/list")
    public String applyList(
            Model model
    ) {
        // 放入所有申请信息
        TableDataInfo<Apply> applyData = (TableDataInfo<Apply>) jsonRedisTemplate.opsForValue().get("applyData");
        model.addAttribute("applyData", applyData);
        // 放入查询条件：处理状态
        Integer state = (Integer) jsonRedisTemplate.opsForValue().get("findAll_apply_state");
        model.addAttribute("state", state);

        return "apply/applyList";
    }


    /**
     * 申请系列操作成功页面
     */
    @GetMapping("/apply/ok")
    public String applyOk(
            Model model
    ){
        // 放入提示信息
        String apply_ok_msg = (String) jsonRedisTemplate.opsForValue().get("apply_ok_msg");
        model.addAttribute("apply_ok_msg", apply_ok_msg);

        return "apply/ok";
    }

    /**
     * 申请详情页
     */
    @GetMapping("/apply/desc")
    public String applyDesc(
            Model model
    ){
        // 放入申请
        Apply apply = (Apply) jsonRedisTemplate.opsForValue().get("apply");
        model.addAttribute("apply", apply);
        // 放入消息携带的data或id
        Object data = jsonRedisTemplate.opsForValue().get("desc_apply_data");
        Object id = jsonRedisTemplate.opsForValue().get("desc_apply_id");
        model.addAttribute("data", data);
        model.addAttribute("id", id);


        return "apply/desc";
    }


}
