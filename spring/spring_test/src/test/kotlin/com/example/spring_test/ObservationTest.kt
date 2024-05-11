package com.example.spring_test

import com.example.spring_test.observation.ObservationService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ObservationTest {

    @Autowired
    lateinit var observationService: ObservationService

    @Test
    fun observation(){
        observationService.observe()
    }
}