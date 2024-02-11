package com.example.skitgubbe

class AIPlayer : Player() {

    // This function should let the AI player choose a card to play from its hand,
    // a two or a ten if it has one, or null if it can't play any card, and also play a card if pile is empty
    override fun chooseCardToPlay(topCardOfPile: Card?): Card? {
        // Filter the hand for cards that are playable (higher or equal value than the top of the pile)
        if (topCardOfPile == null) {
            return handCards.minByOrNull { it.rank }
        }
        return handCards.filter { it.rank>= ((topCardOfPile.rank) as Rank) }
            .minByOrNull { it.rank }
    }

    override fun canPlayCard(card: Card, pileTopCard: Card?): Boolean {
        if (pileTopCard == null || pileTopCard.rank == Rank.TWO) {
            return true
        }
        return pileTopCard?.let { card.rank >= it.rank } ?: true
    }
}
