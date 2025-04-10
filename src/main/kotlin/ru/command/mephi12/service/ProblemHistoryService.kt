package ru.command.mephi12.service

import ru.command.mephi12.constants.ProblemState
import java.util.*

interface ProblemHistoryService {
    fun createProblem()
    fun solveProblem(problemId: UUID, verdict: ProblemState, request: Any)
}