class Lotto {
    // A
    fun pickNDistinct(range: IntRange, n: Int): List<Int>? {
        // returns a list with n distinct ints from range
        return range.shuffled().take(n).toMutableList().sorted()
    }

    fun numDistinct(list: List<Int>): Int {
        // return the number of distinct ints in list
        return list.distinct().size
    }

    fun numCommon(list1: List<Int>, list2: List<Int>): Int {
        // return the number of ints in both list1 and list2
        return list1.intersect(list2).size
    }
//
//    fun isLegalLottoGuess(guess: List<Int>, range: IntRange = lottoRange, count: Int = n): Boolean {
//        // is guess legal? (consists of n different ints from range)
//    }
//
//    fun checkGuess(guess: List<Int>, secret: List<Int> = secretNumbers): Int {
//        // if guess is legal return the number of ints in guess that also appear in secret, otherwise 0
//    }
//
//    // B
//    fun readNDistinct(low: Int, high: Int, n: Int): List<Int> {
//        // that reads from console a line that contains n distinct integer number
//        // ranging from low and high (inclusive), separated by commas.
//        //Hints: use readLine(), .split(), check .toIntOrNull(), .filterNotNull() and .all { ... }
//    }
//
//    fun playLotto() {
//        //- generates (secret) lotto numbers (7 distinct Ints in range from 1 to 40 (inclusive).
//        // - reads from the console user guess using readNDistinct() function
//        //- prints the number of correctly guessed numbers
//        //- lets user either continue with another round or end
//    }
//
//    // C
//    fun findLotto(lotto: Lotto): Pair<Int, List<Int>> {
//        // - generate lotto guesses
//        // - use only function checkGuess to check the guesses
//        // - do not use the secret numbers in other way either directly or indirectly.
//        // - return the number of steps taken to find the correct lotto numbers as well as the list of correct numbers as a Pair.
//    }
}

fun main() {
    val lotto: Lotto = Lotto()
    val numList1 = lotto.pickNDistinct(1..40, 6)
    val numList2 = lotto.pickNDistinct(20..60, 8)
    println(numList1)
    println(numList2)
//    println(numList1?.let { lotto.numDistinct(it) })
//    println(lotto.numCommon(numList1!!, numList2!!))
}