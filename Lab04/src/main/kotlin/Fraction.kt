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
    }

    fun getGCD(x: Int, y: Int): Int = if (y == 0) x else getGCD(y, x % y)

    fun add(other: Fraction): Fraction {
        var newNumerator = this.sign*this.numerator*other.denominator + other.sign*other.numerator*this.denominator
        var newDenominator = this.denominator * other.denominator

        if (newNumerator < 0) return Fraction(-newNumerator, newDenominator, -1)
        else return Fraction(newNumerator, newDenominator)
    }

    fun mult(other: Fraction): Fraction {
        var newNumerator = this.sign*this.numerator * other.sign*other.numerator
        var newDenominator = denominator*other.denominator

        if (newNumerator < 0) return Fraction(-newNumerator, newDenominator, -1)
        else return Fraction(newNumerator, newDenominator)
    }

    fun div(other: Fraction): Fraction {
        var newNumerator = this.sign * this.numerator * other.denominator
        var newDenominator = this.denominator * other.sign*other.numerator

        if (newNumerator < 0) return Fraction(-newNumerator, newDenominator, -1)
        else return Fraction(newNumerator, newDenominator)
    }

    fun negate() = unaryMinus()
    fun intPart(): Int = numerator / denominator
    operator fun plus(other: Fraction): Fraction = add(other)
    operator fun times(other: Fraction): Fraction = mult(other)
    operator fun unaryMinus(): Fraction = Fraction(numerator, denominator, -sign)
    override fun compareTo(other: Fraction): Int = (this.sign * this.numerator * other.denominator).compareTo(other.sign * other.numerator * this.denominator)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Fraction) return false
        return this.sign * this.numerator * other.denominator == other.sign * other.numerator * this.denominator
    }

    override fun toString(): String = "${if (sign == -1) "-" else ""}$numerator/$denominator"
}

fun main() {
    val a = Fraction(1,2,-1)
    println(a)
    println(a.add(Fraction(1,3)))
    println(a.mult(Fraction(5,2, -1)))
    println(a.div(Fraction(2,1)))
    println(-Fraction(1,6) + Fraction(1,2))
    println(Fraction(2,3) * Fraction(3,2))
    println(Fraction(1,2) > Fraction(2,3))
}