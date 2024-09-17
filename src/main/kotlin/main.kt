import kotlin.math.*

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

//class Rectangle(horizontal: Double, vertical: Double) {
//    init {
//        require (horizontal > 0 && vertical > 0)
//    }
//
//    var horizontal = horizontal
//        private set
//    var vertical = vertical
//        private set
//
//    var area
//        get() = this.horizontal * this.vertical
//        set(value) {
//            val ratio = sqrt(value / this.area)
//            this.horizontal *= ratio
//            this.vertical *= ratio
//        }
//}

fun distanceFromOrigo(x: Int, y: Int, metric: (x1: Int, y1: Int, x2: Int, y2: Int) -> Double): Double {
    return metric(0, 0, x, y)
}

fun distFromOrigo(metric: (x1: Int, y1: Int, x2: Int, y2: Int) -> Double):
            (x: Int, y: Int) -> Double {
                return { x:Int, y:Int -> metric(0, 0, x, y) }
}

fun main() {
//    println(translate(4221))
//    println(4221.toString().toList().fold("") { acc, c -> acc + "-${textOfNum[c.toString().toInt()]}"}.substring(1))

//    val r = Rectangle(2.0, 3.0)
//    r.area = 71.0
//    println(r.horizontal)
//    println(r.vertical)

//    val l2metric = { x1: Int, y1: Int, x2: Int, y2: Int -> sqrdist(x1,y1,x2,y2) }
    val l1metric = { x1: Int, y1: Int, x2: Int, y2: Int -> (abs(x1-x2) + abs(y1-y2)).toDouble() }
//    distanceFromOrigo(3,4, l2metric) // 5.0
//    distanceFromOrigo(3,4,l1metric)

    val dfo1 = distFromOrigo(l1metric)
    dfo1(3,4) // 7
//    val dfo2 = distFromOrigo(l2metric)
//    dfo2(3,4) // 5
    val dfoinf = distFromOrigo({x1: Int, y1:Int, x2: Int, y2: Int ->
        max(abs(x1-x2),abs(y1-y2)).toDouble()})
    dfoinf(3, 4) // 4
}

