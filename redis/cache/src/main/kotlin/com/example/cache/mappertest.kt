package com.example.cache

import com.example.cache.service.Test
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.time.LocalDateTime

fun main(){
    val objectMapper = jacksonObjectMapper().registerModule(JavaTimeModule()) // kotlin 모듈
    val value = objectMapper.writeValueAsString(Test("jemin", 12))
    val readValue: Test = objectMapper.readValue(value) // 어떤 객체 타입으로 역직렬화를 해야하는지 꼭 명시가 되야 한다. RedisSerializer는 이때 함께 직렬화되는 클래스 정보를 사용하는 것이다.
    // 만약 별도의 서비스를 사용한다면 같은 디렉토리에 그 클래스가 있는게 아니라면 직렬화 실패
    print(readValue)
}