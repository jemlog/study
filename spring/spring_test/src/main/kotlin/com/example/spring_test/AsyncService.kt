package com.example.spring_test

import com.example.spring_test.client.TestClient
import kotlinx.coroutines.*
import kotlinx.coroutines.slf4j.MDCContext
import org.springframework.stereotype.Service

@Service
class AsyncService {

    suspend fun asyncCall(): String = withContext(Dispatchers.Default){

        val value1 = async {
            delay(5_000L)
            "123"
        }

        val value2 = async {
            delay(5_000L)
            "456"
        }

        value1.await() + value2.await()
    }
}