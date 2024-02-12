package com.example.skitgubbe

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameActivity : AppCompatActivity(), Game.GameUpdateListener {
    private lateinit var game: Game
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val playerName = intent.getStringExtra("playerName")
        val opponentType = intent.getStringExtra("opponentType")

        val drawPileImageView = findViewById<ImageView>(R.id.draw_pile)
        drawPileImageView.setOnClickListener {
            Toast.makeText(this, "$playerName: Your turn AI!", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                game.refillHandToMinimum(game.players[0])
                onUpdateGameUI()
            }
        }
        val discardPileImageView = findViewById<ImageView>(R.id.discard_pile)
        discardPileImageView.setOnClickListener {
            Toast.makeText(this, "Picked up discard pile", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                game.players[0].pickUpPile(game.discardPile)
                onUpdateGameUI()
            }
        }

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
            game = Game(this@GameActivity)
            game.gameUpdateListener = this@GameActivity
            game.setupPlayers()
            game.startGame()
        }
            withContext(Dispatchers.Main) {
                updateGameUI()
            }
            Toast.makeText(this@GameActivity, "Welcome $playerName! You are playing against $opponentType", Toast.LENGTH_SHORT).show()
            Toast.makeText(this@GameActivity, "Let´s begin, $playerName! Pick one of your three cards", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onUpdateGameUI() {
        runOnUiThread {
            updateGameUI()
            // onComplete()
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun getCardImageResource(card: Card): Int {
        val resourceName = "${card.rank.name.lowercase()}_of_${card.suit.name.lowercase()}"
        return resources.getIdentifier(resourceName, "drawable", packageName)
    }

    fun updateGameUI() {
        val playerHandLayout = findViewById<LinearLayout>(R.id.user_hand)
        val playerHand = game.players[0].handCards

        playerHandLayout.removeAllViews()

        playerHand.forEach { card -> addCardToLayout(playerHandLayout, card) }
        updateLaidOutCards(game.players[0].faceUpCards, R.id.user_laid_out1, R.id.user_laid_out2, R.id.user_laid_out3)
        updateLaidOutCards(game.players[1].faceUpCards, R.id.opponent_laid_out1, R.id.opponent_laid_out2, R.id.opponent_laid_out3)

        if (game.shouldUseFaceUpCards(game.players[0])) {
            enableFaceUpCardsForPlay()
        }

        val drawPileImageView = findViewById<ImageView>(R.id.draw_pile)
        if (game.drawPile.isNotEmpty()) {
            drawPileImageView.setImageResource(R.drawable.card_back)
            drawPileImageView.visibility = ImageView.VISIBLE
        } else {
            drawPileImageView.visibility = ImageView.INVISIBLE
        }

        val laidCardView = findViewById<ImageView>(R.id.discard_pile)
        if (game.discardPile.isNotEmpty()) {
            game.discardPile.lastOrNull()?.let {
                laidCardView.setImageResource(getCardImageResource(it))
                laidCardView.visibility = ImageView.VISIBLE
            }
        } else {
            laidCardView.visibility = ImageView.INVISIBLE
        }
    }

    private fun enableFaceUpCardsForPlay() {
        val faceUpCardViews = arrayOf(findViewById<ImageView>(R.id.user_laid_out1), findViewById<ImageView>(R.id.user_laid_out2), findViewById<ImageView>(R.id.user_laid_out3))
        val faceUpCards = game.players[0].faceUpCards

        faceUpCardViews.forEachIndexed { index, imageView ->
            if (index < game.players[0].faceUpCards.size) {
                val card = faceUpCards[index]
                imageView.setOnClickListener{
                    lifecycleScope.launch {
                        playCardFromFaceUpCards(card, game.players[0])
                        onUpdateGameUI()
                    }
                }
                imageView.visibility = ImageView.VISIBLE
            } else {
                imageView.visibility = ImageView.INVISIBLE
            }
        }
    }

    fun playCardFromFaceUpCards(card: Card, player: Player) {

        if (player.canPlayCard(card, game.discardPile.lastOrNull())) {
            player.faceUpCards.remove(card)
            game.discardPile.add(card)
        } else {
            Toast.makeText(this, "You can't play that card", Toast.LENGTH_SHORT).show()
        }
        // Logic to play a card from face-up cards
        // Ensure to move the card from faceUpCards to the pile
        // And then refill the hand from the face-up cards if the draw pile is empty
    }

    private fun addCardToLayout(layout: LinearLayout, card: Card) {
        val cardImageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
            setImageResource(getCardImageResource(card))
            setOnClickListener {
                lifecycleScope.launch {
                    game.playCardFromHand(card)
                }
            }
        }
        layout.addView(cardImageView)
    }

    private fun updateLaidOutCards(cards: List<Card>, vararg viewIds: Int) {
        cards.forEachIndexed { index, card ->
            findViewById<ImageView>(viewIds[index])?.setImageResource(getCardImageResource(card))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

}
