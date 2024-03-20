package com.xukang.kavarioj.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 初始化消息队列
 */
@Configuration
public class RabbitMqConfig {
    public static final String EXCHANGE_X = "exchange_x";
    public static final String QUEUE_A = "queue_a";

    @Bean("xExchange")
    public DirectExchange xExchange() {
        return new DirectExchange(EXCHANGE_X);
    }

    @Bean("queueA")
    public Queue queueA() {
        Map<String, Object> map = new HashMap<>();
        // 设置队列最长长度 超过则发送给死信队列
        map.put("x-message-ttl", 5000);
        return QueueBuilder.durable(QUEUE_A).withArguments(map).build();
    }

    // 绑定
    @Bean
    public Binding ququABindingX(@Qualifier("queueA") Queue queueA,
                                 @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }

}