package com.chenu.pvcstumansys.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基本作用：身份证工具类
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/9
 */
public class IdNumberUtils {

    /**
     * 判断身份证是否正确且存在
     * 暂时只能判断是否正确，是否真的存在还未实现
     */
    public static boolean isExist(String idNumber){
        if(idNumber.length() == 18){
            return true;
        }
        return false;
    }

    /**
     * 根据身份证号解析出该人的年龄
     * 1: 男
     * 0：女
     */
    public static int getGender(String idNumber){
        if(isExist(idNumber)){
//            46902120000808271X
            char[] chars = idNumber.toCharArray();
            try {
                Integer genderNum = Integer.valueOf(chars[16]);
                if(genderNum % 2 == 0 || genderNum == 0){
                    return 0;
                } else {
                    return 1;
                }
            } catch (Exception e){
                System.out.println("年龄为不是数字...");
                return -2;
            }
        } else {
            System.out.println("身份证不正确，得不到年龄...");
            return -1;
        }
    }

    public static int getAge(String idNumber){
        return 18;
    }

}
