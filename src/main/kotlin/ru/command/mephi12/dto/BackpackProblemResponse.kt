package ru.command.mephi12.dto

import java.math.BigInteger
import java.time.LocalDateTime
import java.util.UUID

data class BackpackProblemResponse(
    val id: UUID,
    val createdAt: LocalDateTime,
    val state: String? = null,
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
) : AbstractProblemResponse(false) // todo
