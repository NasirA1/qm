package mathutil

//data class Complex(val real: Double, val imaginary: Double) {
//
//    constructor(real: Int, imaginary: Int) : this(real.toDouble(), imaginary.toDouble())
//    constructor(real: Int, imaginary: Double) : this(real.toDouble(), imaginary)
//    constructor(real: Double, imaginary: Int) : this(real, imaginary.toDouble())
//
//    fun conjugate(): Complex = Complex(real, imaginary = -imaginary)
//    operator fun plus(other: Complex): Complex = Complex(real + other.real, imaginary + other.imaginary)
//    operator fun minus(other: Complex): Complex  = Complex(real - other.real, imaginary - other.imaginary)
//
//    operator fun times(other: Complex): Complex {
//        val real = real * other.real
//        val img1 = Complex(0, this.real * other.imaginary)
//        val img2 = Complex(0, imaginary * other.real)
//        val img3_sqr = imaginary * -other.imaginary
//        return Complex(real + img3_sqr, 0) + img1 + img2
//    }
//
//    operator fun div(other: Complex): Complex {
//        val nom = this * other.conjugate()
//        val denom = other * other.conjugate()
//        return Complex(nom.real / denom.real, nom.imaginary / denom.real)
//    }
//
//    override fun toString(): String {
//        val formattedReal = if(real.isWhole) real.toInt().toString() else real.toString()
//
//        val formattedImaginary = if(real.isWhole) {
//            val intValue = imaginary.toInt()
//            if(intValue == 1 || intValue == -1) ""
//            else intValue.toString()
//        } else imaginary.toString()
//
//        val sign = if(imaginary < 0.0) {
//            if(imaginary == -1.0) "-"
//            else ""
//        } else "+"
//
//        return formattedReal + sign + formattedImaginary + "i"
//    }
//}
//
//val Double.isWhole: Boolean get() = this % 1 == 0.0
//
//val Double.i: Complex get() = Complex(0, this)
//val Int.i: Complex get() = Complex(0, this)
//
//operator fun Double.plus(z: Complex): Complex = Complex(this + z.real, z.imaginary)
//operator fun Int.plus(z: Complex): Complex = toDouble().plus(z)
//
//operator fun Double.minus(z: Complex): Complex = Complex(this, -z.imaginary)
//operator fun Int.minus(z: Complex): Complex = toDouble().minus(z)
//
//operator fun Double.times(z: Complex): Complex = Complex(this * z.real, this * z.imaginary)
//operator fun Int.times(z: Complex): Complex = Complex(this * z.real, this * z.imaginary)
//
//fun List<Complex>.mathutil.sum(): Complex {
//    var result = Complex(0, 0)
//    forEach { result += it }
//    return result
//}
//
//fun Array<Complex>.mathutil.sum(): Complex = toList().mathutil.sum()
