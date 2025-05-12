package ru.command.mephi12.dto

import java.util.UUID

data class UserGroupDto(
    val id: UUID,
    val name: String,
    val users: List<UUID>
)
