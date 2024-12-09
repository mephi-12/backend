package ru.command.mephi12.utils

fun List<String>.fillUpWithZero() : List<String> {
    val maxLength = this.sumOf { it.toBigInteger() }.toString().length
    return this.map { "${"0".repeat(maxLength - it.length)}$it"}
}