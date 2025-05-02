package ru.command.mephi12.service.impl.problems

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import ru.command.mephi12.constants.EL_GAMAL_QUALIFIER
import ru.command.mephi12.constants.ProblemState
import ru.command.mephi12.constants.ProblemType
import ru.command.mephi12.database.dao.ProblemDao
import ru.command.mephi12.database.dao.ProblemSessionDao
import ru.command.mephi12.database.entity.Problem
import ru.command.mephi12.database.entity.ProblemSession
import ru.command.mephi12.dto.ProblemSubmitResponse
import ru.command.mephi12.dto.modern_problem.ProblemSessionDto
import ru.command.mephi12.exception.AppException
import ru.command.mephi12.service.ProblemsCheckerService
import ru.command.mephi12.service.UserService
import ru.command.mephi12.utils.getPrincipal
import java.util.*

@Service
class ProblemDecorator(
    private val problemServices: Map<String, ProblemsCheckerService<*>>, // получаем bean по его qualifier'у
    private val problemSessionDao: ProblemSessionDao,
    private val problemDao: ProblemDao,
    private val objectMapper: ObjectMapper,
    private val userService: UserService
) {
    val configuration: Map<String, List<String>> = mapOf(
        "Sem4" to listOf(EL_GAMAL_QUALIFIER, EL_GAMAL_QUALIFIER, EL_GAMAL_QUALIFIER),
    )

    fun createSolvingSession(sessionType: String) : ProblemSessionDto {
        userService.getCurrentProblemSession().takeUnless { it == null} ?: throw AppException("Вы уже решаете набор задач!")
        val problemConfig = configuration[sessionType] ?: throw AppException("Problem configuration not found")
        val problemSession = ProblemSession().apply {
            user = userService.findEntityById(getPrincipal())
        }
        problemConfig.forEach { problemQualifier ->
            val problemStatement = problemServices[problemQualifier]!!.generateProblem()
            val problem = Problem(
                statement = objectMapper.writeValueAsString(problemStatement),
                type = ProblemType.getByQualifier(problemQualifier)
            )
            problemDao.save(problem)
            problemSession.problems.add(problem)
        }
        problemSessionDao.save(problemSession)
        return problemSession.toDto()
    }

    fun check(statementId: UUID, request: String): ProblemSubmitResponse {
        val problem = problemDao.findById(statementId).orElseThrow { AppException("Задача с id $statementId не найдена") }
        val problemSession = problem.problemSession
        problemSession.user.id.takeUnless { it == getPrincipal() } ?: throw AppException("Вы не решаете набор задач с задачей id $statementId")
        problemSession.sessionState.takeUnless { it == ProblemState.NEW } ?: throw AppException("Набор задач с этой задачей не решается")
        val problemService = problemServices[problem.type.getQualifier()]!!
        val result = problemService.check(problem.statement, request)
        result.problemId = statementId
        result.problemType = problem.type

        if(result.isOk) {
            problem.state = ProblemState.SOLVED
        } else {
            problem.state = ProblemState.FAILED
        }
        problemDao.save(problem)

        if(problemSession.problems.all { it.state != ProblemState.NEW }) {
            if(problemSession.problems.any { it.state == ProblemState.FAILED }) {
                problemSession.sessionState = ProblemState.FAILED
            } else {
                problemSession.sessionState = ProblemState.SOLVED
            }
        }
        problemSessionDao.save(problemSession)

        return result
    }
}