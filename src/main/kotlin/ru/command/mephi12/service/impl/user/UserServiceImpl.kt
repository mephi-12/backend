package ru.command.mephi12.service.impl.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.command.mephi12.constants.ProblemState
import ru.command.mephi12.database.dao.UserDao
import ru.command.mephi12.database.entity.ProblemSession
import ru.command.mephi12.database.entity.User
import ru.command.mephi12.dto.auth.RegistrationRequest
import ru.command.mephi12.dto.mapper.UserMapper
import ru.command.mephi12.exception.AppException
import ru.command.mephi12.exception.ResourceNotFoundException
import ru.command.mephi12.service.UserService
import ru.command.mephi12.utils.getPrincipal
import java.util.*


@Service
class UserServiceImpl(
    private val passwordEncoder: PasswordEncoder,
    private val dao: UserDao,
    private val mapper: UserMapper,
) : UserService {

    override fun existByEmail(email: String): Boolean {
        return dao.existsByEmail(email)
    }

    override fun findEntityByEmail(email: String): User {
        return dao.findByEmail(email).orElseThrow { ResourceNotFoundException(email) }
    }

    override fun findEntityById(id: UUID): User {
        return dao.findById(id).orElseThrow { ResourceNotFoundException(id) }
    }

    override fun getSelfProfile(userId: UUID) {
        val user = findEntityById(userId)
//        return mapper.asResponse(user)
    }

    override fun getUserProfile(userId: UUID) {
        val user = findEntityById(userId)
//        return mapper.asResponse(user)
    }

    override fun getCurrentProblemSession(): ProblemSession? {
        val user = findEntityById(getPrincipal())
        return user.problemSessions.firstOrNull { it.sessionState == ProblemState.NEW }
    }

    override fun createUser(request: RegistrationRequest): User {
        if (dao.existsByEmail(request.email)) {
            throw AppException(request.email) // TODO
        }
        val user = dao.save(
            mapper.asEntity(request).apply { // todo пароль в маппере
                hash = passwordEncoder.encode(request.password)
            }
        )
        return user
    }

}