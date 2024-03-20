package com.xukang.kavarioj.rabbitmq;

import com.xukang.kavarioj.juidge.service.JudgeService;
import org.springframework.amqp.core.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 判题队列消费者
 * 更具提交题目id 异步判题
 */
@Component
@Slf4j
public class QueueConsumer {

    @Resource
    private JudgeService judgeService;

    @RabbitListener(queues = "queue_a")
    public void receiveD(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        log.info("当前时间为：{}，当前消息为{}", new Date().toString(), msg);
        Long questionSubmitVOId = judgeService.doJudge(Long.parseLong(msg));
    }
}