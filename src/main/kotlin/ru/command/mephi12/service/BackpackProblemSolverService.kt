package ru.command.mephi12.service

import ru.command.mephi12.dto.BackpackProblemEditorialRequest
import ru.command.mephi12.dto.BackpackProblemEditorialResponse

interface BackpackProblemSolverService {
    fun solve(request: BackpackProblemEditorialRequest): BackpackProblemEditorialResponse

}