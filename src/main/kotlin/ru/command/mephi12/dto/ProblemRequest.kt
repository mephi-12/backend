package ru.command.mephi12.dto

class ProblemRequest(
    var power: Int? = null,
    var type: ProblemType,
    var message: List<Boolean> = mutableListOf(),
    var lightBackpack: List<Long> = mutableListOf(),
    var omega: Long? = null
)