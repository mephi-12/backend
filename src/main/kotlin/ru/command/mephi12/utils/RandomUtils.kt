package ru.command.mephi12.utils

import kotlin.random.Random

// inclusively
fun getRandomInt(from: Int, to: Int) = Random.nextInt(from, to + 1)

fun generateRandomNumber(size: Int) : String =
    (0 until size)
        .map { ('0' until '9').random() }
        .joinToString(separator = "")
