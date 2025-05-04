package ru.command.mephi12.database.entity

import jakarta.persistence.*
import ru.command.mephi12.constants.ProblemState
import ru.command.mephi12.constants.ProblemType
import ru.command.mephi12.dto.modern_problem.ProblemDto

@Entity
@Table(name = "problem")
class Problem(
    @Column(nullable = false)
    @Lob
    val statement: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var type: ProblemType,
    @Column
    @Enumerated(EnumType.STRING)
    var state: ProblemState = ProblemState.NEW,
) : AbstractEntity() {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "problem_session")
    lateinit var problemSession: ProblemSession

    fun toDto() : ProblemDto =
        ProblemDto(
            id = id,
            statement = statement,
            type = type,
            state = state,
            sessionId = problemSession.id,
        )
}