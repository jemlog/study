package com.example.cache.config


import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.lettuce.core.ClientOptions
import io.lettuce.core.ReadFrom
import io.lettuce.core.TimeoutOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration
import java.util.*


@EnableCaching
@Configuration
class CacheConfig(
    @Value("\${spring.data.redis.host}")
    var host: String,

    @Value("\${spring.data.redis.port}")
    var port: Int,
) {

    @Bean(name = ["redisCacheConnectionFactory"])
    fun redisCacheConnectionFactory(): RedisConnectionFactory {

        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = host
        redisStandaloneConfiguration.port = port
        val lettuceClientConfiguration = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofMillis(1_000L))
            .readFrom(ReadFrom.MASTER)
            .build()

        return LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration)
    }





    @Bean
    fun cacheManager(): CacheManager {

        val typeValidator = BasicPolymorphicTypeValidator
            .builder()
            .allowIfSubType(Any::class.java)
            .build()

        val objectMapper = ObjectMapper()
            .registerKotlinModule() // 역직렬화를 위해서는 기본 생성자가 필수. 해당 모듈을 추가하면 코틀린 객체를 문제없이 역직렬화 가능
            .activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.EVERYTHING) // 코틀린에서는 NON-FINAL 하면 안됨. 기본적으로 모두 final
            .setTimeZone(TimeZone.getTimeZone("UTC"))
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        val defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer(objectMapper)))
         //   .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer(Charset.defaultCharset())))
         //   .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(Jackson2JsonRedisSerializer(objectMapper, Test::class.java)))
            .entryTtl(Duration.ofMinutes(1))
            .disableCachingNullValues()


        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisCacheConnectionFactory())
                .cacheDefaults(defaultConfig)
                .build()
    }
}