package ru.command.mephi12.service.impl.auth

import ru.command.mephi12.security.JwtParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.command.mephi12.security.model.Authority
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


@Service
class JwtHelper(
    private val jwt: JwtParser,

    @Value("\${spring.security.jwt.access.lifetime}")
    private val accessTokenLifeTime: Long,
) {

    fun generateAccessToken(userId: UUID, admin: Boolean): String {
        val authorities = mutableListOf(Authority.USER.authority)
        if (admin) {
            authorities.add(Authority.ADMIN.authority)
        }
        return jwt.createToken(
            "userId" to userId,
            "permissions" to emptyList<String>(),
            "authorities" to authorities,
            expiration = getAccessTokenExpiration(),
        )
    }

    private fun getAccessTokenExpiration(): Date =
        Instant.now().plus(accessTokenLifeTime, ChronoUnit.DAYS).let { Date.from(it) }
}
