package ru.command.mephi12.database.dao

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import ru.command.mephi12.database.entity.Problem

interface ProblemDao : AbstractDao<Problem> {

    @Modifying
    @Query("""
        UPDATE problem
               SET state = 'FAILED'
             WHERE state = 'NEW'
               AND created_at < (now() - INTERVAL '20 MINUTES')
        """, nativeQuery = true)
    fun expireProblems(): Int
}