package com.example.cache

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveSetOperations
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.redis.core.ReactiveZSetOperations
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration


@SpringBootTest
class ReactiveRedisTemplateTest {

    @Autowired
    lateinit var redisTemplate: ReactiveRedisTemplate<String,String>
    lateinit var reactiveListOps: ReactiveListOperations<String,String>
    lateinit var reactiveSetOps: ReactiveSetOperations<String,String>
    lateinit var reactiveValueOps: ReactiveValueOperations<String,String>
    lateinit var reactiveZSetOps: ReactiveZSetOperations<String,String>

    @BeforeEach
    fun setUp(){
        reactiveListOps = redisTemplate.opsForList()
        reactiveSetOps = redisTemplate.opsForSet()
        reactiveValueOps = redisTemplate.opsForValue()
        reactiveZSetOps = redisTemplate.opsForZSet()
    }

    @Test
    fun givenListAndValues_whenLeftPushAndLeftPop_thenLeftPushAndLeftPop() {
     //   reactiveListOps.leftPushAll("hello", "jm", "ki").block() // 이렇게 해야 데이터 저장된다
        redisTemplate.convertAndSend("hello_channel","hello").subscribe()
     //   reactiveSetOps.add("hello_set","1","2","3").block()
     //   reactiveZSetOps.add("hello_zset","1", Double.MAX_VALUE).block()
//        reactiveValueOps.set("test_value", "3").subscribe() // subscribe를 해야 실제 메세지를 보낸다
//        for(i in 0..100){
//            redisTemplate.convertAndSend("hello",i.toString()).subscribe()
//        }
    }

}