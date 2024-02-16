package com.example.skitgubbe

import android.util.Log
import android.widget.Toast


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
        //sort the hand:
        handCards.sortBy { it.rank }
        Log.d("Player", "Picked up the pile '${pile.size}' cards")
    }

    open fun chooseCardToPlay(topCardOfPile: Card?): Card? {
        if (handCards.isNotEmpty()) {
            return handCards.firstOrNull { canPlayCard(it, topCardOfPile) } ?: handCards.first()
        }
        return null
    }


    open fun canPlayCard(cardToPlay: Card, pileTopCard: Card?): Boolean {
        if (pileTopCard == null || pileTopCard.rank == Rank.TWO ){
            return true
        }
        if (cardToPlay.rank == Rank.TWO || cardToPlay.rank == Rank.TEN) {
            return true
        }
        return cardToPlay.rank >= pileTopCard.rank
    }

}
