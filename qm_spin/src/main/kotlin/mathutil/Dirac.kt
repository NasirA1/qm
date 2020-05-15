package mathutil

import java.lang.StringBuilder


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

    operator fun times(scaler: Complex): Ket {
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

operator fun Double.times(ket: Ket) = ket * this
operator fun Complex.times(ket: Ket) = ket * this

class Bra(val label: String, vararg values: Complex) : AbstractVector(arrayOf(*values)) {

    override fun toString(): String {
        val sb = StringBuilder()
        values.forEach { sb.append(" $it ") }
        return sb.toString()
    }

    fun toKet(): Ket = Ket(label, *conj())

    fun innerProduct(ket: Ket): Double = super.dotProduct(ket)
}


