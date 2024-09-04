class Fraction(
    numerator: Int,
    denominator: Int,
    private val sign: Int = 1): Comparable<Fraction>
{
    var numerator: Int = 0
        private set
    var denominator: Int = 0
        private set

    init {
        require (numerator > 0) { "Numerator can't be negative" }
        require (denominator > 0) { "Denominator can't be negative" }
        require(sign == 1 || sign == -1) { "Sign can only be 1 or -1" }

        val gcd = getGCD(numerator, denominator)

        this.numerator = numerator / gcd
        this.denominator = denominator / gcd

//        simplify()
    }

    private fun getGCD(x: Int, y: Int): Int = if (y == 0) x else getGCD(y, x % y)

//    fun simplify() {
//        val gcd = getGCD(numerator, denominator)
//        numerator /= gcd
//        denominator /= gcd
//    }

    fun add(other: Fraction): Fraction {
        var newNumerator = sign*numerator*other.denominator + denominator*other.numerator
        var newDenominator = denominator*other.denominator

        if (newNumerator < 0) return Fraction(-numerator, newDenominator, -1)
        else return Fraction(newNumerator, newDenominator)
    }

    fun mult(other: Fraction) {
        numerator *= other.numerator
        denominator *= other.denominator
    }

    fun div(other: Fraction) {
        numerator *= other.denominator
        denominator *= other.numerator
    }

    fun intPart(): Int = numerator / denominator

    override fun compareTo(other: Fraction): Int { return 0 }
    override fun toString(): String = "${if (sign == -1) "-" else ""}$numerator/$denominator"

}

fun main() {
    val a = Fraction(1,2,-1)
    println(a)
    println(a.add(Fraction(1,3)))
//    println(a.mult(Fraction(5,2, -1)))
//    println(a.div(Fraction(2,1)))
//    println(-Fraction(1,6) + Fraction(1,2))
//    println(Fraction(2,3) * Fraction(3,2))
//    println(Fraction(1,2) > Fraction(2,3))
}