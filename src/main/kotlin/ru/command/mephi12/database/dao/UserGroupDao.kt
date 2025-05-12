package ru.command.mephi12.database.dao

import org.springframework.stereotype.Repository
import ru.command.mephi12.database.entity.UserGroup

@Repository
interface UserGroupDao: AbstractDao<UserGroup> {
    fun findByName(name: String): UserGroup?
}