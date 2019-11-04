package com.chenu.pvcstumansys.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 基本作用：阿里的Druid数据源 配置类
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
@Configuration
public class DruidConfig {

    // 配置数据源
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druid(){
        DruidDataSource druidDataSource = new DruidDataSource();
        // 数据源密码在这里指定，不暴漏在配置文件中
        druidDataSource.setPassword("980814");
        return druidDataSource;
    }

    // 配置Druid监控
    // 1、配置一个管理后台的Servlet
    @Bean
    public ServletRegistrationBean statViewServlet(){
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String, String > initParams = new HashMap<>();

        initParams.put("loginUsername", "admin");
        initParams.put("loginPassword", "147258");
        initParams.put("allow", "");                // 默认允许所有访问
        initParams.put("deny", "192.168.43.52");    // 禁止访问

        bean.setInitParameters(initParams);
        return bean;
    }

    // 2、配置一个监控的filter
    @Bean
    public FilterRegistrationBean webStatFilter(){
        FilterRegistrationBean bean = new FilterRegistrationBean(new WebStatFilter());

        Map<String, String > initParams = new HashMap<>();
        initParams.put("exclusions", "*.js,*.css,/druid/*");

        bean.setInitParameters(initParams);
        bean.setUrlPatterns(Arrays.asList("/*"));
        return bean;
    }

}
