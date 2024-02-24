import kotlin.system.measureTimeMillis

class HoldemStartingHand(initialCards: List<Card>) {
    private val cards: List<Card>

    init {
        if (initialCards.size != 2) throw IllegalArgumentException("A Holdem starting hand must contain exactly 2 cards")
        val uniqueCards = initialCards.groupBy { it.getRank() to it.getSuit() }.size
        if (uniqueCards != initialCards.size) {
            throw IllegalArgumentException("Duplicate cards are not allowed in a starting hand")
        }
        cards = initialCards.sortedByDescending { it.getNumRank() }
    }

    fun getCards(): List<Card> = cards.toList()

}

//Given Holdem starting hand, and complete community board (5 cards), return the best 5 cards hand
//that could be made.

fun getBestFiveCardsHand(communityBoard: List<Card>, startingHand: HoldemStartingHand): FiveCardsHand {
    val startingHandCards = startingHand.getCards()
    val allCards = communityBoard + startingHandCards
    val allCombinations = allCards.combinations(5)

    var bestHand: FiveCardsHand = FiveCardsHand(allCombinations.first()) // Initialize with the first combination

    for (combination in allCombinations.drop(1)) { // Start comparing from the second combination
        val currentHand = FiveCardsHand(combination)
        if (currentHand.equityAgainst(bestHand) == 1.0) {
            bestHand = currentHand
        }
    }

    return bestHand
}


// Extension function to generate combinations of a List
fun <T> List<T>.combinations(k: Int): List<List<T>> {
    if (k == 0) return listOf(listOf())
    if (k > size) return emptyList()
    if (k == size) return listOf(this)
    if (k == 1) return this.map { listOf(it) }

    val combinations = mutableListOf<List<T>>()
    val tailCombinations = drop(1).combinations(k - 1)
    for (combination in tailCombinations) {
        combinations.add(listOf(this[0]) + combination)
    }
    combinations += drop(1).combinations(k)
    return combinations
}

//Hand vs. Hand Equity
fun estimateEquity(
    hand1: HoldemStartingHand,
    hand2: HoldemStartingHand,
    trials: Int = 40_000,
    printTime: Boolean = false // Add this parameter
): Pair<Double, Double> {
    val deck = buildDeck().filterNot { it in hand1.getCards() || it in hand2.getCards() }
    var wins1 = 0.0
    var wins2 = 0.0

    // Measure the simulation time
    val elapsedTime = measureTimeMillis {
        repeat(trials) {
            val communityBoard = deck.shuffled().take(5)
            val bestHand1 = getBestFiveCardsHand(communityBoard, hand1)
            val bestHand2 = getBestFiveCardsHand(communityBoard, hand2)

            when (bestHand1.equityAgainst(bestHand2)) {
                1.0 -> wins1++
                0.5 -> {
                    wins1 += 0.5; wins2 += 0.5
                } // Possibly Tie
                0.0 -> wins2++
                // Draws are not directly counted towards either hand's win rate
            }
        }
    }

    // Conditionally print the time
    if (printTime) {
        println("Monte Carlo simulation took $elapsedTime ms")
    }

    return Pair(wins1 / trials, wins2 / trials)
}

// Equity vs. Random Hands
fun estimateEquityVsRandom(
    hand1: HoldemStartingHand,
    numOfOpps: Int = 1,
    trials: Int = 40_000,
    printTime: Boolean = false
): Double {
    val deck = buildDeck().filterNot { it in hand1.getCards() }
    var winTrials = 0.0

    // Measure the simulation time
    val elapsedTime = measureTimeMillis {
        repeat(trials) {
            val shuffledDeck = deck.shuffled()
            val communityBoard = shuffledDeck.take(5)
            val bestHand1 = getBestFiveCardsHand(communityBoard, hand1)
            val oppHands = List(numOfOpps) {
                HoldemStartingHand(shuffledDeck.drop(5 + it * 2).take(2))
            }

            val bestHandsOpps = oppHands.map { getBestFiveCardsHand(communityBoard, it) }
            var ties = 0.0
            var wins = 0.0

            bestHandsOpps.forEach { oppHand ->
                val result = bestHand1.equityAgainst(oppHand)
                if (result == 1.0) {
                    wins += 1
                } else if (result == 0.5) {
                    ties += 1
                }
            }

            val totalOpponents = bestHandsOpps.size.toDouble()
            // Calculate proportion of the trial that counts as a win for Hand 1
            // Only when no lose we have non-zero score, as (fraction of) pot only awards best hand(s).
            val scoreForThisTrial = if (ties + wins == totalOpponents) {
                1.0 / (1.0 + ties)
            } else {
                0.0
            }

            winTrials += scoreForThisTrial // Aggregate the score for this trial to the total wins
        }
    }

    // Conditionally print the time
    if (printTime) {
        println("Monte Carlo simulation took $elapsedTime ms")
    }

    return winTrials / trials
}


fun buildDeck(): List<Card> {
    val ranks = listOf("A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2")
    val suits = listOf("s", "h", "d", "c")
    return ranks.flatMap { rank -> suits.map { suit -> Card(rank, suit) } }
}