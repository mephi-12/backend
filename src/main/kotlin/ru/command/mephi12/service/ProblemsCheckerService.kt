package ru.command.mephi12.service

import ru.command.mephi12.dto.BackpackProblemEditorialRequest
import ru.command.mephi12.dto.BackpackProblemSubmitRequest

interface ProblemsCheckerService {
    fun check(request: BackpackProblemEditorialRequest, response: BackpackProblemSubmitRequest)
}