package com.example.oop.money

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.math.BigDecimal

@Converter
class MoneyConverter : AttributeConverter<Money,BigDecimal> {

    override fun convertToDatabaseColumn(money: Money): BigDecimal {
        return money.amount
    }

    override fun convertToEntityAttribute(value: BigDecimal): Money {
        return Money(value)
    }
}
