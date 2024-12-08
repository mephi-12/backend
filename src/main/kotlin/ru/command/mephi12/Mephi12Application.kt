package ru.command.mephi12

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

    val respose = MinorDegreesProblemSolverServiceImpl().solve(ProblemRequest(
        type = ProblemType.MINOR_DEGREES,
        message = arrayListOf(true, false, true, true, false, false)
    ))

    println(respose)

    runApplication<Mephi12Application>(*args)
}
