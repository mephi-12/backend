package ru.command.mephi12.dto

import ru.command.mephi12.database.entity.ProblemState
import java.math.BigInteger

data class EditorialProblemSolutionRequest(
    var power: Int,
    var type: BackpackProblemType,
    var message: List<Boolean>,
    var lightBackpack: List<BigInteger>,
    var omega: BigInteger,
    var hardBackpack: List<BigInteger>,
    var encodedMessage: BigInteger,
    var decodedMessage: List<Boolean>,
    var module: BigInteger,
    var reverseOmega: BigInteger,
)
