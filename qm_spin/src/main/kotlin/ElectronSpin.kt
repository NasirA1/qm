import mathutil.*
import sun.util.resources.cldr.en.CalendarData_en_NZ
import kotlin.math.sqrt


//eigenvectors
private val u = Ket("u", 1.R, 0.R)
private val d = Ket("d", 0.R, 1.R)
private val r = Ket("r", (1.0 / sqrt(2.0)).R, (1.0 / sqrt(2.0)).R)
private val l = Ket("l", (1.0 / sqrt(2.0)).R, -(1.0 / sqrt(2.0)).R)
private val i = Ket("i", (1.0 / sqrt(2.0)).R, 1.I / (sqrt(2.0)).R)
private val o = Ket("o", (1.0 / sqrt(2.0)).R, (-1).I / (sqrt(2.0)).R)

private val eigenVectors = mapOf(
    (u to 1.00) to "u",
    (d to 1.00) to "d",
    (r to 1.00) to "r",
    (l to 1.00) to "l",
    (i to 1.00) to "i",
    (o to 1.00) to "o",
    (u to -1.00) to "u",
    (d to -1.00) to "d",
    (r to -1.00) to "r",
    (l to -1.00) to "l",
    (i to -1.00) to "i",
    (o to -1.00) to "o",
    (u to Double.NaN) to "u",
    (d to Double.NaN) to "d",
    (r to Double.NaN) to "r",
    (l to Double.NaN) to "l",
    (i to Double.NaN) to "i",
    (o to Double.NaN) to "o"
)


private val states = mapOf(
    "u>" to u,
    "d>" to d,
    "r>" to r,
    "l>" to l,
    "i>" to i,
    "o>" to o
)


private fun experiment(operator: Matrix, state: Ket): Pair<Ket, Double> {
    val resultantVector = operator `|` state
    if(resultantVector == state)
        return state to 1.0
    else {
        val multipliedByEigenValue = resultantVector * -1.0
        if(multipliedByEigenValue == state) {
            return state to -1.0
        } else {
            return resultantVector to Double.NaN
        }
    }
}


private val operators = mapOf(
    "σ(z)" to pauli_1,
    "σ(x)" to pauli_2,
    "σ(y)" to pauli_3
)


private fun doExperiment(expr: String) {
    val parts = expr.split('|')
    val op = operators[parts[0]]
    val state = states[parts[1]]
    val result = experiment(operator = op!!, state = state!!)
    val eigenResult = eigenVectors[result]
    if(eigenResult != null) {
        println("$expr = $eigenResult")
    } else {
        println("$expr = $result")
    }
}


private fun th(angle: Double) = Ket("$angle°", cos(angle.radians) / 2, sin(angle.radians) / 2)


private fun sn(nx: Double, ny: Double, nz: Double) = (nx.R * pauli_2) + (ny.R * pauli_3) + (nz.R * pauli_1)


//TODO NASIR
private fun psi(nx: Double, ny: Double, nz: Double) = Ket("Ψ", sqrt((1.R+nz)/2.R) * )


fun main() {
    println("Quantum Mechanics - Electron Spin")

    u.braket()
    d.braket()
    r.braket()
    l.braket()
    i.braket()
    o.braket()

    //probabilities
    println("<u|u> = ${u.toBra().innerProduct(u).probability()}")
    println("<d|d> = ${d.toBra().innerProduct(d).probability()}")
    println("<u|d> = ${u.toBra().innerProduct(d).probability()}")
    println("<d|u> = ${d.toBra().innerProduct(u).probability()}")
    println("<r|u> = ${r.toBra().innerProduct(u).probability()}")
    println("<r|d> = ${r.toBra().innerProduct(d).probability()}")
    println("<r|r> = ${r.toBra().innerProduct(r).probability()}")
    println("<r|l> = ${r.toBra().innerProduct(l).probability()}")
    println("<l|r> = ${l.toBra().innerProduct(r).probability()}")
    println("<l|l> = ${l.toBra().innerProduct(l).probability()}")
    println("<l|u> = ${l.toBra().innerProduct(u).probability()}")
    println("<l|d> = ${l.toBra().innerProduct(d).probability()}")
    println("<i|i> = ${i.toBra().innerProduct(i).probability()}")
    println("<o|o> = ${o.toBra().innerProduct(o).probability()}")
    println("<o|i> = ${o.toBra().innerProduct(i).probability()}")
    println("<i|o> = ${i.toBra().innerProduct(o).probability()}")
    println("<i|u> = ${i.toBra().innerProduct(u).probability()}")
    println("<o|u> = ${o.toBra().innerProduct(u).probability()}")
    println("<i|d> = ${i.toBra().innerProduct(d).probability()}")
    println("<o|d> = ${o.toBra().innerProduct(d).probability()}")
    println("<i|l> = ${i.toBra().innerProduct(l).probability()}")
    println("<i|r> = ${i.toBra().innerProduct(r).probability()}")
    println("<o|l> = ${o.toBra().innerProduct(l).probability()}")
    println("<o|r> = ${o.toBra().innerProduct(r).probability()}")

    println()
    doExperiment("σ(z)|u>")
    doExperiment("σ(z)|d>")
    doExperiment("σ(x)|r>")
    doExperiment("σ(x)|l>")
    doExperiment("σ(y)|i>")
    doExperiment("σ(y)|o>")
    doExperiment("σ(x)|u>")
    doExperiment("σ(x)|d>")
    println()
    println("Wait till chapter 7 for explanation of the below..")
    doExperiment("σ(y)|u>")
    doExperiment("σ(y)|d>")
    println("\n")

    probabilityOf(prepare = th(45.0), outcome = u)
    probabilityOf(prepare = th(0.0), outcome = th(0.0))

    println()
    println("σn = ")
    sn(90.0, 90.0, 90.0).print(15)
}
