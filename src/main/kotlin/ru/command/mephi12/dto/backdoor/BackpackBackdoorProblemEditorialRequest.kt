package ru.command.mephi12.dto.backdoor

import ru.command.mephi12.constants.ProblemType
import java.math.BigInteger

class BackpackBackdoorProblemEditorialRequest(
    var power: Int? = null,
    var message: List<Boolean>
)