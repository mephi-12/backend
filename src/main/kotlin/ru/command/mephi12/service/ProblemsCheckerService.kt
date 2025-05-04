package ru.command.mephi12.service

import ru.command.mephi12.dto.ProblemSubmitResponse

interface ProblemsCheckerService<PROBLEM_RESPONSE> {
    fun check(statement: String, solutionRequest: String) : ProblemSubmitResponse
    fun generateProblem() : PROBLEM_RESPONSE
}