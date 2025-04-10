package ru.command.mephi12.service

import com.fasterxml.jackson.databind.ObjectMapper
import ru.command.mephi12.constants.ProblemState
import ru.command.mephi12.constants.ProblemType
import ru.command.mephi12.database.dao.ProblemHistoryDao
import ru.command.mephi12.database.entity.ProblemHistory
import ru.command.mephi12.dto.AbstractProblemResponse
import ru.command.mephi12.exception.AppException
import ru.command.mephi12.utils.getPrincipal
import java.util.*
import kotlin.jvm.optionals.getOrElse

abstract class AbstractProblemCheckerService<PROBLEM_RESPONSE : AbstractProblemResponse, PROBLEM_SUBMIT_REQUEST>(
    val problemHistoryDao: ProblemHistoryDao,
    val type: ProblemType,
    val objectMapper: ObjectMapper,
    val userService: UserService,
) : ProblemsCheckerService<PROBLEM_RESPONSE, PROBLEM_SUBMIT_REQUEST> {
    override fun check(id: UUID, request: PROBLEM_SUBMIT_REQUEST): PROBLEM_RESPONSE {
        val problemHistory = problemHistoryDao.findById(id).getOrElse { throw AppException() }
        val result = check(request)
        if(result.isOk) {
            problemHistory.state = ProblemState.SOLVED
        }
        return result
    }
    override fun generateProblem(): PROBLEM_RESPONSE {
        val problem = generate()
        val problemHistory = ProblemHistory(type, objectMapper.writeValueAsString(problem)).apply {
            user = userService.findEntityById(getPrincipal())
        }
        problemHistoryDao.save(problemHistory)
        return problem
    }

    abstract fun check(request: PROBLEM_SUBMIT_REQUEST) : PROBLEM_RESPONSE
    abstract fun generate(): PROBLEM_RESPONSE
}