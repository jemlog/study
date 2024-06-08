package com.example.oop.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class NicknameValidator : ConstraintValidator<Nickname, String> {

    private lateinit var invalidWord: Array<String>

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if(value == null){
            return true
        }

        return invalidWord.find {
            value.contains(it)
        } == null
    }

    override fun initialize(constraintAnnotation: Nickname) {
        invalidWord = constraintAnnotation.invalidWord
    }
}