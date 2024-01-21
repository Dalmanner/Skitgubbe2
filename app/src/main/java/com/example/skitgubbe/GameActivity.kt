package com.example.skitgubbe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Retrieve extras from intent
        val playerName = intent.getStringExtra("playerName")
        val opponentType = intent.getStringExtra("opponentType")

        // Initialize your game logic here using playerName and opponentType
    }
}