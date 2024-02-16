package com.example.skitgubbe

class AIPlayer : Player() {

         override fun chooseCardToPlay(topCardOfPile: Card?): Card? {
             //shouldUseFaceUpCards:
            if (topCardOfPile?.rank == Rank.TWO) {
                return handCards.filter { it.rank != Rank.TWO && it.rank != Rank.TEN }
                    .minByOrNull { it.rank }
                    ?: handCards.minByOrNull { it.rank }
            }
            val validCards = handCards.filter { topCardOfPile == null || it.rank >= topCardOfPile.rank }
            val bestChoice = validCards.minByOrNull { it.rank }

            if (bestChoice == null) {
                val specialCards = handCards.filter { it.rank == Rank.TWO || it.rank == Rank.TEN }
                return specialCards.minByOrNull { it.rank }
            }
            return bestChoice
        }
    override fun canPlayCard(card: Card, pileTopCard: Card?): Boolean {
        if (pileTopCard == null || pileTopCard.rank == Rank.TWO) {
            return true
        }
        return card.rank >= pileTopCard.rank || card.rank == Rank.TWO || card.rank == Rank.TEN
    }
}

