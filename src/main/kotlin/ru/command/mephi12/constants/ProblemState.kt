package ru.command.mephi12.constants

enum class ProblemState(val value: String) {
    NEW("Задание решается"),
    SOLVED("Задание верно решено"),
    FAILED("Задание решено неверно"),
}