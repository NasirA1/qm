package mathutil

import java.math.BigDecimal
import java.math.RoundingMode


//Pauli Hermitian Matrices
val pauli_1 = Matrix(2, 2, 1.R, 0.R, 0.R, (-1).R)
val pauli_2 = Matrix(2, 2, 0.R, 1.R, 1.R, 0.R)
val pauli_3 = Matrix(2, 2,  0.R, -I, I, 0.R)

fun probabilityOf(prepare: Ket, outcome: Ket) {
    println("[<${outcome.label}|${prepare.label}>]² = ${outcome.toBra().innerProduct(prepare).probability()}")
}

val Double.radians: Double get() = this * Math.PI / 180.0
val Double.degrees: Double get() = this / Math.PI / 180.0


fun Double.round(decimals: Int): Double = BigDecimal(this).setScale(decimals, RoundingMode.HALF_EVEN).toDouble()

fun Double.probability() = (this * this).round(2)

fun List<Complex>.sum(): Complex {
    var result = complex(0, 0)
    forEach { result += it }
    return result
}

fun Array<Complex>.sum(): Complex = toList().sum()
