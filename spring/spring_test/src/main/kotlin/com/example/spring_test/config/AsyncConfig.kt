package com.example.spring_test.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.core.task.support.TaskExecutorAdapter
import org.springframework.scheduling.annotation.EnableAsync
import java.util.concurrent.Executors

@Configuration
@EnableAsync
class AsyncConfig {

    /*
    Async에 Virtual Thread 사용하기
    MDC 전파까지 적용
     */
    @Bean
    fun taskExecutor(): TaskExecutor {
        val taskExecutorAdapter = TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor())
        taskExecutorAdapter.setTaskDecorator(MDCTaskDecorator())

        return taskExecutorAdapter
    }
}