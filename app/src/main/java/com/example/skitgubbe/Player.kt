package com.example.skitgubbe

open class Player {
    val handCards: MutableList<Card> = mutableListOf()
    val faceDownCards: MutableList<Card> = mutableListOf()
    val faceUpCards: MutableList<Card> = mutableListOf()

    fun drawCard(deck: Deck) {
        deck.dealCard()?.let { card ->
            handCards.add(card)
        }
    }

    // Play a card from the player's hand
    fun playCard(card: Card): Boolean {
        if (handCards.contains(card)) {
            handCards.remove(card)
            return true
        }
        // Add logic for face-up and face-down cards if needed
        return false
    }

    // Pick up the pile and add it to the player's hand
    fun pickUpPile(pile: MutableList<Card>) {
        handCards.addAll(pile)
        pile.clear()
    }

    // Check for special cards like twos or tens in the player's hand
    fun hasSpecialCard(): Boolean {
        return handCards.any { card -> card.rank == Rank.TWO || card.rank == Rank.TEN }
    }

    open fun chooseCardToPlay(topCardOfPile: Card?): Card {
        // Add logic to choose a card to play
        return handCards[0]
    }

    fun canPlayCard(cardToPlay: Card, pileTopCard: Card?): Boolean {
        if (pileTopCard == null) {
            return true
        }
        if (cardToPlay.rank == Rank.TWO || cardToPlay.rank == Rank.TEN) {
            return true
        }
        return cardToPlay.rank >= pileTopCard.rank
    }

}
