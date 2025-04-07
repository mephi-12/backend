package ru.command.mephi12.dto.auth

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import ru.command.mephi12.constants.EMAIL_REGEX

data class LoginRequest(
    @field:Pattern(regexp = EMAIL_REGEX)
    @field:Size(min = 4, max = 120)
    val email: String,
    @field:Size(min = 8, max = 255)
    val password: String,
)