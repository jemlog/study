package com.example.mysql

import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import javax.sql.DataSource

@Repository
class PreparedStatementRepository {

    fun insertWithDriverManager(){

        var conn: Connection? = null
        var pstmt: PreparedStatement? = null

        try {
            val url = "jdbc:mysql://localhost:3306/mysql_study_db?useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC"

            conn = DriverManager.getConnection(url,"root","1234")
            pstmt = conn.prepareStatement("insert into user (name) values (?)")
            pstmt.setString(1, "test")
            pstmt.executeUpdate()
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        finally {
            pstmt!!.close()
            conn!!.close()
        }
    }

    fun insertWithDataSource(){

        var conn: Connection? = null
        var pstmt: PreparedStatement? = null

        try {
            val url = "jdbc:mysql://localhost:3306/mysql_study_db?useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC"

            val dataSource = DriverManagerDataSource(url, "root", "1234")
            conn = dataSource.getConnection()
            pstmt = conn.prepareStatement("insert into user (name) values (?)")
            pstmt.setString(1,"dataSource test")
            pstmt.executeUpdate()
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        finally {
            pstmt!!.close()
            conn!!.close()
        }
    }
}