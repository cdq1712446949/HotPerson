package com.chenu.pvcstumansys.service;

import com.chenu.pvcstumansys.db.bean.Message;
import com.chenu.pvcstumansys.db.bean.Sch2prof;
import com.chenu.pvcstumansys.db.bean.noindb.TableDataInfo;

/**
 * 基本作用：学校专业库（学校-专业映射表）服务
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
public interface Sch2profService {

    /**
     * 处理客户端发来的消息，提供服务
     */
    Message service(Message message);

    /**
     * 查询所有专业
     */
    TableDataInfo<Sch2prof> findAll(Integer userSource);

    int remove(Integer id);

    int save(Sch2prof record);

    Sch2prof findById(Integer id);

    int update(Sch2prof record);

}
