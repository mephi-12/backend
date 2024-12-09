package ru.command.mephi12.dto


object BackpackProblemTypeQualifier {
    const val CODE_SUPER_INCREASING = "Супервозрастающая"
    const val CODE_DEGREES = "Кодирование степеней"
}
enum class BackpackProblemType(val text: String) {
    CODE_SUPER_INCREASING(BackpackProblemTypeQualifier.CODE_SUPER_INCREASING),
    CODE_DEGREES(BackpackProblemTypeQualifier.CODE_DEGREES),
}