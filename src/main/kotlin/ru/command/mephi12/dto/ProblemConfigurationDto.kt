package ru.command.mephi12.dto

import java.util.*

data class ProblemConfigurationDto(
    val id: UUID,
    val name: String,
    val tasks: List<String>,
    val enabled: Boolean,
)