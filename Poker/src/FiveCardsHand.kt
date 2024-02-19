class FiveCardsHand(initialCards: List<Card>) {
    private var cards: List<Card>

    init {
        if (initialCards.size != 5) throw IllegalArgumentException("A poker hand must contain exactly 5 cards")

        // Check for duplicates considering both rank and suit
        val uniqueCards = initialCards.groupBy { it.getRank() to it.getSuit() }.size
        if (uniqueCards != initialCards.size) {
            throw IllegalArgumentException("Duplicate cards are not allowed in a poker hand")
        }

        // Sort the cards first by the frequency of each rank in descending order, then by rank in descending order.
        val groupedByRank = initialCards.groupBy { it.getRank() }
        val sortedByGroupSizeThenRank =
            groupedByRank.values.sortedWith(compareByDescending<List<Card>> { it.size }.thenByDescending {
                it.first().getNumRank()
            })
        cards = sortedByGroupSizeThenRank.flatten()

        // Ensures cards are in the desired order for evaluating hands
    }

    fun getCards(): List<Card> = cards.toList() // Provides an immutable copy of the cards list

    //Check if the hand is a straight, not excluding straight flush
    fun isStraight(): Boolean {
        // First, ensure all ranks are non-null
        val ranks = cards.mapNotNull { it.getNumRank() }
        if (ranks.size != cards.size) return false // Indicates one or more cards have a null rank

        // Wheel Straight check with non-null assurance
        if (ranks[0] == 14 && ranks[1] == 5 && ranks[2] == 4 && ranks[3] == 3 && ranks[4] == 2) {
            return true
        }

        // Straight check with non-null assurance
        for (i in 0 until ranks.lastIndex) {
            if (ranks[i] - ranks[i + 1] != 1) {
                return false
            }
        }
        return true
    }

    fun isFlush(): Boolean {
        // First, ensure all suits are non-null
        val suits = cards.mapNotNull { it.getSuit() }
        if (suits.size != cards.size) return false // This checks if any suit is null

        // Efficient flush check with non-null assurance
        return suits.toSet().size == 1
    }

    fun handType(): String {
        // Assumes cards are sorted by descending frequency, then by descending rank
        val ranks = cards.mapNotNull { it.getNumRank() }

        return when {
            isFlush() && isStraight() -> "Straight Flush"
            ranks[0] == ranks[3] || ranks[1] == ranks[4] -> "Quads"
            ranks[0] == ranks[2] && ranks[3] == ranks[4] || ranks[0] == ranks[1] && ranks[2] == ranks[4] -> "Full House"
            isFlush() -> "Flush"
            isStraight() -> "Straight"
            ranks[0] == ranks[2] -> "Trips"
            (ranks[0] == ranks[1] && ranks[2] == ranks[3]) || (ranks[1] == ranks[2] && ranks[3] == ranks[4]) || (ranks[0] == ranks[1] && ranks[3] == ranks[4]) -> "Two Pair"
            ranks.windowed(2, 1).any { it[0] == it[1] } -> "Pair"
            else -> "High Card"
        }
    }

    fun handTypeStrength(): Int {
        val handType = handType()

        return when (handType) {
            "Straight Flush" -> 8
            "Quads" -> 7
            "Full House" -> 6
            "Flush" -> 5
            "Straight" -> 4
            "Trips" -> 3
            "Two Pair" -> 2
            "Pair" -> 1
            else -> 0
        }
    }//Create for the purpose of hand strength comparison

    //Return 1.0, 0.5, 0.0 for win/draw/lose vs. "otherPokerHand"
    fun equityAgainst(otherPokerHand: FiveCardsHand): Double {
        val thisHandStrength = handTypeStrength()
        val otherHandStrength = otherPokerHand.handTypeStrength()

        if (thisHandStrength > otherHandStrength) {
            return 1.0
        } else if (thisHandStrength < otherHandStrength) {
            return 0.0
        } else {
            // Deal with same hand type
            val ranks = cards.mapNotNull { it.getNumRank() }
            val otherRanks = otherPokerHand.getCards().mapNotNull { it.getNumRank() }
            val wheelRanks = listOf(14, 5, 4, 3, 2)

            if (ranks == wheelRanks && otherRanks == wheelRanks) return 0.5
            if (ranks == wheelRanks) return 0.0
            if (otherRanks == wheelRanks) return 1.0

            for (i in ranks.indices) { // Corrected loop boundary
                if (ranks[i] != otherRanks[i]) {
                    return if (ranks[i] > otherRanks[i]) 1.0 else 0.0
                }
            }
            return 0.5 // Hands are exactly equal
        }
    }
}

