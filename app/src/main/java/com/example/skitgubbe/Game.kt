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
        //sort the hand:
        players[0].handCards.sortBy { it.rank }
        gameUpdateListener?.onUpdateGameUI() // Notify the UI to update after drawing cards
        if (player !is AIPlayer){
            isGameOver(0)
            aiPlayerTakeTurn()
        }
    }

    fun aiPlayerTakeTurn() {
        makeText(context, "AI's turn, thinking...", LENGTH_SHORT).show()
        val imageView = ImageView(context)
        ImageView(context).setImageResource(R.drawable.poop)
        imageView.visibility = ImageView.VISIBLE

        val aiPlayer = players[1] as? AIPlayer

        aiPlayer?.let {
            val topCardOfPile = discardPile.lastOrNull()

            if (shouldUseFaceUpCards(aiPlayer)) {
                val cardToPlay = aiPlayer.faceUpCards.first()
                aiPlayer.playCard(cardToPlay)
                discardPile.add(cardToPlay)
                aiPlayer.faceUpCards.remove(cardToPlay)
                gameUpdateListener?.onUpdateGameUI()
                isGameOver(1)
                return
            }

            if (shouldUseFaceDownCards(aiPlayer)) {
                val cardToPlay = aiPlayer.faceDownCards.first()
                aiPlayer.playCard(cardToPlay)
                discardPile.add(cardToPlay)
                aiPlayer.faceDownCards.remove(cardToPlay)
                gameUpdateListener?.onUpdateGameUI()
                isGameOver(1)
                return
            }

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
                        Thread.sleep(2000)
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
                //activate a imageView to show the picked up pile
                makeText(context, "AI picked up the pile", LENGTH_SHORT).show()
                makeText(context," " + aiPlayer.handCards.map { it.rank }, LENGTH_SHORT).show()

            }
            val imageView2 = ImageView(context)
            ImageView(context).setImageResource(R.drawable.poop)
            imageView2.visibility = ImageView.INVISIBLE
            gameUpdateListener?.onUpdateGameUI()
            isGameOver(1)
        }

    }

    fun shouldUseFaceUpCards(player: Player): Boolean {
        return player.handCards.isEmpty() && player.faceUpCards.isNotEmpty() && drawPile.isEmpty()
    }

    fun shouldUseFaceDownCards(player: Player): Boolean {
        return player.handCards.isEmpty() && player.faceUpCards.isEmpty() && player.faceDownCards.isNotEmpty() && drawPile.isEmpty()
    }

    //End the game if the drawPile is empty and the current player has no cards left, and the last face down card is succesfully picked, declare the loser to be Skitgubbe:

    fun isGameOver (currentPlayer: Int) {
        if (drawPile.isEmpty() && players[currentPlayer].handCards.isEmpty() && players[currentPlayer].faceDownCards.isEmpty()) {
            //makeText if currentPlayer is 0 then AI player is Skitgubbe, else human player is Skitgubbe:
            if (currentPlayer == 0) {
                makeText(context, "AI player is Skitgubbe", LENGTH_SHORT).show()
            } else {
                makeText(context, "You are Skitgubbe", LENGTH_SHORT).show()
            }
        }
    }


}
