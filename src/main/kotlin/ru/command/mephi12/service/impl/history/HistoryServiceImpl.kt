package ru.command.mephi12.service.impl.history

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import ru.command.mephi12.database.dao.BackpackProblemDao
import ru.command.mephi12.dto.BackpackProblemResponse
import ru.command.mephi12.dto.mapper.BackpackProblemMapper
import ru.command.mephi12.service.HistoryService

@Service
class HistoryServiceImpl(
    private val backpackProblemDao: BackpackProblemDao,
    private val mapper: BackpackProblemMapper,
) : HistoryService {
    override fun findAll(): List<BackpackProblemResponse> = backpackProblemDao.findAll(Sort.unsorted()).map { mapper.entityToResponse(it) }
}