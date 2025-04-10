package ru.command.mephi12.service

import ru.command.mephi12.dto.BackpackProblemResponse

// LEGACY
interface HistoryService {
    fun findAll() : List<BackpackProblemResponse>
}