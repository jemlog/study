package com.example.cache.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class CacheService{

    /*
    캐시를 할때는 가장 겉을 감싸고 있는 클래스만 open으로 non-final 상태면 된다
     */
    @Cacheable(cacheNames = ["user"], key = "#cacheId") // user::2
    fun get(cacheId: Long): Test {
        return Test("jemin",24)
    }
//
//
////
//    fun <T> get2(fallback: () -> T, clazz: T) :T {
//        val stringifyCachedValue = getFromCache()
//
//        if(stringifyCachedValue==null){
//            val originResult = fallback()
//
//            cache.put(objectMapper.writeValue(originResult))
//            return originResult
//        }else{
//            try {
//                return objectmaper.readValue(stringifyCachedValue,T::Cl)
//            }
//            catch ()
//
//        }
//    }

    fun getFromCache() :String {
        val restClient = RestClient.create()
        val result = restClient.get()
            .uri("https://www.naver.com")
            .retrieve()
            .toEntity(String::class.java)
        println(result)
        println(result.statusCode)
        println(result.headers)
        return result.body!!;
    }
}