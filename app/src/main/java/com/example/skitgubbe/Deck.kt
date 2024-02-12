package com.example.skitgubbe

class Deck {
    val cards: MutableList<Card> = mutableListOf()

    init {
        for (suit in Suit.entries) {
            for (rank in Rank.entries) {
                cards.add(Card(suit, rank))
            }
        }
    }

    fun shuffle() {
        cards.shuffle()
    }

    fun dealCard(): Card? {
        if (cards.isNotEmpty()) {
            return cards.removeAt(0)
        }
        return null
    }

    fun hasCards(): Boolean {
        return cards.isNotEmpty()
    }
}
