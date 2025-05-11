package ru.command.mephi12.service.impl

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.command.mephi12.database.dao.ProblemDao

@Service
class ProblemAutoFailScheduler(
    private val problemDao: ProblemDao
) {

    companion object {
        private val log = LoggerFactory.getLogger(ProblemAutoFailScheduler::class.java)
    }
    @Transactional
    @Scheduled(fixedRate = 60_000) // ms
    fun transitToFailed() {
        problemDao.expireProblems().also {
            log.info("$it problems expired")
        }
    }
}