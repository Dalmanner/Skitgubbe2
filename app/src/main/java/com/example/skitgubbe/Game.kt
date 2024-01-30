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
        repeat(2) {
            players.add(Player())
        }
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
        val currentPlayer = players[0] // TODO: Provide player dynamically, currently assuming player 0 is always the user
        if (currentPlayer.canPlayCard(card, pile.lastOrNull())) {
            currentPlayer.playCard(card)
            pile.add(card)
            gameUpdateListener?.onUpdateGameUI()
   /*         suspendCoroutine<Unit> { continuation ->
                gameUpdateListener?.onUpdateGameUI {
                    continuation.resume(Unit)
                }
            } */
        } else {
            Toast.makeText(context, "You can't play this card", LENGTH_SHORT).show()
        }
    }



    /*fun determineStartingPlayer(): Int? {
        var playersWithHighestCard: MutableList<Int> = mutableListOf()
        var highestCardRank: Rank? = null

        while (playersWithHighestCard.size != 1) {
            val drawnCards = players.map { deck.dealCard() }

            if (drawnCards.any { it == null }) {
                // Deck is empty or not enough cards to continue
                return null
            }

            println("Drawn cards: ${drawnCards.joinToString()}") // For debugging

            drawnCards.forEachIndexed { index, card ->
                card?.let {
                    if (highestCardRank == null || card.rank > highestCardRank!!) {
                        highestCardRank = card.rank
                        playersWithHighestCard.clear()
                        playersWithHighestCard.add(index)
                    } else if (card.rank == highestCardRank) {
                        playersWithHighestCard.add(index)
                    }
                }
            }

            if (playersWithHighestCard.size > 1) {
                println("Tie between players: ${playersWithHighestCard.joinToString()} - Redrawing")
                highestCardRank = null
            }
        }

        return playersWithHighestCard.firstOrNull()
    }*/


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
         //}
        // Determine winner or loser at the end of the game
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

        // Update the UI after the turn
       /* suspendCoroutine<Unit> { continuation ->
            gameUpdateListener?.onUpdateGameUI {
                continuation.resume(Unit)
            }
        } */
        gameUpdateListener?.onUpdateGameUI()

    }



    private fun isGameOver(over: Boolean): Boolean {
        // return players.count { it.hasCards() } <= 1
        return over;
    }

}
