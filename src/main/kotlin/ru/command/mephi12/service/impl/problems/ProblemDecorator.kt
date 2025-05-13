package ru.command.mephi12.service.impl.problems

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.command.mephi12.constants.BACKPACK_QUALIFIER
import ru.command.mephi12.constants.EL_GAMAL_QUALIFIER
import ru.command.mephi12.constants.ProblemState
import ru.command.mephi12.constants.ProblemType
import ru.command.mephi12.database.dao.ProblemDao
import ru.command.mephi12.database.dao.ProblemSessionDao
import ru.command.mephi12.database.entity.Problem
import ru.command.mephi12.database.entity.ProblemConfiguration
import ru.command.mephi12.database.entity.ProblemSession
import ru.command.mephi12.dto.ProblemConfigurationDto
import ru.command.mephi12.dto.ProblemSubmitResponse
import ru.command.mephi12.dto.modern_problem.ProblemSessionDto
import ru.command.mephi12.exception.AppException
import ru.command.mephi12.exception.ResourceNotFoundException
import ru.command.mephi12.service.ProblemsCheckerService
import ru.command.mephi12.service.UserService
import ru.command.mephi12.service.impl.GroupConfigService
import ru.command.mephi12.utils.getPrincipal
import java.util.*

@Service
class ProblemDecorator(
    private val problemServices: Map<String, ProblemsCheckerService<*>>, // получаем bean по его qualifier'у
    private val problemSessionDao: ProblemSessionDao,
    private val problemDao: ProblemDao,
    private val objectMapper: ObjectMapper,
    private val userService: UserService,
    private val groupConfigService: GroupConfigService,
) {
    private fun getAllowedSessionConfigs(): Map<String, List<String>> {
        val groupId = userService.findEntityById(getPrincipal()).group?.id ?: throw ResourceNotFoundException("У вас нет группы!")
        return groupConfigService.enabledConfigs(groupId).associate {
            it.name to it.taskServices
        }
    }

//    val configuration: Map<String, List<String>> = mapOf(
//        "Sem4" to listOf(BACKPACK_QUALIFIER, EL_GAMAL_QUALIFIER, EL_GAMAL_QUALIFIER),
//    )

    @Transactional
    fun createSolvingSession(sessionType: String): ProblemSessionDto {
        if (userService.getCurrentProblemSession() != null) {
            throw AppException("Вы уже решаете набор задач!")
        }
        val configuration = getAllowedSessionConfigs()
        val problemConfig = configuration[sessionType] ?: throw AppException("Problem configuration not found")
        val problemSession = problemSessionDao.save(
            ProblemSession().apply {
                user = userService.findEntityById(getPrincipal())
            }
        )
        problemConfig.forEach { problemQualifier ->
            val problemStatement = problemServices[problemQualifier]!!.generateProblem()
            val problem = problemDao.save(
                Problem(
                    statement = objectMapper.writeValueAsString(problemStatement),
                    type = ProblemType.getByQualifier(problemQualifier)
                ).apply {
                    this.problemSession = problemSession
                }
            )
            problemSession.problems.add(problem)
        }
        return problemSession.toDto()
    }

    @Transactional
    fun check(statementId: UUID, request: String): ProblemSubmitResponse {
        val problem =
            problemDao.findById(statementId).orElseThrow { AppException("Задача с id $statementId не найдена") }
        val problemSession = problem.problemSession
//        problemSession.user.id.takeUnless { it == getPrincipal() }
//            ?: throw AppException("Вы не решаете набор задач с задачей id $statementId")
        problemSession.sessionState.takeUnless { it == ProblemState.NEW }
            ?: throw AppException("Набор задач с этой задачей не решается")
        val problemService = problemServices[problem.type.getQualifier()]!!
        val result = problemService.check(problem.statement, request)
        result.problemId = statementId
        result.problemType = problem.type

        if (result.isOk) {
            problem.state = ProblemState.SOLVED
        } else {
            problem.state = ProblemState.FAILED
        }
        problemDao.save(problem)

        if (problemSession.problems.all { it.state != ProblemState.NEW }) {
            if (problemSession.problems.any { it.state == ProblemState.FAILED }) {
                problemSession.sessionState = ProblemState.FAILED
            } else {
                problemSession.sessionState = ProblemState.SOLVED
            }
        }
        problemSessionDao.save(problemSession)

        return result
    }

    fun getSessions() = problemSessionDao.findAll().map { it.toDto() }
}