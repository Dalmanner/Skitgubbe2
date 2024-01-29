package com.example.skitgubbe

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class Game {
    private val deck = Deck()
    val players: MutableList<Player> = mutableListOf()
    val pile: MutableList<Card> = mutableListOf()
    interface GameUpdateListener {
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

        val startingPlayerIndex = 0 // determineStartingPlayer()
        //playRound(startingPlayerIndex)

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
        var currentPlayerIndex = startingPlayerIndex
        while (!isGameOver(false)) {

            val currentPlayer = players[currentPlayerIndex!!]
            // Player's turn logic
            takeTurn(currentPlayer)

            // Move to the next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size
            isGameOver(true)
        }

        // Determine winner or loser at the end of the game
    }

    private fun takeTurn(player: Player) {
        var continueTurn = true
        while (continueTurn) {
            val topCardOfPile = pile.lastOrNull()
            val cardToPlay = player.chooseCardToPlay(topCardOfPile)

            if (cardToPlay != null && player.canPlayCard(cardToPlay, topCardOfPile)) {
                pile.add(cardToPlay)
                player.playCard(cardToPlay)

                //if (cardToPlay.rank == Rank.TWO) {} // We might not need this. The player continues to play, and the top card will be 2, on which any card can be played.
                if (cardToPlay.rank == Rank.TEN) {
                    pile.clear()
                }
            } else {
                player.pickUpPile(pile)
                continueTurn = false
            }

        }
        player.drawCard(deck)
    }


    private fun isGameOver(over: Boolean): Boolean {
        // return players.count { it.hasCards() } <= 1
        return over;
    }

}
