class FractionMutable(private var a: Int, private var b: Int, private var sign: Int = 1) {
    init {
        if (sign != 1 && sign != -1) throw IllegalArgumentException("Sign can only be 1 or -1")
        simplify()
    }

    fun getNumerator(): Int = a * sign
    fun getDenominator(): Int = b

    fun getGCD(x: Int, y: Int): Int {
        return if (y == 0) x else getGCD(y, x % y)
    }

    fun simplify() {
        val gcd = getGCD(a, b)
        a = a.div(gcd)
        b = b.div(gcd)

        if (a < 0) a = -a.also { sign = -sign }
        if (b < 0) b = -b.also { sign = -sign }
    }

    fun negate() { sign = -sign }

    fun add(f: FractionMutable) {
        a = a*f.getDenominator() + b*f.getNumerator()
        b *= f.getDenominator()
        simplify()
    }

    fun mult(f: FractionMutable) {
        a *= f.getNumerator()
        b *= f.getDenominator()
        simplify()
    }

    fun div(f: FractionMutable) {
        a *= f.getDenominator()
        b *= f.getNumerator()
        simplify()
    }

    fun intPart(): Int {
        return a.div(b)
    }

    override fun toString(): String = "${if (sign == -1) "-" else ""}$a/$b"
}