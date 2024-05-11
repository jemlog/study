package com.example.mysql

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import java.time.LocalDateTime

@SpringBootTest
class BatchTest {
    
    @Autowired
    lateinit var batchRepository: NamedParameterJdbcTemplateRepository

    @Autowired
    lateinit var preparedStatementRepository: PreparedStatementRepository
    
    @DisplayName("NamedParameterJdbcTemplate Batch Insert 테스트")
    @Test
    fun test(){
        val users = listOf(
            User("jemin",26,"FIRST", LocalDateTime.now()),
            User("jemin",26,"FIRST", LocalDateTime.now()),
            User("jemin",26,"FIRST", LocalDateTime.now()),
            User("jemin",26,"FIRST", LocalDateTime.now())
        )

        batchRepository.batchUpdate(users)
    }

    @DisplayName("DriverManager PreparedStatement 테스트")
    @Test
    fun preparedStatement_test_driverManager(){
        preparedStatementRepository.insertWithDriverManager()
    }

    @DisplayName("DataSource PreparedStatement 테스트")
    @Test
    fun preparedStatement_test_dataSource(){
        preparedStatementRepository.insertWithDataSource()
    }
}