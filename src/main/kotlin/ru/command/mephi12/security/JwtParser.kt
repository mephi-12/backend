package ru.command.mephi12.security

import ru.command.mephi12.security.model.UserPrincipal
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import ru.command.mephi12.exception.AppException
import java.util.*
import javax.crypto.SecretKey
import kotlin.collections.LinkedHashMap

@Component
class JwtParser(
    @Value("\${spring.security.jwt.secret:#{null}}")
    secret: String?,
) {
    val secret: SecretKey by lazy {
        if (secret == null) {
            throw NullPointerException("spring.security.jwt.secret is not provided in configuration")
        }
        Keys.hmacShaKeyFor(secret.toByteArray())
    }
    private val headerPrefix = "Bearer "
    private val algorithm = SignatureAlgorithm.HS256

    fun parseToken(token: String): Jws<Claims> {
        return try {
            Jwts.parserBuilder().setSigningKey(this.secret).build().parseClaimsJws(token)
        } catch (e: ExpiredJwtException) {
            throw AppException(HttpStatus.UNAUTHORIZED, "Авторизуйтесь заново", cause = e)
        } catch (e: JwtException) {
            throw AppException() // TODO
        }
    }

    fun createToken(vararg claims: Pair<String, Any>, expiration: Date? = null): String {
        val token = Jwts.builder()
            .addClaims(claims.associate { it })
            .signWith(secret, algorithm)
        if (expiration != null) {
            token.setExpiration(expiration)
        }
        return token.compact()
    }

    fun parseTokenPrincipalFromHeader(tokenFromHeader: String): UserPrincipal {
        val token = tokenFromHeader.replace(headerPrefix, "")
        val claims = parseToken(token)
        val userId = claims.body.get("userId", Integer::class.java)?.toLong() ?: throw AppException() // TODO
        val rawAuthorities = claims.body.get("authorities", List::class.java)?.toList() ?: throw AppException() // TODO
        val authorities = mutableListOf<GrantedAuthority>()
        for (rawAuthority in rawAuthorities) {
            authorities.add(GrantedAuthority { (rawAuthority as LinkedHashMap<String, String>)["authority"] })
        }
        return UserPrincipal(userId, authorities as List<GrantedAuthority>)
    }

    fun createAuthToken(header: String): UserPrincipal {
        if (!header.startsWith(headerPrefix)) throw AppException() // TODO
        val principal = parseTokenPrincipalFromHeader(header)
        return UserPrincipal(principal.userId, principal.authorities)
    }
}
