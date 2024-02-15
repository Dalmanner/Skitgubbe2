package com.example.skitgubbe

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toast.*


class Game(private val context: Context) {
    private val deck = Deck()
    val players: MutableList<Player> = mutableListOf()
    val discardPile: MutableList<Card> = mutableListOf()
    val drawPile: MutableList<Card> = mutableListOf()
    interface GameUpdateListener {
        //fun onUpdateGameUI(onComplete: () -> Unit)
        fun onUpdateGameUI()
    }

    var gameUpdateListener: GameUpdateListener? = null

    fun setupPlayers() {
        players.add(Player()) // Assuming this is the human player
        players.add(AIPlayer()) // This should be the AI player
    }

    private fun dealInitialCards() {
        players.forEach { player ->
            repeat(3) {
                player.faceDownCards.add(deck.dealCard()!!)
                player.faceUpCards.add(deck.dealCard()!!)
                player.handCards.add(deck.dealCard()!!)
            }
        }
    }

    fun startGame() {
        deck.shuffle()
        dealInitialCards()
        //send rest of cards to drawPile
        drawPile.addAll(deck.cards)
        gameUpdateListener?.onUpdateGameUI()

    }

    fun playCardFromHand(card: Card) {
        val currentPlayer = players[0]
        if (currentPlayer.canPlayCard(card, discardPile.lastOrNull())) {
            currentPlayer.playCard(card)
            discardPile.add(card)
            gameUpdateListener?.onUpdateGameUI()
            if (card.rank == Rank.TEN) {
                discardPile.clear()
                gameUpdateListener?.onUpdateGameUI()}

        } else {
            Toast.makeText(context, "You can't play this card", LENGTH_SHORT).show()
        }
    }

    fun refillHandToMinimum(player: Player) {
        while (player.handCards.size < 3 && deck.hasCards()) {
            //remove from drawPile and add to handCards
            player.drawCard(deck)
            drawPile.removeAt(0)
            makeText(context, "Cards left in draw pile: " + drawPile.size, LENGTH_SHORT).show()
        }
        gameUpdateListener?.onUpdateGameUI() // Notify the UI to update after drawing cards
        if (player !is AIPlayer){
            aiPlayerTakeTurn()
        }
    }

    private fun playRound(startingPlayerIndex: Int?) {
        // Main game loop
       // coroutineScope.launch {
            var currentPlayerIndex = startingPlayerIndex
            while (!isGameOver(false)) {

                val currentPlayer = players[currentPlayerIndex!!]
                // Player's turn logic
                //takeTurn(currentPlayer)

                // Move to the next player
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size
                isGameOver(true)
            }
        // Determine winner or loser at the end of the game
        if (players[0].handCards.size == 0) {
            Toast.makeText(context, "You won!", LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "You lost!", LENGTH_SHORT).show()
        }
    }

    suspend fun takeTurn(player: Player) {
        var continueTurn = true
        while (continueTurn) {
          /*  suspendCoroutine<Unit> { continuation ->
                gameUpdateListener?.onUpdateGameUI {
                    continuation.resume(Unit)
                }
            } */
            val topCardOfPile = discardPile.lastOrNull()
            val cardToPlay = player.chooseCardToPlay(topCardOfPile)

            if (cardToPlay != null && player.canPlayCard(cardToPlay, topCardOfPile)) {
                discardPile.add(cardToPlay)
                player.playCard(cardToPlay)
                if (cardToPlay.rank == Rank.TEN) {
                    discardPile.clear()

                }
                // Draw a new card if the deck has cards left
                if (deck.hasCards()) {
                    player.drawCard(deck)
                }
            } else {
                player.pickUpPile(discardPile)
                continueTurn = false
            }
        }

        // Ensure the player always has at least three cards if possible
        while (player.handCards.size < 3 && deck.hasCards()) {
            player.drawCard(deck)
        }

        gameUpdateListener?.onUpdateGameUI()

    }
    fun aiPlayerTakeTurn() {
        makeText(context, "AI's turn, thinking...", LENGTH_SHORT).show()

        val aiPlayer = players[1] as? AIPlayer

        aiPlayer?.let {
            val topCardOfPile = discardPile.lastOrNull()
            Thread.sleep(5000)
            val cardToPlay = aiPlayer.chooseCardToPlay(topCardOfPile)//or null)

            makeText(context, "AI chose to play $cardToPlay", LENGTH_SHORT).show()

            if (cardToPlay != null) {
                discardPile.add(cardToPlay)
                aiPlayer.playCard(cardToPlay)
                //check if there is another card of the same Rank as chosen in hand and play that as well:
                val checkForSameRank = aiPlayer.handCards.filter { it.rank == cardToPlay?.rank }
                if (checkForSameRank.isNotEmpty()) {
                    checkForSameRank.forEach { additionalCard ->
                        aiPlayer.playCard(additionalCard)
                        discardPile.add(additionalCard)
                    }
                }
                if (cardToPlay.rank == Rank.TEN) {
                    discardPile.clear()
                    gameUpdateListener?.onUpdateGameUI()
                    aiPlayerTakeTurn()
                }
                if (cardToPlay.rank == Rank.TWO) {
                    gameUpdateListener?.onUpdateGameUI()
                    aiPlayerTakeTurn()}

                if (topCardOfPile != null) {
                    aiPlayer.handCards.filter { card -> card.rank == topCardOfPile.rank && card != topCardOfPile }.forEach { additionalCard ->
                        aiPlayer.playCard(additionalCard)
                        discardPile.add(additionalCard)
                    }
                }
                refillHandToMinimum(aiPlayer)
            } else {

                aiPlayer.pickUpPile(discardPile)
                makeText(context, "AI picked up the pile", LENGTH_SHORT).show()
                makeText(context," " + aiPlayer.handCards.map { it.rank }, LENGTH_SHORT).show()
            }
            gameUpdateListener?.onUpdateGameUI()
        }

    }

    fun shouldUseFaceUpCards(player: Player): Boolean {
        return player.handCards.isEmpty() && player.faceUpCards.isNotEmpty() && drawPile.isEmpty()
    }

    fun shouldUseFaceDownCards(player: Player): Boolean {
        return player.handCards.isEmpty() && player.faceUpCards.isEmpty() && player.faceDownCards.isNotEmpty() && drawPile.isEmpty()
    }

    //end the game if the drawPile is empty and the player has no cards left, and declare the loser to be Skitgubbe:

    fun isGameOver(shouldEndGame: Boolean): Boolean {
        if (drawPile.isEmpty() && players[0].handCards.isEmpty()) {
            if (shouldEndGame) {
                Toast.makeText(context, // Declare the loser to be Skitgubbe
                    "Skitgubbe!", LENGTH_SHORT).show()
            }
            return true
        }
        return false
    }


}
