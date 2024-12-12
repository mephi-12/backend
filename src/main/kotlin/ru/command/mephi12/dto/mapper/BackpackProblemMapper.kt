package ru.command.mephi12.dto.mapper

import org.springframework.stereotype.Component
import ru.command.mephi12.database.entity.BackpackProblem
import ru.command.mephi12.dto.BackpackProblemResponse

@Component
class BackpackProblemMapper {
    fun entityToResponse(entity: BackpackProblem) : BackpackProblemResponse =
        BackpackProblemResponse(
            id = entity.id,
            createdAt = entity.createdAt,
            state = entity.state,
            power = entity.power,
            type = entity.type.text,
            message = entity.message,
            lightBackpack = entity.lightBackpack,
            omega = entity.omega,
            hardBackpack = entity.hardBackpack,
            encodedMessage = entity.encodedMessage,
            decodedMessage = entity.decodedMessage,
            module = entity.module,
            reverseOmega = entity.reverseOmega,
            errorDescription = entity.errorDescription,
        )
}