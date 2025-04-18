package ru.command.mephi12.service.impl.problems.backpack

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import ru.command.mephi12.constants.ProblemType
import ru.command.mephi12.dto.BackpackProblemEditorialRequest
import ru.command.mephi12.dto.BackpackProblemEditorialResponse
import ru.command.mephi12.dto.BackpackProblemTypeQualifier
import ru.command.mephi12.service.BackpackProblemSolverService

@Component
@Primary
class BackpackProblemHandler(
    @Qualifier(BackpackProblemTypeQualifier.CODE_SUPER_INCREASING)
    private val superIncreasingSolver: BackpackProblemSolverService,

    @Qualifier(BackpackProblemTypeQualifier.CODE_DEGREES)
    private val codeDegreesSolver: BackpackProblemSolverService,
) : BackpackProblemSolverService {
    override fun solve(request: BackpackProblemEditorialRequest): BackpackProblemEditorialResponse {
        return when (request.type) {
            ProblemType.BACKPACK_CODE_DEGREES -> superIncreasingSolver.solve(request)
            ProblemType.BACKPACK_CODE_DEGREES -> codeDegreesSolver.solve(request)
            else -> codeDegreesSolver.solve(request)
        }
    }
}
