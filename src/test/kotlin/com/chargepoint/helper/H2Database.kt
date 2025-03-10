package com.chargepoint.helper

import org.springframework.jdbc.core.JdbcTemplate
import java.util.function.Consumer


/**
 *
 */
object H2Database {
    @JvmStatic
    fun truncateAllTables(jdbcTemplate: JdbcTemplate) {

        val listOfTable = jdbcTemplate.queryForList(
            "SELECT table_name FROM information_schema.tables " +
                    "WHERE table_schema = SCHEMA()", String::class.java
        )

        val listOfSchema = jdbcTemplate.queryForList(
            "SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'",
            String::class.java
        )

        listOfTable.forEach(Consumer { tableName: String ->
            jdbcTemplate.execute(
                "TRUNCATE TABLE \"$tableName\" RESTART IDENTITY"
            )
        })

        listOfSchema.forEach(Consumer { schemaName: String ->
            jdbcTemplate.execute(
                "ALTER SEQUENCE $schemaName RESTART WITH 1"
            )
        })

    }
}