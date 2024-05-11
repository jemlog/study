package com.example.spring_test.client

import org.springframework.stereotype.Component
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@Component
@HttpExchange
interface TestComponent {

    @GetExchange("/test")
    fun get(): String
}