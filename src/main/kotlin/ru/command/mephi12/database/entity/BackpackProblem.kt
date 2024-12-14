package ru.command.mephi12.database.entity

import jakarta.persistence.*
import ru.command.mephi12.dto.BackpackProblemType
import ru.command.mephi12.utils.BigIntegerListConverter
import ru.command.mephi12.utils.BooleanListConverter
import java.math.BigInteger

@Entity
@Table(
    name = "backpack_problem",
)
class BackpackProblem(
    @Column
    var power: Int?,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var type: BackpackProblemType,
    @Column(nullable = false)
    @Convert(converter = BooleanListConverter::class)
    var message: List<Boolean>,
    @Column(name = "light_backpack", nullable = false)
    @Convert(converter = BigIntegerListConverter::class)
    var lightBackpack: List<BigInteger>,
    @Column
    var omega: BigInteger?,
    @Column(name = "hard_backpack")
    @Convert(converter = BigIntegerListConverter::class)
    var hardBackpack: List<BigInteger>?,
    @Column(name = "encoded_message")
    var encodedMessage: BigInteger?,
    @Column(name = "decoded_message")
    @Convert(converter = BooleanListConverter::class)
    var decodedMessage: List<Boolean>?,
    @Column
    var module: BigInteger?,
    @Column(name = "reverse_omega")
    var reverseOmega: BigInteger?,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var state: ProblemState = ProblemState.NEW,
    @Column
    var errorDescription : String?,
): AbstractEntity()