package ru.command.mephi12.dto


object ProblemTypeQualifier {
    const val SUPER_INCREASING = "Супервозрастающая"
    const val MINOR_DEGREES = "Кодирование младших степеней"
    const val MAJOR_DEGREES = "Кодирование старших степеней"
}
enum class ProblemType(val text: String) {
    SUPER_INCREASING(ProblemTypeQualifier.SUPER_INCREASING),
    MINOR_DEGREES(ProblemTypeQualifier.MINOR_DEGREES),
    MAJOR_DEGREES(ProblemTypeQualifier.MAJOR_DEGREES)
}