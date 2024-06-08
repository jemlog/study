package com.example.coroutine

import kotlinx.coroutines.*
import java.util.concurrent.Executors

/*
suspend가 붙어야 suspend를 호출할 수 있다.
코루틴이 중지되었다가 재개 될 수 있는 지점
 */
suspend fun main(){



    val threadPool = Executors.newSingleThreadExecutor()
    CoroutineScope(threadPool.asCoroutineDispatcher()).launch {

    }

    CoroutineName("나만의 코루틴") + SupervisorJob()

    /*
    CoroutineContext 내에는 코루틴 이름, CoroutineExceptionHandler, 코루틴 그 자체, CoroutineDispathcer
    CoroutineScope : 코루틴이 탄생할 수 있는 영역
    CoroutineContext : 코루틴과 관련된 데이터를 보관
     */
    val job = CoroutineScope(Dispatchers.Default).launch {
        delay(1_000L)
        print("job")
        coroutineContext + CoroutineName("이름")
        coroutineContext.minusKey(CoroutineName.Key)
    }

    job.join()
}

class AsyncLogic{
    private val scope = CoroutineScope(Dispatchers.Default)

    fun doSomething(){
        scope.launch {

        }
    }

    fun destroy(){
        scope.cancel()
    }
}