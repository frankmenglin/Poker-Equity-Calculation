fun testCardConstruction() {
    val bestCard = Card("A", "s")
    var success = true
    success = success && (bestCard.getRank() == "A")
    success = success && (bestCard.getSuit() == "s")
    success = success && (bestCard.getNumRank() == 14)
    bestCard.setRank("2")
    bestCard.setSuit("c")//Now the best card is a small card
    success = success && (bestCard.getRank() == "2")
    success = success && (bestCard.getSuit() == "c")
    success = success && (bestCard.getNumRank() == 2)
    if (success) {
        println("testCardConstruction success")
    } else {
        println("testCardConstruction fail")
    }
}

fun testHandTypeClassification() {
    var success = true
    val hand1 = FiveCardsHand(
        listOf(
            Card("A", "h"),
            Card("2", "h"), Card("5", "h"),
            Card("4", "h"), Card("3", "h")
        )
    )
    success = success && (hand1.handType() == "Straight Flush")

    val hand2 = FiveCardsHand(
        listOf(
            Card("A", "h"),
            Card("A", "d"), Card("5", "h"),
            Card("4", "c"), Card("K", "d")
        )
    )
    success = success && (hand2.handTypeStrength() == 1)//one pair

    val hand3 = FiveCardsHand(
        listOf(
            Card("A", "h"),
            Card("8", "h"), Card("5", "h"),
            Card("4", "h"), Card("K", "h")
        )
    )

    val hand4 = FiveCardsHand(
        listOf(
            Card("A", "d"),
            Card("K", "d"), Card("5", "d"),
            Card("4", "d"), Card("8", "d")
        )
    )
    success = success && (hand3.equityAgainst(hand4) == 0.5)
    //Both are AK854 flush, in most variants of poker it is considered tie even if suits are different

    val hand5 = FiveCardsHand(
        listOf(
            Card("9", "h"),
            Card("9", "s"), Card("9", "c"),
            Card("8", "h"), Card("8", "s")
        )
    )

    val hand6 = FiveCardsHand(
        listOf(
            Card("9", "h"),
            Card("9", "s"), Card("8", "c"),
            Card("8", "h"), Card("8", "s")
        )
    )
    success = success && (hand5.equityAgainst(hand6) == 1.0)//9s full of 8 stronger than 8s full of 9

    val hand7 = FiveCardsHand(
        listOf(
            Card("9", "h"),
            Card("8", "s"), Card("7", "c"),
            Card("6", "h"), Card("5", "s")
        )
    )

    val hand8 = FiveCardsHand(
        listOf(
            Card("A", "h"),
            Card("2", "s"), Card("5", "c"),
            Card("3", "h"), Card("4", "s")
        )
    )
    success = success && (hand7.equityAgainst(hand8) == 1.0)//9 high straight stronger than wheel straight

    val hand9 = FiveCardsHand(
        listOf(
            Card("A", "h"),
            Card("A", "s"), Card("A", "d"),
            Card("K", "h"), Card("K", "s")
        )
    )

    val hand10 = FiveCardsHand(
        listOf(
            Card("2", "h"),
            Card("2", "s"), Card("2", "c"),
            Card("2", "d"), Card("K", "s")
        )
    )

    success = success && (hand9.equityAgainst(hand10) == 0.0)//Full House weaker than Quads

    if (success) {
        println("testHandTypeClassification success")
    } else {
        println("testHandTypeClassification fail")
    }
}

fun testHoldemHand() {
    var success = true
    val pocketAces = HoldemStartingHand(listOf(Card("A", "h"), Card("A", "d")))
    val communityBoard = listOf(
        Card("A", "s"), Card("J", "h"),
        Card("T", "h"), Card("7", "h"), Card("4", "h")
    )
    val myFiveCardsHand = getBestFiveCardsHand(communityBoard, pocketAces)//Should be AJT74 all h
    success = success && (myFiveCardsHand.equityAgainst(
        FiveCardsHand(
            listOf(
                Card("A", "c"), Card("J", "c"),
                Card("T", "c"), Card("7", "c"), Card("4", "c")
            )
        )
    ) == 0.5)//Change suit to also test equity against

    if (success) {
        println("testHoldemHand success")
    } else {
        println("testHoldemHand fail")
    }
}

fun testHoldemHandEquity() {
    val pocketAces = HoldemStartingHand(listOf(Card("A", "h"), Card("A", "d")))
    val queenTenSuited = HoldemStartingHand(listOf(Card("Q", "c"), Card("T", "c")))
    val acesAgainstQTs = estimateEquity(pocketAces, queenTenSuited, 2500, printTime = true)
    println("Equity of AhAd vs QcTc after 2500 Monte Carlo trials are")
    println(acesAgainstQTs)
    println("testHoldemHandEquity success")
}

fun main() {
    testCardConstruction()
    testHandTypeClassification()
    testHoldemHand()
    testHoldemHandEquity()
}