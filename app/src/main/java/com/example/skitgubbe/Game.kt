package com.example.skitgubbe

class Game {
    private val deck = Deck()
    private val players: MutableList<Player> = mutableListOf()
    private val pile: MutableList<Card> = mutableListOf()

    init {
        setupPlayers()
        deck.shuffle()
        dealInitialCards()
    }

    private fun setupPlayers() {
        // Create player instances and add them to the players list
        // For example, for a 4-player game:
        repeat(4) {
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
        val startingPlayerIndex = determineStartingPlayer()
        playRound(startingPlayerIndex)
    }

    fun determineStartingPlayer(): Int {
        var playersWithHighestCard: MutableList<Int> = mutableListOf()
        var highestCardRank = Rank.TWO

        do {
            val drawnCards = players.map { it -> deck.dealCard()!! }
            println("Drawn cards: ${drawnCards.joinToString()}") // For debugging

            drawnCards.forEachIndexed { index, card ->
                if (card.rank > highestCardRank) {
                    highestCardRank = card.rank
                    playersWithHighestCard.clear()
                    playersWithHighestCard.add(index)
                } else if (card.rank == highestCardRank) {
                    playersWithHighestCard.add(index)
                }
            }

            if (playersWithHighestCard.size > 1) {
                println("Tie between players: ${playersWithHighestCard.joinToString()} - Redrawing")
                highestCardRank = Rank.TWO
            }
        } while (playersWithHighestCard.size > 1)

        return playersWithHighestCard.first()
    }

    private fun playRound(startingPlayerIndex: Int) {
        // Main game loop
        var currentPlayerIndex = startingPlayerIndex
        while (!isGameOver()) {
            val currentPlayer = players[currentPlayerIndex]
            // Player's turn logic
            takeTurn(currentPlayer)

            // Move to the next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size
        }

        // Determine winner or loser at the end of the game
    }

    private fun takeTurn(player: Player) {
        var continueTurn = true
        while (continueTurn) {
            val topCardOfPile = pile.lastOrNull()
            val cardToPlay = player.chooseCardToPlay(topCardOfPile)

            if (cardToPlay != null && player.canPlayCard(cardToPlay, pile.last())) {
                pile.add(cardToPlay)
                player.playCard(cardToPlay)

                if (cardToPlay.rank == Rank.TWO) { } // We might not need this. The player continues to play, and the top card will be 2, on which any card can be played.
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


    private fun isGameOver(): Boolean {
        // return players.count { it.hasCards() } <= 1
        return true
    }

    private fun compareCards(card1: Card, card2: Card): Int {
        // Compare cards based on their rank
        return card1.rank.compareTo(card2.rank)
    }
}
