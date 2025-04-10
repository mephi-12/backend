package ru.command.mephi12.dto.mapper

import org.springframework.stereotype.Component
import ru.command.mephi12.database.entity.BackpackProblem
import ru.command.mephi12.dto.BackpackProblemEditorialRequest
import ru.command.mephi12.dto.BackpackProblemResponse
import ru.command.mephi12.dto.BackpackProblemSubmitRequest

@Component
class BackpackProblemMapper {
    fun entityToResponse(entity: BackpackProblem) : BackpackProblemResponse =
        BackpackProblemResponse(
            id = entity.id,
            createdAt = entity.createdAt,
            state = entity.state.value,
            power = entity.power,
            type = entity.type.description,
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
    fun requestToEntity(req: BackpackProblemEditorialRequest) : BackpackProblem =
        BackpackProblem(
            power = req.power,
            type = req.type,
            message = req.message,
            lightBackpack = req.lightBackpack,
            omega = req.omega,
            encodedMessage = null,
            decodedMessage = null,
            hardBackpack = null,
            errorDescription = null,
            module = null,
            reverseOmega = null,
        )

    fun modifyEntity(ent: BackpackProblem, req: BackpackProblemSubmitRequest) =
        ent.apply {
            lightBackpack = req.lightBackpack
            omega = req.omega
            hardBackpack = req.hardBackpack
            encodedMessage = req.encodedMessage
            decodedMessage = req.decodedMessage
            module = req.module
            reverseOmega = req.reverseOmega
        }

}