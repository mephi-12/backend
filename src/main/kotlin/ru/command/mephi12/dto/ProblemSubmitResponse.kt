package ru.command.mephi12.dto

import ru.command.mephi12.constants.ProblemType
import java.util.UUID

data class ProblemSubmitResponse(
    var problemId: UUID? = null,
    var isOk: Boolean,
    var problemType: ProblemType? = null,
    var errorMessage: String? = null,
)
