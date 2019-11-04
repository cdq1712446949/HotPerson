package com.chenu.pvcstumansys.config;

import com.chenu.pvcstumansys.compoent.MyLocalResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;


/**
 * 基本作用：使用WebMvcConfigurerAdapter可以扩展SprigMVC的功能；
 * 详细解释：使用这种方式，既保留了所有的自动配置，也能扩展我们自己的个性配置。
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
//@EnableWebMvc         /* 启动手动配置SpringMvc，我们将全面接管，SpringBoot提供的SpringMvc自动配置将失效 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    /**
     * 注册视图解析器
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // index主页/后台
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index").setViewName("index");
        // 登录
        registry.addViewController("/usr/login").setViewName("userLogin");
        // 错误页面
        registry.addViewController("/myerror").setViewName("myerror");
        // 测试
        registry.addViewController("/testMsg").setViewName("test/testMessage");

    }

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
    }

    /**
     * 注册自定义的区域信息解析器到容器中
     */
    @Bean
    public LocaleResolver localeResolver(){
        return new MyLocalResolver();
    }

}
