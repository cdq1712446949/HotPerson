package com.chenu.pvcstumansys.utils;

import static org.thymeleaf.spring4.view.ThymeleafViewResolver.FORWARD_URL_PREFIX;
import static org.thymeleaf.spring4.view.ThymeleafViewResolver.REDIRECT_URL_PREFIX;

/**
 * 基本作用：url工具
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
public class GoUtils {

    /**
     * 转发一个请求
     * @param url 要转发的原始url
     * @return 转换成功的url
     */
    public static String ForwardUrl(String url){
        return FORWARD_URL_PREFIX + url;
    }

    /**
     * 重定向请求
     * @param url 要重定向的原始url
     * @return 转换成功的url
     */
    public static String RedirectUrl(String url){
        return REDIRECT_URL_PREFIX + url;
    }

}
