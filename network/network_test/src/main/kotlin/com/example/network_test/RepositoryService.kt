package com.example.network_test

import org.springframework.web.service.annotation.GetExchange

interface RepositoryService {

    @GetExchange("/hello")
    fun getRepository()
}