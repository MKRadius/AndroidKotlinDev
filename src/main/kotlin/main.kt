val textOfNum: Map<Int, String> = mapOf(
    0 to "zero",
    1 to "one",
    2 to "two",
    3 to "three",
    4 to "four",
    5 to "five",
    6 to "six",
    7 to "seven",
    8 to "eight",
    9 to "nine",
)

fun translate(num: Int): String? {
    if (num < 10) return textOfNum[num]
    return "${translate(num / 10)}-" + textOfNum[num % 10]
}

fun main() {
    println(translate(4221))
}