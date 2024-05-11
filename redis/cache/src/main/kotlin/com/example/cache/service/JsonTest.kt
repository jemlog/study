package com.example.cache.service

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.LocalDateTime
import java.time.ZonedDateTime

/*
전체 전략 설정
 */
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL) // 해당 조건에 맞는것만 JSON 문자열 변환 시 전환된다
class JsonTest(
    // snake case로 들어오면 camel case로 매핑해준다
    @JsonIgnore // 해당 어노테이션 붙이면 JSON 문자열 변환 시 무시된다
    @JsonProperty(value = "my_name")
    val myName: String,
    @JsonProperty(value = "my_age")
    val myAge: Int,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    val time: LocalDateTime, // Customizer를 사용하는 방법도 있다.
){
}
