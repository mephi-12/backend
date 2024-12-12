package ru.command.mephi12.database.dao

import org.springframework.data.repository.PagingAndSortingRepository
import ru.command.mephi12.database.entity.AbstractEntity

interface AbstractDao<T: AbstractEntity> : PagingAndSortingRepository<T, Long>