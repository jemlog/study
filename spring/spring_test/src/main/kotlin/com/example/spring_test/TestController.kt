package com.example.spring_test

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    val log: Logger = LoggerFactory.getLogger(TestController::class.java)


    @GetMapping("/test")
    fun test(){

        runBlocking(MDCContext()) {
            log.info("실행")
        }

        log.info("testController 호출")
    }
}