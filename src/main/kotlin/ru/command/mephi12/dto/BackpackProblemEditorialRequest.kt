package ru.command.mephi12.dto

import java.math.BigInteger

class BackpackProblemEditorialRequest(
    var power: Int? = null,
    var type: ProblemType,
    var message: List<Boolean> = mutableListOf(),
    var lightBackpack: List<BigInteger> = mutableListOf(),
    var omega: BigInteger? = null
)