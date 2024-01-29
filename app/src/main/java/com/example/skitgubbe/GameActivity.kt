package com.example.skitgubbe

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
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

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
            game = Game()
            game.gameUpdateListener = this@GameActivity
            game.setupPlayers()
            game.startGame()
        }
            withContext(Dispatchers.Main) {
                updateGameUI()
            }
            //game.startGame()
        }
    }

    override fun onUpdateGameUI() {
        runOnUiThread {
            updateGameUI()
        }
    }

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

        val laidCardView = findViewById<ImageView>(R.id.discard_pile)
        game.pile.lastOrNull()?.let { laidCardView.setImageResource(getCardImageResource(it)) }
    }

    private fun addCardToLayout(layout: LinearLayout, card: Card) {
        val cardImageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
            setImageResource(getCardImageResource(card))
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
        coroutineScope.cancel() // Cancel any ongoing coroutines when the activity is destroyed
    }

}
