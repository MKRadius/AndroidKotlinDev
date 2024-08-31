class Lotto(
    val lottoRange: IntRange = 1..40,
    val n: Int = 7,
) {
    private val secretNumbers: List<Int> = pickNDistinct(lottoRange, n) ?: lottoRange.shuffled().take(n)

//    val secretNumbers: List<Int> = listOf(1, 7, 8, 9, 10, 11, 40) // hard-set secret numbers for testing purpose
//    val secretNumbers: List<Int> = listOf(6, 13, 19, 21, 24, 37, 39)
//    val secretNumbers: List<Int> = listOf(5, 10, 12, 20, 21, 25, 31)

    // A
    fun pickNDistinct(range: IntRange, n: Int): List<Int>? {
        // returns a list with n distinct ints from range
        return range.shuffled().take(n).sorted()
    }

    fun numDistinct(list: List<Int>): Int {
        // return the number of distinct ints in list
        return list.distinct().size
    }

    fun numCommon(list1: List<Int>, list2: List<Int>): Int {
        // return the number of ints in both list1 and list2
        return list1.intersect(list2).size
    }

    fun isLegalLottoGuess(guess: List<Int>, range: IntRange = lottoRange, count: Int = n): Boolean {
        // is guess legal? (consists of n different ints from range)
        return guess.distinct().size == guess.size && guess.size == count && guess.all { it -> range.contains(it) }
    }

    fun checkGuess(guess: List<Int>, secret: List<Int> = secretNumbers): Int {
        // if guess is legal return the number of ints in guess that also appear in secret, otherwise 0
        return numCommon(guess, secret)
    }
}

// B
fun readNDistinct(low: Int, high: Int, n: Int): List<Int> {
    // that reads from console a line that contains n distinct integer number
    // ranging from low and high (inclusive), separated by commas.
    // Hints: use readLine(), .split(), check .toIntOrNull(), .filterNotNull() and .all { ... }
    return readln()
        .replace(" ", "")
        .split(",")
        .toList()
        .distinct()
        .mapNotNull { it.toIntOrNull().takeIf { it in low..high } }
        .sorted()
}

fun playLotto() {
    // - generates (secret) lotto numbers (7 distinct Ints in range from 1 to 40 (inclusive).
    // - reads from the console user guess using readNDistinct() function
    // - prints the number of correctly guessed numbers
    // - lets user either continue with another round or end

    val lotto: Lotto = Lotto(1..40, 7)
    var userInput: List<Int>

    do {
        print("Give ${lotto.n} numbers from ${lotto.lottoRange.first} to ${lotto.lottoRange.last}, separated by commas: " )
        userInput = readNDistinct(lotto.lottoRange.first, lotto.lottoRange.last, lotto.n)
        println(userInput)
    }
    while (!lotto.isLegalLottoGuess(userInput))

    val computerLottoGuess = findLotto(lotto)

    println("lotto numbers: $userInput, you got ${lotto.checkGuess(userInput)} correct")
    println("computer guess in ${computerLottoGuess.first} steps is ${computerLottoGuess.second}")
}

// C
fun findLotto(lotto: Lotto): Pair<Int, List<Int>> {
    // - generate lotto guesses
    // - use only function checkGuess to check the guesses
    // - do not use the secret numbers in other way either directly or indirectly.
    // - return the number of steps taken to find the correct lotto numbers as well as the list of correct numbers as a Pair.

    var computerGuess: List<Int> = (lotto.lottoRange.first..lotto.lottoRange.last).take(lotto.n)
    val unsureList: MutableList<Int> = mutableListOf<Int>()
    val checkList: MutableList<Int> = ((lotto.n + 1)..lotto.lottoRange.last).toMutableList()
    var steps: Int = 1

    var index: Int = 0

    while (checkList.isNotEmpty() && index < lotto.n) {
        var popNum = checkList.removeFirst()
        var tempList = computerGuess.toMutableList()
        var reminder = tempList[index]
        tempList[index] = popNum

        if (lotto.checkGuess(tempList) > lotto.checkGuess(computerGuess)) { computerGuess = tempList; index++ }
        else if (lotto.checkGuess(tempList) < lotto.checkGuess(computerGuess)) index++
        else if (lotto.checkGuess(tempList) == lotto.checkGuess(computerGuess)) { tempList[index] = reminder; unsureList.add(popNum) }
        steps++
    }

    if (lotto.checkGuess(computerGuess) == lotto.n) return Pair(++steps, computerGuess.sorted())

    while (unsureList.isNotEmpty() && lotto.checkGuess(computerGuess) != lotto.n) {
        var popNum = unsureList.removeFirst()
        var tempList = computerGuess.toMutableList()
        tempList[index] = popNum

        if (lotto.checkGuess(tempList) > lotto.checkGuess(computerGuess)) { computerGuess = tempList; index++ }
        else if (lotto.checkGuess(tempList) == lotto.checkGuess(computerGuess)) { unsureList.addLast(popNum) }
        steps++
    }

    return Pair(steps, computerGuess.sorted())



// Find sureList (numbers that matches)
//    var computerGuess: List<Int> = (lotto.lottoRange.first..lotto.lottoRange.last).take(lotto.n)
//    val sureList: MutableList<Int> = mutableListOf<Int>()
//    val checkList: MutableList<Int> = ((lotto.n + 1)..lotto.lottoRange.last).toMutableList()
//    var steps: Int = 1
//
//    var index: Int = 0
//
//    while (checkList.size > 0) {
//        var testNum: Int = checkList.removeFirst()
//        var tempList = computerGuess.toMutableList()
//        tempList[0] = testNum
//
//        if (lotto.checkGuess(tempList) == lotto.checkGuess(computerGuess)) { steps++; checkList.addLast(testNum); continue }
//        else if (lotto.checkGuess(tempList) > lotto.checkGuess(computerGuess)) { steps++; computerGuess = tempList; break }
//        else if (lotto.checkGuess(tempList) < lotto.checkGuess(computerGuess)) { steps++; break }
//    }
//
//    while (checkList.size > 0) {
//        var testNum: Int = checkList.removeFirst()
//        var tempList = computerGuess.toMutableList()
//        tempList[0] = testNum
//
//        if (lotto.checkGuess(tempList) == lotto.checkGuess(computerGuess)) { steps++; sureList.add(testNum); }
//        else if (lotto.checkGuess(tempList) < lotto.checkGuess(computerGuess)) { steps++; }
//    }
//
//    index = 1
//    while (sureList.isNotEmpty()) {
//        var popNum = sureList.removeLast()
//        var tempList = computerGuess.toMutableList()
//        tempList[index] = popNum
//
//        if (lotto.checkGuess(tempList) > lotto.checkGuess(computerGuess)) { computerGuess = tempList; index++ }
//        else if (lotto.checkGuess(tempList) == lotto.checkGuess(computerGuess)) index++
//        steps++
//    }
//
//    return Pair(steps, computerGuess.sorted())


// Find sureList, replace from index 2 -> not efficient
//    var computerGuess: List<Int> = (lotto.lottoRange.first..lotto.lottoRange.last).take(lotto.n)
//    val sureList: MutableList<Int> = mutableListOf<Int>()
//    val checkList: MutableList<Int> = ((lotto.n + 1)..lotto.lottoRange.last).toMutableList()
//    var steps: Int = 1
//
//    var index: Int = 0
//
//    while (checkList.size > 0) {
//        var testNum: Int = checkList.removeFirst()
//        var tempList = computerGuess.toMutableList()
//        tempList[0] = testNum
//
//        if (lotto.checkGuess(tempList) == lotto.checkGuess(computerGuess)) { steps++; checkList.addLast(testNum); continue }
//        else if (lotto.checkGuess(tempList) > lotto.checkGuess(computerGuess)) { steps++; computerGuess = tempList; break }
//        else if (lotto.checkGuess(tempList) < lotto.checkGuess(computerGuess)) { steps++; break }
//    }
//
//    while (checkList.size > 0) {
//        var testNum: Int = checkList.removeFirst()
//        var tempList = computerGuess.toMutableList()
//        tempList[0] = testNum
//
//        if (lotto.checkGuess(tempList) == lotto.checkGuess(computerGuess)) { steps++; sureList.add(testNum); }
//        else if (lotto.checkGuess(tempList) < lotto.checkGuess(computerGuess)) { steps++; }
//    }
//
//    index = 1
//
//    while (sureList.isNotEmpty() && lotto.checkGuess(computerGuess) != lotto.n) {
//        var popNum = sureList.removeFirst()
//        var tempList = computerGuess.toMutableList()
//        tempList[index] = popNum
//
//        if (lotto.checkGuess(tempList) > lotto.checkGuess(computerGuess)) { computerGuess = tempList; index++ }
//        else if (lotto.checkGuess(tempList) == lotto.checkGuess(computerGuess)) { sureList.addLast(popNum) }
//
//        steps++
//    }
//
//    return Pair(steps, computerGuess.sorted())
}

fun main() {

//    val numList1 = lotto.pickNDistinct(1..40, 6)
//    val numList2 = lotto.pickNDistinct(20..60, 8)

//    println(numList1)
//    println(numList2)
//    println(numList1?.let { lotto.numDistinct(it) })
//    println(lotto.numCommon(numList1!!, numList2!!))
//    println(lotto.numCommon(listOf(1, 2, 3, 3), listOf(3, 4, 5)))

//    val isLegalGuess = lotto.isLegalLottoGuess(listOf(2, 2, 13, 24, 32, 38), 1..40, 6)
//    println(isLegalGuess)

//    println(lotto.readNDistinct(1, 40, 6))

    playLotto()
}