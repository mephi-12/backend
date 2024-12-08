package ru.command.mephi12.service.problems

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import ru.command.mephi12.dto.ProblemRequest
import ru.command.mephi12.dto.ProblemResponse
import ru.command.mephi12.dto.ProblemType
import ru.command.mephi12.dto.ProblemTypeQualifier
import ru.command.mephi12.service.ProblemSolverService

@Component
@Primary
class ProblemHandler(
    @Qualifier(ProblemTypeQualifier.SUPER_INCREASING)
    private val superIncreasingSolver: ProblemSolverService,

    @Qualifier(ProblemTypeQualifier.MINOR_DEGREES)
    private val minorDegreesSolver: ProblemSolverService,

    @Qualifier(ProblemTypeQualifier.MAJOR_DEGREES)
    private val majorDegreesSolver: ProblemSolverService
) : ProblemSolverService {
    override fun solve(request: ProblemRequest): ProblemResponse {
        return when (request.type) {
            ProblemType.SUPER_INCREASING -> superIncreasingSolver.solve(request)
            ProblemType.MINOR_DEGREES -> minorDegreesSolver.solve(request)
            ProblemType.MAJOR_DEGREES -> majorDegreesSolver.solve(request)
        }
    }
}
