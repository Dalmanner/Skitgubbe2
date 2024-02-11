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

//A two permits any card after, and a ten clears the pile

//A player can play a card if it is equal or higher than the top card of the pile
//A player can play a two or a ten at any time
//A player can play multiple cards of the same value at the same time
//A player can play a card of the same value as the top card of the pile at any time


