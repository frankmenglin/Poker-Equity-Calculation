//Class of single card in a standard poker deck (no joker)
class Card(private var rank: String?, private var suit: String?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Card

        if (rank != other.rank) return false
        if (suit != other.suit) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rank.hashCode()
        result = 31 * result + suit.hashCode()
        return result
    }

    fun getSuit() = suit
    fun getRank() = rank
    fun setSuit(newSuit: String?) {
        if (listOf("s", "h", "d", "c").contains(newSuit)) {
            suit = newSuit
        }
    }

    fun setRank(newRank: String?) {
        if (listOf("A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2").contains(newRank)) {
            rank = newRank
        }
    }

    fun getNumRank(): Int? {
        val rankMap = mapOf(
            "A" to 14, "K" to 13, "Q" to 12, "J" to 11, "T" to 10,
            "9" to 9, "8" to 8, "7" to 7, "6" to 6, "5" to 5, "4" to 4, "3" to 3, "2" to 2
        )
        return rankMap[rank]
    }//For comparison purpose
}