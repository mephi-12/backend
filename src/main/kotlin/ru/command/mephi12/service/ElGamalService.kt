package ru.command.mephi12.service

import ru.command.mephi12.utils.mathLibrary.*
import java.math.BigInteger

interface ElGamalService {
    /**
     * Создает демонстрационный пример работы криптосистемы Эль-Гамаля
     */
    fun generateDemo(bitLength: Int = 64, message: BigInteger? = null): ElGamalDemo
    
    /**
     * Создает задание для пользователя
     */
    fun generateTask(bitLength: Int = 32): ElGamalTask
    
    /**
     * Проверяет решение задания пользователя
     */
    fun checkSolution(task: ElGamalTask, solution: ElGamalSolution): ElGamalVerificationResult
    
    /**
     * Шифрует сообщение с использованием криптосистемы Эль-Гамаля
     */
    fun encrypt(message: BigInteger, p: BigInteger, g: BigInteger, y: BigInteger, k: BigInteger? = null): ElGamalUtils.Ciphertext
    
    /**
     * Расшифровывает сообщение с использованием криптосистемы Эль-Гамаля
     */
    fun decrypt(ciphertext: ElGamalUtils.Ciphertext, p: BigInteger, x: BigInteger): BigInteger
}