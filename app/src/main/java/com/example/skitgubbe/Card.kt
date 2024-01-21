package com.example.skitgubbe

class Card(val suit: Suit, val rank: Rank) {
    override fun toString(): String = "$rank of $suit"
}


enum class Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES
}

enum class Rank {
    THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE, TWO
}