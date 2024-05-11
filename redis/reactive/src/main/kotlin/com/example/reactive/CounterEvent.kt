package com.example.reactive

import java.time.ZoneId
import java.time.ZonedDateTime

class CounterEvent(
    val value: Long,
    val at: ZonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"))
) {

}