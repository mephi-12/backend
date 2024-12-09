package ru.command.mephi12.utils

import java.math.BigInteger

fun List<BigInteger>.sum() : BigInteger {
    var res = BigInteger.ZERO
    for(el in this) {
        res += el
    }
    return res
}