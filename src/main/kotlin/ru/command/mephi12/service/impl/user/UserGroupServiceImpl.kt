package ru.command.mephi12.service.impl.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.command.mephi12.database.dao.UserGroupDao
import ru.command.mephi12.database.entity.UserGroup
import ru.command.mephi12.dto.UserGroupDto
import ru.command.mephi12.exception.ResourceNotFoundException
import ru.command.mephi12.service.UserGroupService

@Service
class UserGroupServiceImpl(
    private val dao: UserGroupDao
) : UserGroupService {
    override fun findByName(name: String): UserGroup =
        dao.findByName(name) ?: throw ResourceNotFoundException("группа с названием $name")

    @Transactional
    override fun save(userGroup: UserGroup) =
        dao.save(userGroup)

    @Transactional(readOnly = true)
    override fun findAll() = dao.findAll().map {
        UserGroupDto(
            it.id,
            it.name,
            it.users.map {user -> user.id},
        )
    }
}