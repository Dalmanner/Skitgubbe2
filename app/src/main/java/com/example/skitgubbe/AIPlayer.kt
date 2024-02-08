package com.example.skitgubbe

class AIPlayer : Player() {

    // This function has been modified to only choose a card, additional logic for playing extra equal value cards should be handled elsewhere
    override fun chooseCardToPlay(topCardOfPile: Card?): Card? {
        // Filter the hand for cards that are playable (higher or equal value than the top of the pile)
        return handCards.filter { it.rank>= ((topCardOfPile?.rank ?: 0) as Rank) }
            .maxByOrNull { it.rank }
    }

    override fun canPlayCard(card: Card, pileTopCard: Card?): Boolean {
        // Assuming Card class has a 'value' property for simplicity
        // Implement your actual comparison logic here
        return pileTopCard?.let { card.rank >= it.rank } ?: true
    }
}
// Compare this snippet from app/src/main/java/com/example/skitgubbe/Card.kt:
