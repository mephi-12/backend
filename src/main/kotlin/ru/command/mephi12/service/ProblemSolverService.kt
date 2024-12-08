package ru.command.mephi12.service

import ru.command.mephi12.dto.ProblemRequest
import ru.command.mephi12.dto.ProblemResponse

interface ProblemSolverService {
    fun solve(request: ProblemRequest): ProblemResponse

}