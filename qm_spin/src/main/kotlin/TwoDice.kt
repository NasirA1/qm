import mathutil.*
import java.lang.Math.pow
import java.text.FieldPosition
import kotlin.math.*

val diceValues = (2..12).toList()
val relativeProbabilities = listOf(1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1)
val normalisationDenominator = sqrt(relativeProbabilities.sum().toDouble())
val normalisedProbabilityAmplitudes = relativeProbabilities.map { sqrt(it.toDouble()) / normalisationDenominator }
val psi = Ket("Ψ", *normalisedProbabilityAmplitudes.map { it.R }.toTypedArray())
val R = Matrix(11, 11, *makeRElements())

fun makeRElements() = run {
    val xs = MutableList(psi.size * psi.size) { 0.R }
    var pos = 0
    for(i in 0 until psi.size) {
        for (j in 0 until psi.size) {
            if(i == j) {
                xs[pos] = (i + 2).R
            }
            pos += 1
        }
    }
    xs.toTypedArray()
}


val diceEigenKets = (0 until psi.size).map {
    fun createDiceEigenKet(label: String, oneValuePosition: Int)
            = Ket(label, *Array(psi.size) { if(it == oneValuePosition) 1.R else 0.R })

    diceValues[it].toString() to createDiceEigenKet(diceValues[it].toString(), it)
}.toMap()



fun main() {

    println("_v_|RP_|__Ψ___|__P%__")
    for(i in diceValues.indices) {
        println(diceValues[i].toString().padEnd(2) + " | " + relativeProbabilities[i] + " | "
                + normalisedProbabilityAmplitudes[i] .round(2)+ " | "
                + ((100.00 * pow(normalisedProbabilityAmplitudes[i], 2.0)).roundToInt().toString() + "%").padStart(3)
        )
    }
    println()
    println("<Ψ| = (${psi.toBra()})")
    println("<Ψ|Ψ> = " + psi.toBra().innerProduct(psi))
    println()


    println("Eigen vectors:")
    diceEigenKets.forEach {
        println("<${it.key}|".padStart(4) + " = " + it.value.toBra())
    }
    println()
    println("<2|2> = " + diceEigenKets["2"]?.toBra()?.innerProduct(diceEigenKets["2"]!!))
    println("<2|6> = " + diceEigenKets["2"]?.toBra()?.innerProduct(diceEigenKets["6"]!!))
    println()

    println("R:")
    R.print(formatFn = { c -> c.re.toInt().toString() })
    println()
    println("Expectation Value <Ψ|R|Ψ> = " + (R * psi).toBra().innerProduct(psi))
    println();
}
