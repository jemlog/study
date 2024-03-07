package com.example.cache.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CacheService {

    @Cacheable(cacheNames = ["user"], key = "#cacheId")
    fun get(cacheId: Long): Test{
        return Test("제민",26)
    }
}