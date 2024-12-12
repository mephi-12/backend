package ru.command.mephi12.dto

import ru.command.mephi12.database.entity.ProblemState
import java.math.BigInteger
import java.time.LocalDateTime

data class BackpackProblemResponse(
    val id: Long,
    val createdAt: LocalDateTime,
    val state: ProblemState? = null,
    var power: Int? = null,
    var type: String,
    var message: List<Boolean>,
    var lightBackpack: List<BigInteger>,
    var omega: BigInteger? = null,
    var hardBackpack: List<BigInteger>? = null,
    var encodedMessage: BigInteger? = null,
    var decodedMessage: List<Boolean>? = null ,
    var module: BigInteger? = null,
    var reverseOmega: BigInteger? = null,
    var errorDescription: String? = null,
)
