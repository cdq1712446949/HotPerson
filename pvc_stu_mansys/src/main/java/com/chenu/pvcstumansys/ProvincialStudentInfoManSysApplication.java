package com.chenu.pvcstumansys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * SpringBoot主配置类
 *
 * 这个工程已经完成以下配置：
 *  1、使用slf4j日志框架，请在配置文件application.yaml中配置日志框架的参数
 *  2、使用阿里的Druid数据源，支持网页实时监控数据源信息
 *  3、使用MyBatis框架管理数据库操作
 *  4、使用redis作为缓存中间件，支持注解缓存开发
 *  5、引入了thymeleaf模版框架，也可以不使用其提供的拓展功能
 *  6、使用RabbitMQ(AMQP实现)来实现微服务消息传递
 *  7、引入了Spring Health监控管理，可以后台实时查看运行时的程序状态，还支持远程关闭程序功能
 *  8、支持热部署开发，对工程的所有改动（包括类和配置文件），只需要重新编译（ctrl + f9）即可看到效果，无需重启应用
 *  9、引入了Spring Security安全框架，可以快速开发安全/权限管理
 */
@EnableCaching                                              /* 开启缓存的注解开发 */
@EnableRabbit                                               /* 开启RabbitMQ */
@SpringBootApplication
public class ProvincialStudentInfoManSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProvincialStudentInfoManSysApplication.class, args);
    }

}
