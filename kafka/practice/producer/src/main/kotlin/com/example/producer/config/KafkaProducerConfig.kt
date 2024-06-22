package com.example.producer.config

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@EnableKafka
@Configuration
class KafkaProducerConfig {

    @Bean
    fun producerFactory(): ProducerFactory<String, Long> {

        val props = HashMap<String, Any>()

        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "127.0.0.1:29092,127.0.0.1:39092,127.0.0.1:49092"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = LongSerializer::class.java
        props[ProducerConfig.TRANSACTIONAL_ID_CONFIG] = "tx-advance" // 트랜잭션 ID는 마음대로 설정하기

        return DefaultKafkaProducerFactory(props)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Long> {
        return KafkaTemplate(producerFactory())
    }
}