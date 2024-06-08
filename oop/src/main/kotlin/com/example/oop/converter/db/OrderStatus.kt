package com.example.oop.converter.db

import java.util.Arrays

enum class OrderStatus(val legacyCode: String) {
    PREPARE_PAYMENT("1"),
    COMPLETE_PAYMENT("2"),
    SHIPPED("3"),
    CANCELED("4");

    companion object{
        fun ofLegacyCode(legacyCode: String): OrderStatus {
            return Arrays.stream(entries.toTypedArray())
                .filter { it.legacyCode == legacyCode }
                .findAny()
                .orElseThrow()
        }
    }
}