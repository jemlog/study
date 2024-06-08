package com.example.oop.converter.web

import com.example.oop.converter.db.OrderStatus
import org.springframework.core.convert.converter.Converter

/*
WebConfig에 등록하면 String으로 들어왔을때 자동으로 Enum으로 Mapping한다.
 */
class OrderControllerConverter : Converter<String, OrderStatus> {
    override fun convert(source: String): OrderStatus {
        return OrderStatus.ofLegacyCode(source)
    }
}