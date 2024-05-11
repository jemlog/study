package com.example.network_test

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.RestClient


@SpringBootTest
class ClientTest {


    @DisplayName("RestClient Test")
    @Test
    fun test(){
        // given
        val client = RestClient.create()
        val value = client.get()
            .uri("https://www.naver.com")
            .retrieve()


        // when
        println(value)
        // then
    }
}