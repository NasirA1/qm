import mathutil.*

private val x = Ket("x", 1.R, 0.R)
private val y = Ket("y", 0.R, 1.R)
private val f = Ket("/", 1.0/sqrt(2), 1.0/sqrt(2))
private val b = Ket("\\", 1.0/sqrt(2), -1.0/sqrt(2))

private val eigenVectors = mapOf(
    (x to 1.00) to "x",
    (y to 1.00) to "y",
    (f to 1.00) to "/",
    (b to 1.00) to "\\",
    (x to -1.00) to "x",
    (y to -1.00) to "y",
    (f to -1.00) to "/",
    (b to -1.00) to "\\",
    (x to Double.NaN) to "x",
    (y to Double.NaN) to "y",
    (f to Double.NaN) to "/",
    (b to Double.NaN) to "\\"
)

private val states = mapOf(
    "x>" to x,
    "y>" to y,
    "/>" to f,
    "\\>" to b
)

private val operators = mapOf(
    "H+" to pauli_1,
    "Hx" to pauli_2,
    "H*" to pauli_3
)

private fun printPrimaryOperators() {
    println("Primary Operators:")
    operators.forEach {
        println(it.key + " =")
        it.value.print(4) { c ->
            if (c.im == 0.0) {
                c.re.toInt().toString()
            } else {
                c.toString()
            }
        }
        println()
    }
}


//Generic Eigenvector and H operator generator functions
private val cw = Ket("↻", 1.0/sqrt(2.0), I/sqrt(2.0))
private val ac = Ket("↺", 1.0/sqrt(2.0), -I/sqrt(2.0))

fun th(angle: Double) = Ket("$angle°", cos(angle.radians), sin(angle.radians))

fun H(angle: Double) = Matrix(2, 2,
    cos(2 * angle.radians), sin(2 * angle.radians),
    sin(2 * angle.radians), -cos(2 * angle.radians)
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

fun main() {
    println("Quantum Mechanics - Light Polarisation")

    x.braket()
    y.braket()
    f.braket()
    b.braket()

    printPrimaryOperators()

    probabilityOf(prepare = x, outcome = x)
    probabilityOf(prepare = x, outcome = y)
    probabilityOf(prepare = x, outcome = f)
    probabilityOf(prepare = x, outcome = b)
    probabilityOf(prepare = f, outcome = b)
    probabilityOf(prepare = f, outcome = x)
    probabilityOf(prepare = b, outcome = y)

    doExperiment("H+|x>")
    doExperiment("H+|y>")
    doExperiment("Hx|/>")
    doExperiment("Hx|\\>")

    probabilityOf(prepare = th(45.0), outcome = th(45.0))
    probabilityOf(prepare = th(135.0), outcome = th(45.0))
    probabilityOf(prepare = x, outcome = th(45.0))
    probabilityOf(prepare = y, outcome = th(135.0))
    probabilityOf(prepare = th(60.0), outcome = th(30.0))
    probabilityOf(prepare = th(0.0), outcome = th(90.0))
    probabilityOf(prepare = th(275.0), outcome = th(180.0))
    println(H(90.0) * th(45.0))

    println()
    println("Circular Polarisation:")
    probabilityOf(prepare = ac, outcome = cw)
    probabilityOf(prepare = cw, outcome = ac)
    probabilityOf(prepare = cw, outcome = cw)
    probabilityOf(prepare = ac, outcome = ac)
    probabilityOf(prepare = f, outcome = cw)
    println()
    probabilityOf(prepare = cw, outcome = th(180.0))
    println()

    println("Applying H* Operator: ")
    println("H*|${cw.label}> = " + (operators["H*"]!! * cw).toBra())
    println("H*|${ac.label}> =" + (operators["H*"]!! * ac).toBra())
}
