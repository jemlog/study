package com.example.cache.config


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration
import java.util.*


@EnableCaching
@Configuration
class CacheConfig(
    @Value("\${spring.data.redis.host}")
    var hostName: String,

    @Value("\${spring.data.redis.port}")
    var port: Int,

    @Value("\${spring.data.redis.connect-timeout}")
    val timeout: Long,
) {

    @Bean(name = ["redisCacheConnectionFactory"])
    fun redisCacheConnectionFactory(): RedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = hostName
        redisStandaloneConfiguration.port = port
        val lettuceClientConfiguration = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofMillis(timeout)).build()
        return LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration)
    }

    @Bean
    fun cacheManager(): CacheManager {

        val typeValidator = BasicPolymorphicTypeValidator
            .builder()
            .allowIfSubType(Any::class.java) // Any의 서브타입이기만 하면 된다
            .build()

        // 캐시에 특화된 ObjectMapper라면 이 내부에서 등록해 사용하는 것도 맞다고 생각한다
        val objectMapper = ObjectMapper()
            .registerKotlinModule() // 이게 있어야 코틀린에서도 기본 생성자를 만들어서 역직렬화가 가능하다
            .activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL) // GenericJackson2JsonRedisSerializer가 기본으로 사용하는 DefaultTyping은 NON_FINAL
            .setTimeZone(TimeZone.getTimeZone("Asia/Seoul"))
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        val defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            // GenericJackson2JsonRedisSerializer를 사용하면 애초에 no-constructor 못 만들어서 에러 발생
            // 다른 Serializer들은 받을때 어떤 타입으로 받을지를 명시, 하지만 이거는 데이터 내부에 타입이 명시 그래서 만약 데이터 타입 없으면 뭔지 모르니깐 바로 LinkedHashMap 사용
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer(objectMapper))) // 반드시 상대방 서비스에서도 똑같은 디렉토리에 객체가 있어야 해서 MSA에 부적합
            .entryTtl(Duration.ofMinutes(100))

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisCacheConnectionFactory())
                .cacheDefaults(defaultConfig)
                .build()

    }
}