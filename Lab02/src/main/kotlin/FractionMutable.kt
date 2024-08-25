class FractionMutable(private var a: Int, private var b: Int, private var sign: Int = 1) {
    init {
        val gcd = getGCD(a, b)
        a = a.div(gcd)
        b = b.div(gcd)
    }

    fun getGCD(x: Int, y: Int): Int {
        return if (y == 0) x else getGCD(y, x % y)
    }

    fun negate() {

    }

    fun add() {

    }

    fun mult() {

    }

    fun div() {

    }

    fun intPart() {

    }
}