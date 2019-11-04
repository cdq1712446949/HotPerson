package com.chenu.pvcstumansys.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 基本作用：AMQP配置类，使用的是RabbitMQ实现
 * 详细解释：
 * 作者：辰湖飞彦
 * 时间：2019/10/2
 */
@Configuration
public class AMQPConfig {

    /**
     * 配置默认(jdk序列化)消息内容转换器为 json消息内容转换器
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
