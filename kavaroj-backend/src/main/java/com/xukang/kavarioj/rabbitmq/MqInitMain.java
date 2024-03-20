//package com.xukang.kavarioj.rabbitmq;
//
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;
//
///**
// * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
// */
//public class MqInitMain {
//
//    public static void main(String[] args) {
//        try {
//            ConnectionFactory factory = new ConnectionFactory();
//            factory.setHost("10.18.68.61:44444");
//            Connection connection = factory.newConnection();
//            Channel channel = connection.createChannel();
//            String EXCHANGE_NAME = "exchange_x";
//            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
//
//            // 创建队列，随机分配一个队列名称
//            String queueName = "queue_a";
//            channel.queueDeclare(queueName, true, false, false, null);
//            channel.queueBind(queueName, EXCHANGE_NAME, "XA");
//        } catch (Exception e) {
//
//        }
//
//    }
//}