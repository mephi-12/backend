package ru.command.mephi12.service

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import ru.command.mephi12.database.dao.ProblemSessionDao
import ru.command.mephi12.database.dao.UserDao
import ru.command.mephi12.database.entity.User
import ru.command.mephi12.service.impl.problems.ProblemDecorator
import ru.command.mephi12.utils.getPrincipal

// не работает
@SpringBootTest
class ProblemDecoratorTest {

    @Autowired
    lateinit var decorator : ProblemDecorator

    @Autowired
    lateinit var userDao: UserDao

    @Autowired
    lateinit var problemSessionDao: ProblemSessionDao

    @BeforeEach
    fun setUp() {
        val user = User(
            "test@test.com",
            "Тестик",
        ).apply {
            this.hash = "asfasd"
        }

        val id = userDao.save(user).id

        Thread.sleep(1000)

        println("ID: $id")

        val auth: Authentication =
            UsernamePasswordAuthenticationToken(id, null, emptyList())

        SecurityContextHolder.setContext(SecurityContextHolder.getContext().apply { authentication = auth })
    }

    @AfterEach
    fun tearDown() {
        userDao.deleteById(getPrincipal())
        SecurityContextHolder.clearContext()
    }

    @Test
    @Transactional
    fun testGeneration() {
        val session = decorator.createSolvingSession("Sem4")
        val problemSession = problemSessionDao.findById(session.id).orElseThrow { throw Exception() }
        println(session)
    }
}