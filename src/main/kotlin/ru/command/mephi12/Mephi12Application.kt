package ru.command.mephi12

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import ru.command.mephi12.dto.BackpackProblemRequest
import ru.command.mephi12.dto.BackpackProblemType
import ru.command.mephi12.service.problems.backpack.BackpackDegreesProblemSolverServiceImpl
import ru.command.mephi12.service.problems.backpack.BackpackProblemHandler
import ru.command.mephi12.service.problems.backpack.BackpackSuperIncreasingProblemSolverServiceImpl
import java.math.BigInteger

@SpringBootApplication
class Mephi12Application

fun main(args: Array<String>) {

    val response = BackpackProblemHandler(
        BackpackSuperIncreasingProblemSolverServiceImpl(),
        BackpackDegreesProblemSolverServiceImpl(),
    ).solve(BackpackProblemRequest(
        type = BackpackProblemType.CODE_SUPER_INCREASING,
//        message = arrayListOf(true, false, true, true, false, false)
    ))

    println(response.let { jacksonObjectMapper().writeValueAsString(response) })

    runApplication<Mephi12Application>(*args)
}
