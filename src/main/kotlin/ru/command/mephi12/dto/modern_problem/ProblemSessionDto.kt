package ru.command.mephi12.dto.modern_problem

import ru.command.mephi12.constants.ProblemState
import java.time.LocalDateTime
import java.util.UUID

data class ProblemSessionDto(
    val id: UUID,
    val createdAt: LocalDateTime,
    val state: ProblemState,
    val userId: UUID,
    val problems: List<ProblemDto>,
)
