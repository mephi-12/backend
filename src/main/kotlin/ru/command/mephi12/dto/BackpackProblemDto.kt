package ru.command.mephi12.dto

import java.math.BigInteger

class BackpackProblemDto(
    var power: Int? = null,
    var type: BackpackProblemType,
    var message: List<Boolean>,
    var lightBackpack: List<BigInteger>,
    var omega: BigInteger,
    var hardBackpack: List<BigInteger>,
    var encodedMessage: BigInteger,
    var module: BigInteger,
    var reverseOmega: BigInteger,
)