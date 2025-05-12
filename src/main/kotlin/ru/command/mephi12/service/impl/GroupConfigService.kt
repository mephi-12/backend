package ru.command.mephi12.service.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.command.mephi12.database.dao.UserGroupDao
import ru.command.mephi12.database.entity.ProblemConfiguration
import ru.command.mephi12.dto.ProblemConfigurationDto
import java.util.*

@Service
class GroupConfigService(
    private val dao: UserGroupDao,
) {
    @Transactional(readOnly = true)
    fun getConfigs(groupId: UUID): List<ProblemConfigurationDto> =
        dao.findById(groupId)
            .orElseThrow()
            .problemConfigs
            .map {
                val pc = it.problemConfiguration
                ProblemConfigurationDto(
                    pc.id,
                    pc.name,
                    pc.taskServices,
                    it.enabled,
                )
            }

    @Transactional(readOnly = true)
    fun enabledConfigs(groupId: UUID): List<ProblemConfiguration> =
        dao.findById(groupId)
            .orElseThrow()
            .problemConfigs
            .filter { it.enabled }
            .map { it.problemConfiguration }
}