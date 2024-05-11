package com.example.network_test

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import java.time.Duration


@Configuration
class RestClientConfig {

    /*
    HttpInterface를 사용하기 위한 RestClient 설정
     */
    @Bean
    fun repositoryService(): RepositoryService{

        val requestFactory = HttpComponentsClientHttpRequestFactory()


        val restClient = RestClient.builder()
            .requestFactory(requestFactory)
            .baseUrl("https://www.naver.com")
            .build()
        val adapter = RestClientAdapter.create(restClient)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()
        return factory.createClient(RepositoryService::class.java)
    }
}