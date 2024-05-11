package com.example.spring_test.monitoring

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MonitorController(private val monitorService: MonitorService) {

    @GetMapping("/monitor/order")
    fun order(){
        monitorService.order()
    }

    @GetMapping("monitor/cancel")
    fun cancel(){
        monitorService.cancel()
    }
}