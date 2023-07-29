package com.zhang.bi.bizmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MyMessageProducerTest {

    @Resource
    private MyMessageProducer myMessageProducer;
    @Test
    void sendMessage() {
        String message = "你好呀";
        myMessageProducer.sendMessage("demo_exchange", "demo_routingKey", message);
        System.out.println("发送消息：" + message);
    }
}