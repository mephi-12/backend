package ru.command.mephi12.dto

class ProblemResponse(
    var power: Int? = null,
    var type: ProblemType,
    var message: List<Boolean>,
    var lightBackpack: List<Long>,
    var omega: Long,
    var hardBackpack: List<Long>,
    var encodedMessage: Long,
    var module: String,
    var reverseOmega: Long,
)