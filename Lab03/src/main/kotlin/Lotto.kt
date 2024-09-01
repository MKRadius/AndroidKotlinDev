class Lotto(
    val lottoRange: IntRange = 1..40,
    val n: Int = 7,
) {
    private var secretNumbers: List<Int> = lottoRange.shuffled().take(n)

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

        secretNumbers = pickNDistinct(lottoRange, n) ?: lottoRange.shuffled().take(n)

        var userInput: List<Int>

        do {
            print("Give $n numbers from ${lottoRange.first} to ${lottoRange.last}, separated by commas: " )
            userInput = readNDistinct(lottoRange.first, lottoRange.last, n)
        }
        while (!isLegalLottoGuess(userInput))

        val computerLottoGuess = findLotto(this)

        println("lotto numbers: $userInput, you got ${checkGuess(userInput)} correct")
        println("computer guess in ${computerLottoGuess.first} steps is ${computerLottoGuess.second}")
    }
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

    while (unsureList.isNotEmpty() && lotto.checkGuess(computerGuess) != lotto.n) {
        var popNum = unsureList.removeFirst()
        var tempList = computerGuess.toMutableList()
        tempList[index] = popNum

        if (lotto.checkGuess(tempList) > lotto.checkGuess(computerGuess)) { computerGuess = tempList; index++ }
        else if (lotto.checkGuess(tempList) == lotto.checkGuess(computerGuess)) { unsureList.addLast(popNum) }
        steps++
    }

    return Pair(steps, computerGuess.sorted())
}

fun userContinue(): Boolean {
    while (true) {
        print("More? (Y/N): ")
        val userInput: String = readLine().toString().lowercase()

        if (userInput == "y") return true
        else if (userInput == "n") return false
        else continue
    }
}

fun main() {
    val lotto = Lotto(1..40, 7)
    do {
        lotto.playLotto()
    }
    while (userContinue())
}