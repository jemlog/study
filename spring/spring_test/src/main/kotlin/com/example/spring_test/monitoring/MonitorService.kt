package com.example.spring_test.monitoring

import com.example.spring_test.monitoring.domain.User
import com.example.spring_test.monitoring.domain.UserRepository
import io.micrometer.observation.annotation.Observed
import org.springframework.stereotype.Service

@Observed(name = "monitorService")
@Service
class MonitorService(private val userRepository: UserRepository) {

    fun order(){

        for(i in 1..10000000){
            val user = User(1L, "jemin")
        }

        userRepository.findAll()

    }

    fun cancel(){
        print("cancel")
    }
}