package ru.command.mephi12.dto.backdoor

import ru.command.mephi12.constants.ProblemType
import java.math.BigInteger

class BackpackBackdoorProblemEditorialResponse(
    var power: Int? = null,
    var spaceSize: Long,
    var message: List<Boolean>,
    var lightBackpack: List<BigInteger>,
    var omega: String,
    var hardBackpack: List<BigInteger>,
    var encodedMessage: BigInteger,
    var module: String,
    var reverseOmega: BigInteger,
)