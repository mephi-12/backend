package ru.command.mephi12.constants

import ru.command.mephi12.dto.BackpackProblemTypeQualifier

enum class ProblemType(val description: String) {
    BACKPACK_CODE_SUPER_INCREASING(BackpackProblemTypeQualifier.CODE_SUPER_INCREASING),
    BACKPACK_CODE_DEGREES(BackpackProblemTypeQualifier.CODE_DEGREES),
    BACKPACK_BACKDOOR("Задача о бэкдоре в Ранцевой КС"),
    EL_GAMAL("Задача о КС Эль-Гамаля")
}