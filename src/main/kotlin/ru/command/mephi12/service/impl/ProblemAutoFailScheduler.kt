package ru.command.mephi12.service.impl

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.command.mephi12.constants.ProblemState
import ru.command.mephi12.database.dao.ProblemDao
import ru.command.mephi12.database.dao.ProblemSessionDao
import ru.command.mephi12.database.entity.ProblemSession

@Service
class ProblemAutoFailScheduler(
    private val problemDao: ProblemDao,
    private val problemSessionDao: ProblemSessionDao
) {

    companion object {
        private val log = LoggerFactory.getLogger(ProblemAutoFailScheduler::class.java)
    }
    //неэффективно
    @Transactional
    @Scheduled(fixedRate = 60_000) // ms
    fun transitToFailed() {
        problemDao.expireProblems().also {
            log.info("$it problems expired")
        }
        val problemSessions = problemSessionDao.findAll()
        for (problemSession in problemSessions) {
            if(problemSession.problems.none { it.state == ProblemState.NEW } && problemSession.sessionState == ProblemState.NEW) {
                problemSession.sessionState = ProblemState.SOLVED
            }
            problemSessionDao.save(problemSession)
        }
    }
}