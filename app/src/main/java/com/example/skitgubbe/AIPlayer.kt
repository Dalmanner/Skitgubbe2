package com.example.skitgubbe

class AIPlayer : Player() {

    /*override fun chooseCardToPlay(topCardOfPile: Card?): Card? {
        // First, try to find a card of equal or higher value than the top card of the pile.
        val higherOrEqualCards = handCards.filter { card ->
            topCardOfPile == null || card.rank >= topCardOfPile.rank
        }
        val bestChoice = higherOrEqualCards.minByOrNull { it.rank }

        // If no higher or equal card is found, check for special cards (two or ten).
        if (bestChoice == null) {
            val specialCards = handCards.filter { it.rank == Rank.TWO || it.rank == Rank.TEN }
            return specialCards.minByOrNull { it.rank }
        }

        return bestChoice
    }*/

         override fun chooseCardToPlay(topCardOfPile: Card?): Card? {
            // If the top card is a two, the AI can play any card since the two resets the play.
            // Try to play the lowest card possible in this scenario, excluding twos and tens to save them.
            if (topCardOfPile?.rank == Rank.TWO) {
                return handCards.filter { it.rank != Rank.TWO && it.rank != Rank.TEN }
                    .minByOrNull { it.rank }
                    ?: handCards.minByOrNull { it.rank } // Play any card if only special cards are left.
            }

            // For other scenarios, find the lowest card that is either equal or higher than the top card of the pile.
            val validCards = handCards.filter { topCardOfPile == null || it.rank >= topCardOfPile.rank }
            val bestChoice = validCards.minByOrNull { it.rank }

            // If no higher or equal card is found and the pile is not reset by a two, consider playing a special card.
            if (bestChoice == null) {
                val specialCards = handCards.filter { it.rank == Rank.TWO || it.rank == Rank.TEN }
                return specialCards.minByOrNull { it.rank }
            }

            return bestChoice
        }
    override fun canPlayCard(card: Card, pileTopCard: Card?): Boolean {
        if (pileTopCard == null || pileTopCard.rank == Rank.TWO) {
            return true // Always allow playing on an empty pile or on top of a two.
        }
        // Allow playing the card if it is of equal or greater value than the top of the pile,
        // or if it is a special card (two or ten).
        return card.rank >= pileTopCard.rank || card.rank == Rank.TWO || card.rank == Rank.TEN
    }
}

