package ru.command.mephi12.service

import ru.command.mephi12.dto.AbstractProblemResponse
import java.util.*

interface ProblemsCheckerService<PROBLEM_RESPONSE: AbstractProblemResponse, PROBLEM_SUBMIT_REQUEST> {
    fun check(id: UUID, request: PROBLEM_SUBMIT_REQUEST) : PROBLEM_RESPONSE
    fun generateProblem() : PROBLEM_RESPONSE
}