package ru.command.mephi12.dto

import java.math.BigInteger

class BackpackProblemRequest(
    var power: Int? = null,
    var type: BackpackProblemType,
    var message: List<Boolean> = mutableListOf(),
    var lightBackpack: List<BigInteger> = mutableListOf(),
    var omega: BigInteger? = null
)