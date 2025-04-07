package ru.command.mephi12.dto.mapper

import org.springframework.stereotype.Component
import ru.command.mephi12.database.entity.User
import ru.command.mephi12.dto.auth.RegistrationRequest

@Component
class UserMapper {
    fun asEntity(request: RegistrationRequest): User =
        User(
            email = request.email,
            name = request.name,
        )
}