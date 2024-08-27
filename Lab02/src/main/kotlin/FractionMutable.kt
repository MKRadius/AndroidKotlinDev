class FractionMutable(
    private var numerator: Int,
    private var denominator: Int,
    private var sign: Int = 1
) {
    init {
        require(sign == 1 || sign == -1) { "Sign can only be 1 or -1" }
        simplify()
    }

    fun getNumerator(): Int = numerator * sign
    fun getDenominator(): Int = denominator

    private fun getGCD(x: Int, y: Int): Int = if (y == 0) x else getGCD(y, x % y)

    fun simplify() {
        val gcd = getGCD(numerator, denominator)
        numerator /= gcd
        denominator /= gcd

        if (numerator < 0) numerator = -numerator.also { sign = -sign }
        if (denominator < 0) denominator = -denominator.also { sign = -sign }
    }

    fun negate() { sign = -sign }

    fun add(other: FractionMutable) {
        numerator = numerator*other.getDenominator() + denominator*other.getNumerator()
        denominator *= other.getDenominator()
        simplify()
    }

    fun mult(other: FractionMutable) {
        numerator *= other.getNumerator()
        denominator *= other.getDenominator()
        simplify()
    }

    fun div(other: FractionMutable) {
        numerator *= other.getDenominator()
        denominator *= other.getNumerator()
        simplify()
    }

    fun intPart(): Int = numerator / denominator

    override fun toString(): String = "${if (sign == -1) "-" else ""}$numerator/$denominator"
}