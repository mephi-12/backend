package ru.command.mephi12

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import ru.command.mephi12.dto.ProblemRequest
import ru.command.mephi12.dto.ProblemType
import ru.command.mephi12.service.problems.MajorDegreesProblemSolverServiceImpl
import ru.command.mephi12.service.problems.MinorDegreesProblemSolverServiceImpl
import ru.command.mephi12.service.problems.SuperIncreasingProblemSolverServiceImpl

@SpringBootApplication
class Mephi12Application

fun main(args: Array<String>) {

    val respose = SuperIncreasingProblemSolverServiceImpl().solve(ProblemRequest(
        type = ProblemType.SUPER_INCREASING,
//        message = arrayListOf(true, false, true, true, false, false)
    ))

    println(respose.let { jacksonObjectMapper().writeValueAsString(respose) })

    runApplication<Mephi12Application>(*args)
}
