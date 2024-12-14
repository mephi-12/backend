package ru.command.mephi12.service

import ru.command.mephi12.dto.*
import java.util.*

interface ProblemsCheckerService {
    fun check(id: UUID, request: BackpackProblemSubmitRequest) : BackpackProblemResponse
    fun generateTask() : BackpackProblemResponse
}