package ru.command.mephi12.database.dao

import org.springframework.stereotype.Repository
import ru.command.mephi12.database.entity.User
import java.util.*

@Repository
interface UserDao : AbstractDao<User> {
    fun findByEmail(email: String): Optional<User>

    fun existsByEmail(email: String): Boolean
}