package ru.command.mephi12.dto.modern_problem

import ru.command.mephi12.constants.ProblemState
import ru.command.mephi12.constants.ProblemType
import java.util.*

data class ProblemDto(
    val id: UUID,
    val statement: String,
    val type: ProblemType,
    val state: ProblemState,
    val sessionId: UUID,
)
