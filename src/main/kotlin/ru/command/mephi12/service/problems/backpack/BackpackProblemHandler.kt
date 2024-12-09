package ru.command.mephi12.service.problems.backpack

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import ru.command.mephi12.dto.BackpackProblemRequest
import ru.command.mephi12.dto.BackpackProblemDto
import ru.command.mephi12.dto.BackpackProblemType
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
    override fun solve(request: BackpackProblemRequest): BackpackProblemDto {
        return when (request.type) {
            BackpackProblemType.CODE_SUPER_INCREASING -> superIncreasingSolver.solve(request)
            BackpackProblemType.CODE_DEGREES -> codeDegreesSolver.solve(request)
        }
    }
}
