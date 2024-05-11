package com.example.producer

import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProducerController(
    private val kafkaTemplate: KafkaTemplate<String,Long>
) {
    @Transactional
    @GetMapping("/test")
    fun find(){
        for(i in 0..1000){
            kafkaTemplate.send(ProducerRecord("topic-p3r3","key" + i, 2L))
            Thread.sleep(50L)
        }
    }
}