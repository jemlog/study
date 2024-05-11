package com.example.reactive

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.*
import java.time.Duration


@SpringBootTest
class ReactiveRedisTemplateTest {

    @Autowired
    lateinit var redisTemplate: ReactiveRedisTemplate<String, String>

    lateinit var reactiveListOps: ReactiveListOperations<String, String>
    lateinit var reactiveSetOps: ReactiveSetOperations<String, String>
    lateinit var reactiveValueOps: ReactiveValueOperations<String, String>
    lateinit var reactiveZSetOps: ReactiveZSetOperations<String, String>
    lateinit var reactiveHashOps: ReactiveHashOperations<String, String, String>

    @BeforeEach
    fun setUp() {
        reactiveValueOps = redisTemplate.opsForValue()
        reactiveListOps = redisTemplate.opsForList()
        reactiveHashOps = redisTemplate.opsForHash()
        reactiveSetOps = redisTemplate.opsForSet()
        reactiveZSetOps = redisTemplate.opsForZSet()
    }

    @Test
    fun givenListAndValues_whenLeftPushAndLeftPop_thenLeftPushAndLeftPop() {
        redisTemplate.convertAndSend("hello_channel", "hello").subscribe()
        reactiveValueOps.set("test_key","4", Duration.ofMinutes(1L)).subscribe()
    }

}