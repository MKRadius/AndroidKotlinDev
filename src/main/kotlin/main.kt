import kotlin.math.sqrt

//val textOfNum: Map<Int, String> = mapOf(
//    0 to "zero",
//    1 to "one",
//    2 to "two",
//    3 to "three",
//    4 to "four",
//    5 to "five",
//    6 to "six",
//    7 to "seven",
//    8 to "eight",
//    9 to "nine",
//)
//
//fun translate(num: Int): String? {
//    if (num < 10) return textOfNum[num]
//    return "${translate(num / 10)}-" + textOfNum[num % 10]
//}

class Rectangle(horizontal: Double, vertical: Double) {
    init {
        require (horizontal > 0 && vertical > 0)
    }

    var horizontal = horizontal
        private set
    var vertical = vertical
        private set

    var area
        get() = this.horizontal * this.vertical
        set(value) {
            val ratio = sqrt(value / this.area)
            this.horizontal *= ratio
            this.vertical *= ratio
        }

}

fun main() {
//    println(translate(4221))
//    println(4221.toString().toList().fold("") { acc, c -> acc + "-${textOfNum[c.toString().toInt()]}"}.substring(1))

    val r = Rectangle(2.0, 3.0)

    r.area = 71.0

    println(r.horizontal)
    println(r.vertical)
}