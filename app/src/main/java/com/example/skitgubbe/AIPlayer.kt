package com.example.skitgubbe

class AIPlayer : Player() {

    override fun chooseCardToPlay(pileTopCard: Card?): Card {
        // Implement AI logic to choose the best card
        // For example, return the first playable card:
        return handCards.firstOrNull { canPlayCard(it, pileTopCard) } ?: handCards.first()
    }
}
