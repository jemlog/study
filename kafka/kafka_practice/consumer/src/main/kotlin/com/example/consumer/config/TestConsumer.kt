package com.example.consumer.config

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class TestConsumer {

    @KafkaListener(topics = ["topic-p3r3"])
    fun test(@Payload num: Long){
        print(num)
    }
}