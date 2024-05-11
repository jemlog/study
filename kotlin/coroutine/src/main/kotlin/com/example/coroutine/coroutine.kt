package com.example.coroutine

import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors


fun main(){

    val vt = Thread.ofVirtual().unstarted { println(Thread.currentThread().isVirtual) }

    vt.start()

    vt.join()
}