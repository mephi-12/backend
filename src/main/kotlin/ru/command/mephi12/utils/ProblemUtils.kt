package ru.command.mephi12.utils

import java.math.BigInteger
import kotlin.math.ceil
import kotlin.random.Random

fun generateRandomMessage(sizeFrom: Int = 8, sizeTo: Int = 16): List<Boolean> {
    val length = getRandomInt(sizeFrom, sizeTo)
    val result = MutableList(length) { Random.nextBoolean() }

    // Проверяем долю нулей - минимум 1/3
    val zerosCount = result.count { !it }
    val requiredZeros = ceil(length / 3.0).toInt()
    if (zerosCount < requiredZeros) {
        val needed = requiredZeros - zerosCount
        var replaced = 0
        for (i in result.indices) {
            if (replaced >= needed) break
            if (result[i]) {
                result[i] = false
                replaced++
            }
        }
    }
    return result
}

fun generateRandomPartOfLightBackpack(lb: List<String>, maxElBeAddedAtStart : Int = 3, maxElBeAddedAtEnd: Int = 3) : List<BigInteger> {
    val toAddAtStart = getRandomInt(0, maxElBeAddedAtStart)
    val toBeAddedAtStart = List(lb.size) { generateRandomNumber(toAddAtStart) }
    val toAddAtEnd = getRandomInt(0, maxElBeAddedAtEnd)
    val toBeAddedAtEnd = List(lb.size) { generateRandomNumber(toAddAtEnd)}.fillUpWithZero()
    return lb
        .fillUpWithZero()
        .mapIndexed { index, value -> BigInteger("${toBeAddedAtStart[index]}$value${toBeAddedAtEnd[index]}") }

}