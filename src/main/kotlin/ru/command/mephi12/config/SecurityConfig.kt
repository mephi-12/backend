package ru.command.mephi12.config

import ru.command.mephi12.security.JwtFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfigurationSource
import ru.command.mephi12.constants.API_PUBLIC
import ru.command.mephi12.constants.API_VERSION_1

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val jwtFilter: JwtFilter,
    private val corsConfig: CorsConfigurationSource,
) {

    @Bean
    fun getEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun configure(http: HttpSecurity): SecurityFilterChain {
        http.cors { it.configurationSource(corsConfig) }
        http.csrf { csrf -> csrf.disable() }

        http.authorizeHttpRequests { requests ->
            requests.requestMatchers("/public/**").permitAll()
            requests.requestMatchers("/auth/**").permitAll()
            requests.requestMatchers("/**").hasAnyRole("USER")
        }

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        http.sessionManagement { sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
        http.authenticationManager(authManager(http))

        return http.build()
    }

    @Bean
    fun authManager(http: HttpSecurity): AuthenticationManager? {
        val builder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        builder.inMemoryAuthentication()
        return builder.build()
    }
}