package ru.command.mephi12.utils.mathLibrary

import java.math.BigInteger

/**
 * Модель для задания по криптосистеме Эль-Гамаля
 */
data class ElGamalTask(
    val p: BigInteger,          // Простое число
    val g: BigInteger,          // Генератор мультипликативной группы
    val x: BigInteger,          // Секретный ключ
    val m: BigInteger           // Исходное сообщение
)

/**
 * Модель для ответа на задачу по криптосистеме Эль-Гамаля
 */
data class ElGamalSolution(
    val y: BigInteger,          // Открытый ключ
    val k: BigInteger,          // Эфемерный ключ для шифрования
    val c1: BigInteger,         // Первая часть шифротекста
    val c2: BigInteger,         // Вторая часть шифротекста
    val decryptedMessage: BigInteger  // Расшифрованное сообщение
)

/**
 * Модель для демонстрационного примера криптосистемы Эль-Гамаля
 * с промежуточными вычислениями
 */
data class ElGamalDemo(
    // Параметры задачи
    val p: BigInteger,          // Простое число
    val g: BigInteger,          // Генератор
    val x: BigInteger,          // Секретный ключ
    val m: BigInteger,          // Сообщение

    // Вычисления для открытого ключа
    val y: BigInteger,          // Открытый ключ
    val yCalculation: String,   // Пояснение вычисления y

    // Параметры шифрования
    val k: BigInteger,          // Эфемерный ключ
    val c1: BigInteger,         // Первая часть шифротекста
    val c1Calculation: String,  // Пояснение вычисления c1
    val yk: BigInteger,         // Промежуточное значение y^k
    val c2: BigInteger,         // Вторая часть шифротекста
    val c2Calculation: String,  // Пояснение вычисления c2

    // Параметры расшифровки
    val s: BigInteger,          // Общий секрет (c1^x)
    val sInverse: BigInteger,   // Обратное к s по модулю p
    val decryptedMessage: BigInteger, // Расшифрованное сообщение
    val decryptionCalculation: String // Пояснение расшифровки
)

/**
 * Результат проверки решения
 */
data class ElGamalVerificationResult(
    val isCorrect: Boolean,
    val errorMessage: String? = null
)