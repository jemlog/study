package com.example.coroutine

import kotlinx.coroutines.*
import java.util.concurrent.Executors


suspend fun main(){

    val job3 = CoroutineScope(Dispatchers.Default).launch {

        launch {
            printWithThread("API CALL 1")
            delay(1_000L)
            printWithThread("API CALL 3")
        }

        launch {
            printWithThread("API CALL 2")
        }
    }

    job3.join()
}