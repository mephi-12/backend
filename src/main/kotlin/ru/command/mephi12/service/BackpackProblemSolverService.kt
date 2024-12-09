package ru.command.mephi12.service

import ru.command.mephi12.dto.BackpackProblemRequest
import ru.command.mephi12.dto.BackpackProblemDto

interface BackpackProblemSolverService {
    fun solve(request: BackpackProblemRequest): BackpackProblemDto

}