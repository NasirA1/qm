package mathutil

import sun.plugin.dom.exception.InvalidStateException
import java.lang.StringBuilder
import java.lang.UnsupportedOperationException
import java.security.InvalidParameterException


data class Matrix(val rows: Int = 2, val cols: Int = 2) {

    companion object {
        fun makeIdentityMatrix(size: Int): Matrix {
            val createRow: (Int) -> IntArray = { onePos -> IntArray(size) { if(it == onePos) 1 else 0 } }
            val ys = (0 until size).map(createRow).flatMap { it.asList() }.toIntArray()
            return Matrix(size, size, *ys)
        }
    }

    private var matrix: Array<Array<Complex>> = arrayOf()

    constructor(rows: Int = 2, cols: Int = 2, vararg elements: Complex): this(rows, cols) {
        val e = elements.iterator()
        for(i in 0 until rows) {
            for(j in 0 until cols) {
                matrix[i][j] = e.next()
            }
        }
    }

    constructor(rows: Int = 2, cols: Int = 2, vararg elements: Double): this(rows, cols, *elements.toTypedArray().map { it.R }.toTypedArray())
    constructor(rows: Int = 2, cols: Int = 2, vararg elements: Int): this(rows, cols, *elements.toTypedArray().map { it.R }.toTypedArray())

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

    val isHermitian: Boolean get() = equals(transpose().conj())

    val isUnitary: Boolean get() = transpose().conj() * this == makeIdentityMatrix(rows)

    fun determinant(): Complex = if(rows == 2 && cols == 2) {
        (matrix[0][0] * matrix[1][1]) - (matrix[0][1] * matrix[1][0])
    } else throw UnsupportedOperationException("I don't know how to calculate the determinant of rows $rows and cols $cols")

    val inverse: Matrix get() = if(rows == 2 && cols == 2) {
        val swapNegated = arrayOf(matrix[1][1], -matrix[0][1], -matrix[1][0], matrix[0][0])
        val determinant = determinant()
        if(determinant == 0.R) {
            throw InvalidStateException("Inverse not available.  Matrix is Singular, i.e. determinant is 0")
        }
        (1.0/determinant) * Matrix(2, 2, *swapNegated)
    } else throw UnsupportedOperationException("I don't know how to calculate the determinant of rows $rows and cols $cols")

    fun toArray(): Array<Complex> {
        val r = mutableListOf<Complex>()
        for(i in matrix.indices) {
            r.addAll(matrix.map { it[i] })
        }
        return r.toTypedArray()
    }

    fun toKet(label: String = ""): Ket {
        if(cols != 1) {
            throw TypeCastException("Unable to convert matrix with cols > 1 to a Ket vector")
        }
        val xs = matrix.map { it[0] }.toTypedArray()
        return Ket(label, *xs)
    }

    fun conj(): Matrix = apply { it.conj() }

    fun transpose(): Matrix {
        val r = Matrix(cols, rows)
        for(i in 0 until rows) {
            for(j in 0 until cols) {
                r.matrix[j][i] = matrix[i][j]
            }
        }
        return r
    }

    fun print(colWidth: Int = 3, formatFn: ((Complex) -> String)? = null) {
        for(row in matrix) {
            for(col in row) {
                if(formatFn != null) {
                    print(formatFn(col).padStart(colWidth))
                } else {
                    print("$col".padStart(colWidth))
                }
            }
            println()
        }
    }
    
    fun printInts(colWidth: Int = 3) = print(colWidth) { it.re.toInt().toString() }

    fun apply(fn: (Complex) -> Complex): Matrix {
        val r = Matrix(rows, cols)
        for(i in 0 until rows) {
            for(j in 0 until cols) {
                r.matrix[i][j] = fn(matrix[i][j])
            }
        }
        return r
    }


    // ================================= Operator Overloads ===================================== //

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

    operator fun times(scaler: Complex): Matrix = apply { scaler * it }


    //TODO refactor plus and minus into common function to remove duplication
    operator fun plus(other: Matrix): Matrix {
        if(cols != other.cols && rows != other.rows) {
            throw InvalidParameterException("$size matrix cannot be added to ${other.size} matrix.")
        }

        val r = Matrix(rows, cols)
        for(i in 0 until rows) {
            for(j in 0 until cols) {
                r.matrix[i][j] = matrix[i][j] + other.matrix[i][j]
            }
        }
        return r
    }

    //TODO refactor plus and minus into common function to remove duplication
    operator fun minus(other: Matrix): Matrix {
        if(cols != other.cols && rows != other.rows) {
            throw InvalidParameterException("${other.size} matrix cannot be subtracted from $size matrix.")
        }

        val r = Matrix(rows, cols)
        for(i in 0 until rows) {
            for(j in 0 until cols) {
                r.matrix[i][j] = matrix[i][j] - other.matrix[i][j]
            }
        }
        return r
    }


    // ================================= Standard overrides ===================================== //

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

    override fun equals(other: Any?): Boolean {
        return other is Matrix &&
                cols == other.cols &&
                rows == other.rows &&
                matrix.contentDeepEquals(other.matrix)
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        result = 31 * result + matrix.contentDeepHashCode()
        return result
    }
}

operator fun Complex.times(m: Matrix): Matrix = m.apply { this * it }
operator fun Double.times(m: Matrix): Matrix = m.apply { this * it }


//Tests
fun main() {

    println("m:")
    val m = Matrix(3, 2, 1, 2, 3, 4, 5, 6)
    m.printInts()
    println()

    println("m transposed:")
    m.transpose().printInts()
    println()

    println("σ(y)")
    val sigmaY = Matrix(2, 2,  0.R, -I, I, 0.R)
    sigmaY.print(5)
    println()

    println("σ(y) transposed:")
    sigmaY.transpose().print(5)
    println()

    println("σ(y) conj:")
    sigmaY.conj().print(5)
    println()

    println("σ(y) transposed conj:")
    sigmaY.transpose().conj().print(5)
    println()

    println("σ(y) isHermitian? ${sigmaY.isHermitian}")
    println("σ(y) isUnitary? ${sigmaY.isUnitary}")
    println("m isHermitian? ${m.isHermitian}")
    println("m isUnitary? ${m.isUnitary}")
    println()

    println("Arithmetic:")
    val m1 = Matrix(2, 2, 12, 1, 3, -5)
    val m2 = Matrix(2, 2, 5, 0, 3, 17)
    println("m1:")
    m1.printInts()
    println("m2:")
    m2.printInts()
    println("m1 + m2:")
    (m1 + m2).printInts()
    println()
    println("m1 - m2:")
    (m1 - m2).print(4) { it.re.toInt().toString() }
    println()
    println("m1 * m2:")
    (m1 * m2).print(4) { it.re.toInt().toString() }
    println()

    val id: Matrix = Matrix.makeIdentityMatrix(5)
    val m3 = Matrix(5, 5, *(1..25).toList().toIntArray())
    id.printInts()
    println()
    m3.printInts()
    println()
    (m3 * id).printInts()
    println()

    println("m1:")
    m1.printInts()
    println(message = "Determinant of m1: ${m1.determinant()}")
    println()

    println("Inverse of m1:")
    m1.inverse.print(7) { it.re.round(2).toString() }
    println()

    println("m1 * inverse(m1):")
    (m1 * m1.inverse).print(5) { it.re.round(2).toString() }
    println()

    println("Singular Matrix:")
    val sm = Matrix(2, 2, -6, 9, 2, -3)
    sm.printInts()
    try { sm.inverse.print() }
    catch(ex: Throwable) { println(ex.message) }
}