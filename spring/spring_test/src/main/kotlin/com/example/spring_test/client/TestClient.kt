package com.example.spring_test.client

import org.springframework.stereotype.Component

@Component
class TestClient(
    private val testComponent: TestComponent
){
    fun get(): String{
        return testComponent.get()
    }
}