package com.example.spring_test.config

import org.slf4j.MDC
import org.springframework.core.task.TaskDecorator

class MDCTaskDecorator: TaskDecorator {
    override fun decorate(runnable: Runnable): Runnable {
        // 요청 쓰레드의 MDC Context Map을 복제한다.
        val copyOfContextMap = MDC.getCopyOfContextMap()

        // 복제한 MDC Context Map을 비동기 작업 쓰레드의 MDC에 전달한다.
        return Runnable {
            MDC.setContextMap(copyOfContextMap)
            runnable.run()
        }
    }
}