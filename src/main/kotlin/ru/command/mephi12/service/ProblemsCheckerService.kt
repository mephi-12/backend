package ru.command.mephi12.service

import ru.command.mephi12.dto.BackpackProblemEditorialRequest
import ru.command.mephi12.dto.BackpackProblemSubmitRequest
import ru.command.mephi12.dto.EditorialProblemSolutionRequest
import ru.command.mephi12.dto.EditorialTaskCheckRequest

interface ProblemsCheckerService {
    fun check(request: BackpackProblemEditorialRequest, response: EditorialProblemSolutionRequest)
    fun check(request: EditorialTaskCheckRequest)
}