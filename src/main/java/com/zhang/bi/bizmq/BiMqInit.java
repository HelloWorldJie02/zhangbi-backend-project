package com.zhang.bi.bizmq;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 仅用于创建队列和交换机, 使用一次
 * @author ZHANG
 */
public class BiMqInit {
    public static void main(String[] args) {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("192.168.42.132");
            connectionFactory.setUsername("admin");
            connectionFactory.setPassword("123456");

            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            final String biExchangeName = BiMqConstant.BI_EXCHANGE_NAME;

            channel.exchangeDeclare(biExchangeName, "direct");
            // 创建队列，分配一个队列名称
            String queueName = BiMqConstant.BI_QUEUE_NAME;
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, biExchangeName, BiMqConstant.BI_ROUTING_KEY);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
