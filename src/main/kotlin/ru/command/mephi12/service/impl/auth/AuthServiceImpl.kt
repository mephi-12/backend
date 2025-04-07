package ru.command.mephi12.service.impl.auth

import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.Modifying
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.command.mephi12.database.entity.User
import ru.command.mephi12.dto.auth.LoginRequest
import ru.command.mephi12.dto.auth.LoginResponse
import ru.command.mephi12.dto.auth.RegistrationRequest
import ru.command.mephi12.exception.AppException
import ru.command.mephi12.service.AuthService
import ru.command.mephi12.service.UserService


@Service
@Transactional
class AuthServiceImpl(
    private val jwtHelper: JwtHelper,
    private val userService: UserService,
    private val encoder: PasswordEncoder
) : AuthService {

    companion object {
        val log = LoggerFactory.getLogger(AuthServiceImpl::class.java)
    }

    override fun login(request: LoginRequest): LoginResponse {
        log.info("Получен запрос авторизации")
        val user = loginUser(request)
        val jwt = jwtHelper.generateAccessToken(user.id, false)
        return LoginResponse(jwt)
    }

    @Modifying
    override fun registration(request: RegistrationRequest): LoginResponse {
        log.info("Получен запрос на регистрацию")
        val user = userService.createUser(request)
        return LoginResponse(jwtHelper.generateAccessToken(user.id, false))
    }

    private fun loginUser(request: LoginRequest): User {
        val user = userService.findEntityByEmail(request.email)
        if (!encoder.matches(request.password, user.hash)) {
            throw AppException(HttpStatus.UNAUTHORIZED, "Неправильный пароль")
        }
        return user
    }
}