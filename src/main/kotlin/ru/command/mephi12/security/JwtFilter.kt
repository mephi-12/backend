package ru.command.mephi12.security

import ru.command.mephi12.security.model.Authority
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.command.mephi12.constants.API_ADMIN
import ru.command.mephi12.constants.API_PUBLIC
import ru.command.mephi12.exception.AppException
import ru.command.mephi12.exception.ExceptionResolver
import ru.command.mephi12.utils.containsAnyPath
import ru.command.mephi12.utils.getAuthorities

@Component
class JwtFilter(
    @Lazy
    private val jwtParser: JwtParser,
    private val exceptionResolver: ExceptionResolver,
) : OncePerRequestFilter() {

    companion object {
        val log = LoggerFactory.getLogger(JwtFilter::class.java)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean = request.requestURI.also { log.info("Получен запрос {} {}", request.method, request.requestURI) }.containsAnyPath("/public", "/auth")


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val header = request.getHeader("Authorization")
                ?: throw AppException(HttpStatus.UNAUTHORIZED, "Вы неавторизованы")
            SecurityContextHolder.getContext().authentication = jwtParser.createAuthToken(header)
            if (request.requestURI.containsAnyPath(API_ADMIN)) {
                val authorities = getAuthorities()
                var admin = false
                for (x in authorities) {
                    if (x.authority == Authority.ADMIN.authority.authority) {
                        admin = true
                        break
                    }
                }
                if (!admin) {
                    throw AppException() // TODO
                }
            }
            filterChain.doFilter(request, response)
        } catch (exception: AppException) {
            exceptionResolver.handleException(exception, request, response) // todo подумать как вынести
            return
        }
    }
}
