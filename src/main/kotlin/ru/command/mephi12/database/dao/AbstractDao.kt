package ru.command.mephi12.database.dao

import org.springframework.data.repository.CrudRepository
import ru.command.mephi12.database.entity.AbstractEntity
import java.util.UUID

interface AbstractDao<T: AbstractEntity> : CrudRepository<T, UUID>