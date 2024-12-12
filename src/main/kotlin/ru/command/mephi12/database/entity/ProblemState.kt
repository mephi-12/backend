package ru.command.mephi12.database.entity

enum class ProblemState(val value: String) {
    NEW("Задание решается"),
    SOLVED("Задание верно решено"),
    FAILED("Задание решено неверно"),
}