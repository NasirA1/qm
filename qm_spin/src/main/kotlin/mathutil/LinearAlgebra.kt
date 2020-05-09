package mathutil

import java.lang.StringBuilder
import java.math.BigDecimal
import java.math.RoundingMode
import java.security.InvalidParameterException

fun Double.round(decimals: Int): Double = BigDecimal(this).setScale(decimals, RoundingMode.HALF_EVEN).toDouble()

fun Double.probability() = (this * this).round(2)

fun List<Complex>.sum(): Complex {
    var result = complex(0, 0)
    forEach { result += it }
    return result
}

fun Array<Complex>.sum(): Complex = toList().sum()



open class AbstractVector(val values: Array<Complex>) {

    constructor(reals: Array<Double>): this(Array<Complex>(reals.size) { real ->
        complex(
            real,
            0
        )
    })

    val size: Int get() = values.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Ket
        if (!values.contentDeepEquals(other.values)) return false
        return true
    }

    override fun hashCode(): Int {
        return values.contentDeepHashCode()
    }

    fun conj(): Array<Complex> = values.map { it.conj() }.toTypedArray()

    protected fun dotProduct(other: AbstractVector): Double =
        values.mapIndexed { j, z -> z * other.values[j] }.sum().re
}


class Ket(val label: String, vararg values: Complex) : AbstractVector(arrayOf(*values)) {

    override fun toString(): String {
        val sb = StringBuilder()
        values.forEach { sb.appendln("$it") }
        return sb.toString()
    }

    fun toString(spaces: Int, excludeFirst: Boolean = true): String {
        val sb = StringBuilder()
        values.forEachIndexed { i, z ->
            if(excludeFirst && i == 0)
                sb.appendln(" $z")
            else
                sb.appendln(" ".repeat(spaces) + "$z")
        }
        return sb.toString()
    }

    operator fun times(scaler: Double): Ket {
        val r = values.map { scaler * it }.toTypedArray()
        return Ket(label, *r)
    }

    fun toBra(): Bra = Bra(label, *conj())

    fun innerProduct(bra: Bra): Double = super.dotProduct(bra)

    fun braket() {
        println("<${label}|=${toBra()}")
        println("|${label}>=${toString(5)}")
    }
}

class Bra(val label: String, vararg values: Complex) : AbstractVector(arrayOf(*values)) {

    override fun toString(): String {
        val sb = StringBuilder()
        values.forEach { sb.append(" $it ") }
        return sb.toString()
    }

    fun toKet(): Ket = Ket(label, *conj())

    fun innerProduct(ket: Ket): Double = super.dotProduct(ket)
}


data class Matrix(val rows: Int = 2, val cols: Int = 2) {
    private var matrix: Array<Array<Complex>> = arrayOf()

    constructor(rows: Int = 2, cols: Int = 2, vararg elements: Complex): this(rows, cols) {
        val e = elements.iterator()
        for(i in 0 until rows) {
            for(j in 0 until cols) {
                matrix[i][j] = e.next()
            }
        }
    }

    init {
        for(i in 0 until rows) {
            var col = arrayOf<Complex>()
            for(j in 0 until cols) {
                col += 0.R
            }
            matrix += col
        }
    }

    val size: String get() = "${rows}x${cols}"
    val elementCount: Int get() = rows * cols

    operator fun times(other: Matrix): Matrix {
        if(cols != other.rows) {
            throw InvalidParameterException("$size matrix cannot be multiplied with ${other.size} matrix.")
        }

        var myRow = 0; var myCol = 0
        var herRow = 0; var herCol = 0
        var herElementsCount = 0
        var elementsDone = 0
        var x = 0.R
        val results = Array(rows * other.cols) { 0.R }

        while(elementsDone < results.size) {
            //println("($myRow, $myCol) - ($herRow, $herCol)")
            x += matrix[myRow][myCol] * other.matrix[herRow][herCol]
            myCol += 1
            herRow += 1
            herElementsCount += 1

            if(herRow >= other.rows) {
                results[elementsDone] = x
                elementsDone += 1
                x = 0.R
                herRow = 0
                herCol += 1
                myCol = 0
                if(herElementsCount == other.rows * other.cols) {
                    myRow += 1
                    herElementsCount = 0
                    herCol = 0
                }
            }
        }
        return Matrix(rows, other.cols, *results)
    }

    operator fun times(ket: Ket): Ket {
        val r = (this * Matrix(ket.size,1, *ket.values))
        return r.toKet()
    }

    infix fun `|` (ket: Ket): Ket {
        return this * ket
    }


    fun toKet(label: String = ""): Ket {
        if(cols != 1) {
            throw TypeCastException("Unable to convert matrix with cols > 1 to a Ket vector")
        }
        val xs = matrix.map { it[0] }.toTypedArray()
        return Ket(label, *xs)
    }

    fun toArray(): Array<Complex> {
        val r = mutableListOf<Complex>()
        for(i in matrix.indices) {
            r.addAll(matrix.map { it[i] })
        }
        return r.toTypedArray()
    }


    fun conj(): Matrix {
        val r = Matrix(rows, cols)
        for(i in 0 until rows) {
            for(j in 0 until cols) {
                r.matrix[i][j] = matrix[i][j].conj()
            }
        }
        return r
    }

    fun transpose(): Matrix {
        val r = Matrix(cols, rows)
        for(i in 0 until rows) {
            for(j in 0 until cols) {
                r.matrix[j][i] = matrix[i][j].conj()
            }
        }
        return r
    }

    fun isHermitian(): Boolean {
        return equals(transpose().conj())
    }

    fun print(colWidth: Int = 3, asIntegers: Boolean = false) {
        for(row in matrix) {
            for(col in row) {
                if(asIntegers) {
                    print("${col.re.toInt()}".padStart(colWidth))
                } else {
                    print("$col".padStart(colWidth))
                }
            }
            println()
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for(row in matrix) {
            for(col in row) {
                sb.append("$col ")
            }
            sb.appendln()
        }
        return sb.toString()
    }

    override fun equals(other: Any?): Boolean =
        other is Matrix &&
        cols == other.cols &&
        rows == other.rows &&
        matrix.contentDeepEquals(other.matrix)


    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        result = 31 * result + matrix.contentDeepHashCode()
        return result
    }
}
