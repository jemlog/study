package com.example.coroutine

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

// 코루틴은 스레드 위에서 실행되야 한다.
// 스레드는 동시성을 위해 멀티 쓰레드가 필요하지만, 코루틴은 n개의 코루틴이 1개의 쓰레드에서 실행 가능하다.
// 코루틴은 스스로가 yield를 사용해서 양보 가능하다. 스레드는 OS가 직접 스레드 멈춰야 한다. 코루틴은 비선점, 스레드는 선점
// 하나의 코루틴의 코드가 여러 스레드에서 분할적으로 실행 가능하다

// runBlocking을 사용하면 코루틴 영역으로 들어간다
// 이를 코루틴 빌더 라고한다
fun main(): Unit = runBlocking() {

    // lazy는 바로 실행 안된다
    val job = launch(start = CoroutineStart.LAZY) {
        printWithThread("START")
    }

    job.start()

    printWithThread("START")

    // 반환값이 없는 코루틴을 만드는데 사용된다
    launch { // launch는 바로 실행 안되고 main에서 yield 해야 실행이 비로소 된다
        newRoutine()
    }
    yield()
    printWithThread("END")
}

suspend fun newRoutine(){
    val num1 = 1
    val num2 = 2 // 얘네는 메모리에서 안 사라지고 남아있다
    yield() // 이 부분을 사용하면 지금 코루틴 실행 멈추고 다른 코루틴이 실행되도록 양보한다. suspend fun이다
    printWithThread("${num1 + num2}") // main 코루틴이 끝난 뒤에, 마지막에 실행된다
}

fun printWithThread(str: Any){
    println("[${Thread.currentThread().name}] $str")
}