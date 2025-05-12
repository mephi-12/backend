package ru.command.mephi12.service

import ru.command.mephi12.database.entity.UserGroup
import ru.command.mephi12.dto.UserGroupDto

interface UserGroupService {
    fun findByName(name: String): UserGroup
    fun save(userGroup: UserGroup): UserGroup
    fun findAll(): List<UserGroupDto>
}