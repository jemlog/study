package com.example.oop.converter.db

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class OrderStatusConverter : AttributeConverter<OrderStatus, String> {

    override fun convertToDatabaseColumn(attribute: OrderStatus?): String? {
        return attribute?.legacyCode
    }

    override fun convertToEntityAttribute(dbData: String?): OrderStatus? {

        if(dbData == null){
            return null;
        }

        return OrderStatus.ofLegacyCode(dbData)
    }
}