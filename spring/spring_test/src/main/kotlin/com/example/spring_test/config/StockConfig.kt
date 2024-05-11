package com.example.spring_test.config

import com.example.spring_test.monitoring.domain.UserRepository
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StockConfig {

    @Bean
    fun myStockMetric(userRepository: UserRepository, registry: MeterRegistry): MyStockMetric{
        return MyStockMetric(userRepository, registry)
    }

    companion object {
        val log:Logger = LoggerFactory.getLogger(MyStockMetric::class.java)

        class MyStockMetric(
            private val userRepository: UserRepository,
            private val registry: MeterRegistry
        ){

            @PostConstruct
            fun init(){
                Gauge.builder("my.stock", userRepository) {
                    repository -> repository.count().toDouble()
                }.register(registry)
            }
        }
    }
}