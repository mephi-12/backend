package ru.command.mephi12.service

import ru.command.mephi12.database.entity.User
import ru.command.mephi12.dto.auth.RegistrationRequest
import java.util.UUID

interface UserService {
    fun createUser(request: RegistrationRequest): User
    fun existByEmail(email: String): Boolean
    fun findEntityByEmail(email: String): User
    fun findEntityById(id: UUID): User
    fun getSelfProfile(userId: UUID)
    fun getUserProfile(userId: UUID)
}