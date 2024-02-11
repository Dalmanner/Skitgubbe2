package com.example.skitgubbe

import android.content.Context
import android.widget.Toast
import android.widget.Toast.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class Game(private val context: Context) {
    private val deck = Deck()
    val players: MutableList<Player> = mutableListOf()
    val pile: MutableList<Card> = mutableListOf()
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
        gameUpdateListener?.onUpdateGameUI()
   /*     suspendCoroutine<Unit> { continuation ->
            gameUpdateListener?.onUpdateGameUI {
                continuation.resume(Unit)
            } */

            val startingPlayerIndex = 0 // determineStartingPlayer()
            // playRound(startingPlayerIndex)


    }

    fun playCardFromHand(card: Card) {
        val currentPlayer = players[0]
        if (currentPlayer.canPlayCard(card, pile.lastOrNull())) {
            currentPlayer.playCard(card)
            pile.add(card)
            gameUpdateListener?.onUpdateGameUI()
            if (card.rank == Rank.TEN) {
                pile.clear()
                gameUpdateListener?.onUpdateGameUI()}

        } else {
            Toast.makeText(context, "You can't play this card", LENGTH_SHORT).show()
        }
    }

    fun refillHandToMinimum(player: Player) {
        while (player.handCards.size < 3 && deck.hasCards()) {
            player.drawCard(deck)
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
            val topCardOfPile = pile.lastOrNull()
            val cardToPlay = player.chooseCardToPlay(topCardOfPile)

            if (cardToPlay != null && player.canPlayCard(cardToPlay, topCardOfPile)) {
                pile.add(cardToPlay)
                player.playCard(cardToPlay)
                if (cardToPlay.rank == Rank.TEN) {
                    pile.clear()

                }
                // Draw a new card if the deck has cards left
                if (deck.hasCards()) {
                    player.drawCard(deck)
                }
            } else {
                player.pickUpPile(pile)
                continueTurn = false
            }
        }

        // Ensure the player always has at least three cards if possible
        while (player.handCards.size < 3 && deck.hasCards()) {
            player.drawCard(deck)
        }

        // Update the UI after the turn is complete
       /* suspendCoroutine<Unit> { continuation ->
            gameUpdateListener?.onUpdateGameUI {
                continuation.resume(Unit)
            }
        } */
        gameUpdateListener?.onUpdateGameUI()

    }

    // Call this method when it's time for the AIPlayer to take its turn
    fun aiPlayerTakeTurn() {
        makeText(context, "AIPlayer's turn, thinking...", LENGTH_SHORT).show()

        val aiPlayer = players[1] as? AIPlayer

        aiPlayer?.let {
            val topCardOfPile = pile.lastOrNull()
            //wait for 2 seconds
            Thread.sleep(5000)
            val cardToPlay = aiPlayer.chooseCardToPlay(topCardOfPile)//or null)
            makeText(context, "AI chose to play $cardToPlay", LENGTH_SHORT).show()

            if (cardToPlay != null) {
                pile.add(cardToPlay)
                aiPlayer.playCard(cardToPlay)
                if (cardToPlay.rank == Rank.TEN) {
                    pile.clear()
                    gameUpdateListener?.onUpdateGameUI()
                    //play another card
                    aiPlayerTakeTurn()
                }
                if (cardToPlay.rank == Rank.TWO) {
                    gameUpdateListener?.onUpdateGameUI()
                    //play another card
                    aiPlayerTakeTurn()
                }
                if (topCardOfPile != null) {
                    aiPlayer.handCards.filter { card -> card.rank == topCardOfPile.rank && card != topCardOfPile }.forEach { additionalCard ->
                        aiPlayer.playCard(additionalCard)
                        pile.add(additionalCard)
                    }
                }
                refillHandToMinimum(aiPlayer)
            } else {
                // Pick up the pile if the AIPlayer can't play a card

                aiPlayer.pickUpPile(pile)
                makeText(context, "AI picked up the pile", LENGTH_SHORT).show()
                makeText(context," " + aiPlayer.handCards.map { it.rank }, LENGTH_SHORT).show()
            }
            gameUpdateListener?.onUpdateGameUI()
        }

    }




    private fun isGameOver(over: Boolean): Boolean {
        // return players.count { it.hasCards() } <= 1
        return over;
    }

}
