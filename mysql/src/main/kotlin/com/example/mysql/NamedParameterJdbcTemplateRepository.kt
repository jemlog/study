package com.example.mysql

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.DriverManager


@Repository
class NamedParameterJdbcTemplateRepository(
    private val jdbc: NamedParameterJdbcTemplate
) {

    @Transactional
    fun batchUpdate(users: List<User>){

        val namedParameters = users.map { it.toSqlParam() }

        jdbc.batchUpdate(
            """ 
          INSERT INTO USER
          (name, grade, createdAt) 
          VALUES 
          (:name, :grade, :createdAt)
          """.trimIndent(),
            namedParameters.toTypedArray()
        )
    }
}