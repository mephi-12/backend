package ru.command.mephi12.constants

import ru.command.mephi12.dto.BackpackProblemTypeQualifier

enum class ProblemType(val description: String) {
    BACKPACK_CODE_SUPER_INCREASING(BackpackProblemTypeQualifier.CODE_SUPER_INCREASING),
    BACKPACK_CODE_DEGREES(BackpackProblemTypeQualifier.CODE_DEGREES),
    BACKPACK("Задача о Ранцевой КС"),
    BACKPACK_BACKDOOR("Задача о бэкдоре в Ранцевой КС"),
    EL_GAMAL("Задача о КС Эль-Гамаля");

    companion object {
        fun getByQualifier(qualifier: String): ProblemType =
            when (qualifier) {
                EL_GAMAL_QUALIFIER -> EL_GAMAL
                BACKPACK_QUALIFIER -> BACKPACK
                else -> throw IllegalArgumentException("Unknown problem qualifier")
            }
    }

    fun getQualifier(): String = when(this) {
        EL_GAMAL -> EL_GAMAL_QUALIFIER
        BACKPACK -> BACKPACK_QUALIFIER
        else -> throw IllegalArgumentException("Unknown problem qualifier")
    }
}