package com.chenu.pvcstumansys.service;

import com.chenu.pvcstumansys.db.bean.Message;
import com.chenu.pvcstumansys.db.bean.Professional;
import com.chenu.pvcstumansys.db.bean.noindb.TableDataInfo;

/**
 * 基本作用：专业服务
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
public interface ProfessionalService  {

    /**
     * 处理客户端发来的消息，提供服务
     */
    Message service(Message message);

    /**
     * 查询所有专业
     */
    TableDataInfo<Professional> findAll(Integer userSource);

    int remove(Integer pProfid);

    int save(Professional record);

    Professional findById(Integer pProfid);

    int update(Professional record);

}
