package com.example.coroutine

import kotlinx.coroutines.*

/*
부모 코루틴과 자식 코루틴 관계에서 중요한 점
자식 코루틴에서 예외가 발생하면 부모 코루틴도 실패하고 다른 자식도 실패한다
트랜잭션 전파와 비슷한 느낌
이걸 Structured Concurrency라고 부른다

만약 자식 코루틴들은 실패하지 않았는데, 부모가 실패하면 자식도 취소된다.
하지만 자식에서 CancellationException은 정상 취소로 간주하기 때문에 전파 안되고 다른 자식도 취소 안한다
 */
//fun main(): Unit = runBlocking{
//
//    launch {
//        delay(600L)
//        printWithThread("A")
//    }
//
//    launch {
//        delay(500L)
//        throw CancellationException("코루틴 실패!")
//    }
//}

suspend fun main(){

    // Dispatcher는 코루틴을 어떤 스레드에 배정할지를 결정한다
    val job = CoroutineScope(Dispatchers.Default).launch {
        delay(1_000L)
        printWithThread("Job 1")
    }

    job.join()
}